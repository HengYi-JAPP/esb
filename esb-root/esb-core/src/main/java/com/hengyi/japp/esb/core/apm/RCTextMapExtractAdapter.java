package com.hengyi.japp.esb.core.apm;

import io.opentracing.propagation.TextMap;
import io.vertx.core.eventbus.Message;

import java.util.Iterator;
import java.util.Map;

/**
 * @author jzb 2019-06-25
 */
public class RCTextMapExtractAdapter implements TextMap {
    private final Message reply;

    public RCTextMapExtractAdapter(Message reply) {
        this.reply = reply;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return reply.headers().iterator();
    }

    @Override
    public void put(String key, String value) {
        throw new UnsupportedOperationException("carrier is read-only");
    }
}
