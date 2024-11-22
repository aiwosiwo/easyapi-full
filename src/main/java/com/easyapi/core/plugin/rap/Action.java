package com.easyapi.core.plugin.rap;

import java.util.HashSet;
import java.util.Set;

/**
 * action
 * <p>
 * licence Apache 2.0, from japidoc
 **/
class Action {

    private int id;
    private int disableCache;
    private String name;
    private String description;
    private String requestType = "1";
    private String requestUrl;
    private Set<com.easyapi.core.plugin.rap.Parameter> requestParameterList = new HashSet<com.easyapi.core.plugin.rap.Parameter>();
    private Set<com.easyapi.core.plugin.rap.Parameter> responseParameterList = new HashSet<com.easyapi.core.plugin.rap.Parameter>();
    private String responseTemplate;
    private Set<com.easyapi.core.plugin.rap.Page> pageList = new HashSet<com.easyapi.core.plugin.rap.Page>();
    private String remarks;

    public static Action newAction() {
        Action action = new Action();
        action.setId(-1);
        return action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDisableCache() {
        return disableCache;
    }

    public void setDisableCache(int disableCache) {
        this.disableCache = disableCache;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Set<com.easyapi.core.plugin.rap.Parameter> getRequestParameterList() {
        return requestParameterList;
    }

    public void setRequestParameterList(Set<com.easyapi.core.plugin.rap.Parameter> requestParameterList) {
        this.requestParameterList = requestParameterList;
    }

    public Set<com.easyapi.core.plugin.rap.Parameter> getResponseParameterList() {
        return responseParameterList;
    }

    public void setResponseParameterList(Set<com.easyapi.core.plugin.rap.Parameter> responseParameterList) {
        this.responseParameterList = responseParameterList;
    }

    public String getResponseTemplate() {
        return responseTemplate;
    }

    public void setResponseTemplate(String responseTemplate) {
        this.responseTemplate = responseTemplate;
    }

    public Set<com.easyapi.core.plugin.rap.Page> getPageList() {
        return pageList;
    }

    public void setPageList(Set<com.easyapi.core.plugin.rap.Page> pageList) {
        this.pageList = pageList;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
