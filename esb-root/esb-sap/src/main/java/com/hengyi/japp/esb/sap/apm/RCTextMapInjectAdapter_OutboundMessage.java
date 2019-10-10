package com.hengyi.japp.esb.sap.apm;

import io.opentracing.propagation.TextMap;

import java.util.Iterator;
import java.util.Map;

/**
 * @author jzb 2019-06-25
 */
public class RCTextMapInjectAdapter_OutboundMessage implements TextMap {
    private final Map<String, Object> headers;

    public RCTextMapInjectAdapter_OutboundMessage(Map<String, Object> headers) {
        this.headers = headers;
    }

    @Override
    public void put(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
    }
}
