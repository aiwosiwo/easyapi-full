package com.easyapi.core.parser;

/**
 * field node
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class FieldNode {

    private String name;
    private String type;
    private String description;
    private MockNode mockNode;
    private com.easyapi.core.parser.ClassNode childNode;
    private com.easyapi.core.parser.ClassNode classNode;
    private Boolean loopNode = Boolean.FALSE;
    private Boolean notNull = Boolean.FALSE;

    public Boolean getLoopNode() {
        return loopNode;
    }

    public void setLoopNode(Boolean loopNode) {
        this.loopNode = loopNode;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MockNode getMockNode() {
        return mockNode;
    }

    public void setMockNode(MockNode mockNode) {
        this.mockNode = mockNode;
    }

    public com.easyapi.core.parser.ClassNode getChildNode() {
        return childNode;
    }

    public void setChildNode(com.easyapi.core.parser.ClassNode childNode) {
        this.childNode = childNode;
    }

    public com.easyapi.core.parser.ClassNode getClassNode() {
        return classNode;
    }

    public void setClassNode(ClassNode classNode) {
        this.classNode = classNode;
    }

}
