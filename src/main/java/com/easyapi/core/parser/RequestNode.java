package com.easyapi.core.parser;


import com.easyapi.core.constant.ChangeFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * request node
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class RequestNode {

    private List<String> method = new ArrayList<>();
    private String url;
    private String methodName;
    private String description;
    private String supplement;
    private List<com.easyapi.core.parser.ParamNode> paramNodes = new ArrayList<>();
    private List<com.easyapi.core.parser.HeaderNode> header = new ArrayList<>();
    private Boolean deprecated = Boolean.FALSE;
    private com.easyapi.core.parser.ResponseNode responseNode;
    private com.easyapi.core.parser.ControllerNode controllerNode;
    private String codeFileUrl;
    private String author;
    private Byte changeFlag = ChangeFlag.SAME;
    private RequestNode lastRequestNode;

    public String getSupplement() {
        return supplement;
    }

    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }

    public String getCodeFileUrl() {
        return codeFileUrl;
    }

    public void setCodeFileUrl(String codeFileUrl) {
        this.codeFileUrl = codeFileUrl;
    }

    public RequestNode getLastRequestNode() {
        return lastRequestNode;
    }

    public void setLastRequestNode(RequestNode lastRequestNode) {
        this.lastRequestNode = lastRequestNode;
    }

    public List<String> getMethod() {
        if (method == null || method.size() == 0) {
            return Arrays.asList(com.easyapi.core.parser.RequestMethod.GET.name(), RequestMethod.POST.name());
        }
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        if (description != null) {
            description = description.replaceAll("\\r\\n", "");
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<com.easyapi.core.parser.ParamNode> getParamNodes() {
        return paramNodes;
    }

    public void setParamNodes(List<com.easyapi.core.parser.ParamNode> paramNodes) {
        this.paramNodes = paramNodes;
    }

    public List<com.easyapi.core.parser.HeaderNode> getHeader() {
        return header;
    }

    public void setHeader(List<com.easyapi.core.parser.HeaderNode> header) {
        this.header = header;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public com.easyapi.core.parser.ResponseNode getResponseNode() {
        return responseNode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setResponseNode(ResponseNode responseNode) {
        this.responseNode = responseNode;
    }

    public void addMethod(String method) {
        if (this.method.contains(method)) {
            return;
        }
        this.method.add(method);
    }

    public void addHeaderNode(HeaderNode headerNode) {
        header.add(headerNode);
    }

    public void addParamNode(com.easyapi.core.parser.ParamNode paramNode) {
        paramNodes.add(paramNode);
    }

    public com.easyapi.core.parser.ControllerNode getControllerNode() {
        return controllerNode;
    }

    public void setControllerNode(ControllerNode controllerNode) {
        this.controllerNode = controllerNode;
    }

    public com.easyapi.core.parser.ParamNode getParamNodeByName(String name) {
        for (ParamNode node : paramNodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }



    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public byte getChangeFlag() {
        return changeFlag;
    }

    public void setChangeFlag(Byte changeFlag) {
        this.changeFlag = changeFlag;
    }
}
