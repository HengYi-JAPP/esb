package com.hengyi.japp.esb.core.infrastructure.persistence.xodus;

import com.github.ixtf.japp.core.J;
import com.google.common.collect.Maps;
import jetbrains.exodus.entitystore.Entity;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 描述：
 *
 * @author jzb 2018-03-20
 */
public abstract class BaseXodusEntity implements Serializable {
    protected final Map<String, Comparable> _xodusMap = Maps.newHashMap();

    public BaseXodusEntity(Entity entity) {
        Optional.ofNullable(entity)
                .ifPresent(it -> entity.getPropertyNames()
                        .stream()
                        .collect(Collectors.toMap(Function.identity(), entity::getProperty))
                );
    }

    public boolean _isNew() {
        return J.isBlank(getId());
    }

    public void fill(Entity entity) {
        _xodusMap.forEach((k, v) -> {
            if (v != null) {
                entity.setProperty(k, v);
            } else {
                entity.deleteProperty(k);
            }
        });
    }

    protected <T> T _getProperty(String key) {
        return (T) Optional.ofNullable(key)
                .map(_xodusMap::get)
                .orElse(null);
    }

    protected void _setProperty(String key, Comparable value) {
        Optional.ofNullable(key)
                .ifPresent(it -> {
                    if (value == null) {
                        _xodusMap.remove(key);
                    } else {
                        _xodusMap.put(key, value);
                    }
                });
    }

    public String getId() {
        return _getProperty("id");
    }

    public void setId(String id) {
        _setProperty("id", id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseXodusEntity that = (BaseXodusEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
