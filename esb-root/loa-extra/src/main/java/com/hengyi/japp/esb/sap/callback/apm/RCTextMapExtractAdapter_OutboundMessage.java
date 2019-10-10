package com.hengyi.japp.esb.sap.callback.apm;

import com.rabbitmq.client.Delivery;
import io.opentracing.propagation.TextMap;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jzb 2019-06-25
 */
public class RCTextMapExtractAdapter_OutboundMessage implements TextMap {
    private final Delivery delivery;

    public RCTextMapExtractAdapter_OutboundMessage(Delivery delivery) {
        this.delivery = delivery;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        final Map<String, String> map = delivery.getProperties().getHeaders()
                .entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
        return map.entrySet().iterator();
    }

    @Override
    public void put(String key, String value) {
        throw new UnsupportedOperationException("carrier is read-only");
    }
}
