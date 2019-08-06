package com.hengyi.japp.esb.core.apm;

import io.opentracing.propagation.TextMap;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.Iterator;
import java.util.Map;

/**
 * @author jzb 2019-06-25
 */
public class RCTextMapInjectAdapter implements TextMap {
    private final DeliveryOptions deliveryOptions;

    public RCTextMapInjectAdapter(DeliveryOptions deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    @Override
    public void put(String key, String value) {
        deliveryOptions.addHeader(key, value);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
    }
}
