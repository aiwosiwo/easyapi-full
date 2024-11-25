package com.easyapi.core.doc;

import lombok.Data;

/**
 * link
 * <p>
 * licence Apache 2.0, from japidoc
 **/
@Data
class Link {
    private String name;
    private String url;

    public Link(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
