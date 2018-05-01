package com.hengyi.japp.esb.sms.dto;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.split;

/**
 * @author jzb 2018-03-18
 */
public class SmsSendResponseDTO implements Serializable {
    private final String resp;
    private final Map<String, String> map;

    public SmsSendResponseDTO(String resp) {
        this.resp = resp;

        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (String pair : split(resp, "&")) {
            String[] s = split(pair, "=");
            if (s.length < 2) {
                continue;
            }
            String key = s[0];
            String value = s[1];
            builder.put(key, value);
        }
        this.map = builder.build();
    }

    public boolean isSuccess() {
        return "0".equals(this.map.get("result"));
    }

    public String getResp() {
        return resp;
    }

    @Override
    public String toString() {
        return resp;
    }
}
