package com.hengyi.japp.esb.sms.domain;

import com.hengyi.japp.esb.core.infrastructure.persistence.xodus.BaseXodusEntity;
import jetbrains.exodus.entitystore.Entity;
import org.jzb.J;

import java.util.Date;

/**
 * 描述：
 *
 * @author jzb 2018-03-23
 */
public class SmsSend1818Log extends BaseXodusEntity {
    private String content;
    private String callException;
    private String note;

    public SmsSend1818Log(Entity entity) {
        super(entity);
        callException = entity.getBlobString("callException");
        content = entity.getBlobString("content");
    }

    public SmsSend1818Log() {
        super(null);
    }

    @Override
    public void fill(Entity entity) {
        super.fill(entity);
        if (J.nonBlank(callException)) {
            entity.setBlobString("callException", callException);
        } else {
            entity.deleteBlob("callException");
        }
        if (J.nonBlank(content)) {
            entity.setBlobString("content", content);
        } else {
            entity.deleteBlob("content");
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCallException() {
        return callException;
    }

    public void setCallException(Throwable exception) {
        setCallException(exception.getMessage());
    }

    public void setCallException(String callException) {
        this.callException = callException;
    }

    public Date getStartDateTime() {
        final long l = _getProperty("startDateTime");
        return new Date(l);
    }

    public void setStartDateTime(long startDateTime) {
        _setProperty("startDateTime", startDateTime);
    }

    public void setStartDateTime(Date startDateTime) {
        setStartDateTime(startDateTime.getTime());
    }

    public Date getEndDateTime() {
        final long l = _getProperty("endDateTime");
        return new Date(l);
    }

    public void setEndDateTime(long endDateTime) {
        _setProperty("endDateTime", endDateTime);
    }

    public void setEndDateTime(Date endDateTime) {
        setEndDateTime(endDateTime.getTime());
    }

    public boolean isSuccessed() {
        return _getProperty("successed");
    }

    public void setSuccessed(boolean successed) {
        _setProperty("successed", successed);
    }

    public String getNote() {
        return _getProperty("note");
    }

    public void setNote(String note) {
        _setProperty("note", note);
    }

}
