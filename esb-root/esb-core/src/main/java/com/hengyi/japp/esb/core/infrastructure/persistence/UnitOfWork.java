package com.hengyi.japp.esb.core.infrastructure.persistence;

import com.hengyi.japp.esb.core.infrastructure.persistence.xodus.BaseXodusEntity;

/**
 * 描述：
 *
 * @author jzb 2018-03-20
 */
public interface UnitOfWork {

    <T extends BaseXodusEntity> void registerNew(T entity);

    <T extends BaseXodusEntity> void registerDirty(T entity);

    <T extends BaseXodusEntity> void registerClean(T entity);

    <T extends BaseXodusEntity> void registerDelete(T entity);

    void commit();

    <T extends BaseXodusEntity> T find(Class<T> clazz, String id);
}
