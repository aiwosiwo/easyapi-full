package com.easyapi.core.plugin.rap;

/**
 * model response
 * <p>
 * licence Apache 2.0, from japidoc
 **/
class ModelResponse {

    private int code;
    private String msg;
    private com.easyapi.core.plugin.rap.Project model;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public com.easyapi.core.plugin.rap.Project getModel() {
        return model;
    }

    public void setModel(com.easyapi.core.plugin.rap.Project model) {
        this.model = model;
    }
}
