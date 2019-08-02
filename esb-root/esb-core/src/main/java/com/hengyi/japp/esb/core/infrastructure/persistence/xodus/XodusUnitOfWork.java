package com.hengyi.japp.esb.core.infrastructure.persistence.xodus;

import com.github.ixtf.japp.codec.Jcodec;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.hengyi.japp.esb.core.infrastructure.persistence.UnitOfWork;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * 描述：
 *
 * @author jzb 2018-03-20
 */
public class XodusUnitOfWork implements UnitOfWork {
    private static final Logger LOG = LoggerFactory.getLogger(XodusUnitOfWork.class);
    private final PersistentEntityStore store;
    private Set<BaseXodusEntity> newSet = Sets.newConcurrentHashSet();
    private Set<BaseXodusEntity> dirtySet = Sets.newConcurrentHashSet();
    private Set<BaseXodusEntity> deleteSet = Sets.newConcurrentHashSet();

    @Inject
    XodusUnitOfWork(PersistentEntityStore store) {
        this.store = store;
    }

    @Override
    public <T extends BaseXodusEntity> void registerNew(T entity) {
        newSet.add(entity);
    }

    @Override
    public <T extends BaseXodusEntity> void registerDirty(T entity) {
        dirtySet.add(entity);
    }

    @Override
    public <T extends BaseXodusEntity> void registerClean(T entity) {
        newSet.remove(entity);
        deleteSet.remove(entity);
    }

    @Override
    public <T extends BaseXodusEntity> void registerDelete(T entity) {
        deleteSet.add(entity);
    }

    @Override
    public void commit() {
        store.executeInTransaction(txn -> {
            newSet.stream().forEach(it -> {
                final Entity entity = txn.newEntity(it.getClass().getSimpleName());
                it.setId(Jcodec.uuid());
                it.fill(entity);
            });
            dirtySet.stream().forEach(it -> {
                final Entity entity = txn.find(it.getClass().getSimpleName(), "id", it.getId()).getFirst();
                it.fill(entity);
            });
            deleteSet.stream().forEach(it -> {
                final Entity entity = txn.find(it.getClass().getSimpleName(), "id", it.getId()).getFirst();
                entity.delete();
            });
        });
    }

    @Override
    public <T extends BaseXodusEntity> T find(Class<T> clazz, String id) {
        return store.computeInTransaction(txn -> {
            final Entity entity = txn.find(clazz.getSimpleName(), "id", id).getFirst();
            try {
                return clazz.getDeclaredConstructor(Entity.class).newInstance(entity);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOG.error("", e);
                throw new RuntimeException(e);
            }
        });
    }

}
