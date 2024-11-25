package com.easyapi.core.http;


import com.easyapi.core.utils.Utils;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * http response
 * <p>
 * licence Apache 2.0, from japidoc
 **/
@Data
public class DHttpResponse {

    private int code;
    private InputStream stream;
    private Map<String, String> headers = new HashMap<>();

    void addHeader(String key, String value) {
        this.headers.put(key, value);
    }
    public String getHeader(String header) {
        return headers.get(header);
    }
    public String streamAsString() {
        try {
            return Utils.streamToString(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
