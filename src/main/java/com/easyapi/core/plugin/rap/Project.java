package com.easyapi.core.plugin.rap;


import com.easyapi.core.DocContext;
import com.easyapi.core.IResponseWrapper;
import com.easyapi.core.ParseUtils;
import com.easyapi.core.Utils;
import com.easyapi.core.parser.ClassNode;

import java.util.*;

/**
 * project
 * <p>
 * licence Apache 2.0, from japidoc
 **/
class Project {

    private int id;
    private int userId;
    private String name;
    private Date createDate;
    private Date updateTime;
    private String introduction;
    private int workspaceModeInt;
    private String relatedIds = "";
    private int groupId;
    private int mockNum;
    private int teamId;
    private short accessType;
    private Set<Module> moduleList = new HashSet<Module>();
    private String projectData;
    private List<String> memberAccountList;
    private String version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getWorkspaceModeInt() {
        return workspaceModeInt;
    }

    public void setWorkspaceModeInt(int workspaceModeInt) {
        this.workspaceModeInt = workspaceModeInt;
    }

    public String getRelatedIds() {
        return relatedIds;
    }

    public void setRelatedIds(String relatedIds) {
        this.relatedIds = relatedIds;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getMockNum() {
        return mockNum;
    }

    public void setMockNum(int mockNum) {
        this.mockNum = mockNum;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public short getAccessType() {
        return accessType;
    }

    public void setAccessType(short accessType) {
        this.accessType = accessType;
    }

    public Set<Module> getModuleList() {
        return moduleList;
    }

    public void setModuleList(Set<Module> moduleList) {
        this.moduleList = moduleList;
    }

    public String getProjectData() {
        return projectData;
    }

    public void setProjectData(String projectData) {
        this.projectData = projectData;
    }

    public List<String> getMemberAccountList() {
        return memberAccountList;
    }

    public void setMemberAccountList(List<String> memberAccountList) {
        this.memberAccountList = memberAccountList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static Project valueOf(int id, List<com.easyapi.core.parser.ControllerNode> controllerNodeList) {
        Project project = new Project();
        project.setId(id);
        Module module = Module.newModule();
        project.getModuleList().add(module);

        for (com.easyapi.core.parser.ControllerNode controllerNode : controllerNodeList) {
            Page page = Page.newPage();
            module.getPageList().add(page);
            page.setName(controllerNode.getDescription());

            for (com.easyapi.core.parser.RequestNode requestNode : controllerNode.getRequestNodes()) {
                Action action = Action.newAction();
                action.setName(requestNode.getDescription());
                String requestUrl = requestNode.getUrl();
                action.setRequestUrl(supportRestfulUrl(requestUrl));

                List<String> methods = requestNode.getMethod();
                action.setRequestType(com.easyapi.core.plugin.rap.ActionTypeEnum.valueOf(methods.get(0)).type);

                for (com.easyapi.core.parser.ParamNode paramNode : requestNode.getParamNodes()) {
                    Parameter parameter = Parameter.newParameter();
                    if (com.easyapi.core.plugin.rap.DataType.isArrayType(paramNode.getType())) {
                        parameter.setIdentifier(getArrayIdentifier(paramNode.getName()));
                    } else {
                        parameter.setIdentifier(paramNode.getName());
                    }
                    parameter.setName(paramNode.getDescription());
                    parameter.setDataType(com.easyapi.core.plugin.rap.DataType.rapTypeOfNode(paramNode.getType()));
//                  parameter.setRemark(DataType.mockTypeOfNode(paramNode.getType()));
                    action.getRequestParameterList().add(parameter);
                }

                IResponseWrapper responseWrapper = DocContext.getResponseWrapper();
                Map<String, Object> resultMap = responseWrapper.wrapResponse(requestNode.getResponseNode());
                setResultMapToAction(resultMap, action.getResponseParameterList());

                page.getActionList().add(action);
            }
        }

        return project;
    }

    private static void setResultMapToAction(Map<String, Object> resultMap, Set<Parameter> parameterSet) {
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            Parameter parameter = Parameter.newParameter();
            parameter.setIdentifier(entry.getKey());
            if (com.easyapi.core.Utils.isValueType(entry.getValue())) {
                String uType = unifyType(entry.getValue());
                parameter.setDataType(com.easyapi.core.plugin.rap.DataType.rapTypeOfNode(uType));
                parameter.setRemark(com.easyapi.core.plugin.rap.DataType.mockValue(entry.getValue()));
                parameterSet.add(parameter);
            } else if (entry.getValue() instanceof Map) {
                parameter.setDataType(com.easyapi.core.plugin.rap.DataType.OBJECT);
                parameterSet.add(parameter);
                setResultMapToAction((Map) entry.getValue(), parameter.getParameterList());
            } else if (entry.getValue() instanceof com.easyapi.core.parser.ResponseNode) {
                com.easyapi.core.parser.ResponseNode responseNode = (com.easyapi.core.parser.ResponseNode) entry.getValue();
                if (responseNode.isList()) {
                    parameter.setIdentifier(getArrayIdentifier(entry.getKey()));
                    parameter.setDataType(com.easyapi.core.plugin.rap.DataType.ARRAY_OBJECT);
                } else {
                    parameter.setDataType(com.easyapi.core.plugin.rap.DataType.OBJECT);
                }
                parameterSet.add(parameter);
                setResponseToAction(responseNode, parameter.getParameterList());
            }
        }
    }

    private static void setResponseToAction(ClassNode responseNode, Set<Parameter> parameterSet) {
        for (com.easyapi.core.parser.FieldNode fieldNode : responseNode.getChildNodes()) {
            Parameter parameter = Parameter.newParameter();
            parameter.setName(fieldNode.getDescription());
            com.easyapi.core.parser.MockNode mockNode = fieldNode.getMockNode();

            if (com.easyapi.core.plugin.rap.DataType.isArrayType(fieldNode.getType())) {
                parameter.setIdentifier(getArrayIdentifier(fieldNode.getName()));
            } else {
                parameter.setIdentifier(fieldNode.getName());
            }
            parameter.setRemark(com.easyapi.core.plugin.rap.DataType.mockTypeOfNode(fieldNode.getType()));
            parameter.setDataType(com.easyapi.core.plugin.rap.DataType.rapTypeOfNode(fieldNode.getType()));

            // cover
            if (mockNode != null) {
                if (com.easyapi.core.Utils.isNotEmpty(mockNode.getLimit())) {
                    parameter.setIdentifier(String.format("%s|%s", fieldNode.getName(), mockNode.getLimit()));
                }
                if (Utils.isNotEmpty(mockNode.getValue())) {
                    parameter.setRemark(com.easyapi.core.plugin.rap.DataType.mockValue(mockNode.getValue()));
                }
            }

            parameterSet.add(parameter);
            if (fieldNode.getChildNode() != null) {
                setResponseToAction(fieldNode.getChildNode(), parameter.getParameterList());
            }
        }
    }

    private static String unifyType(Object value) {
        return ParseUtils.unifyType(value.getClass().getSimpleName());
    }

    private static String getArrayIdentifier(String name) {
        return String.format("%s|1-10", name);
    }

    private static String supportRestfulUrl(String url) {
        if (url.contains("{") && url.contains("}")) {
            url = "reg:" + url.replaceAll("\\{.+?\\}", ".+");
        }
        return url;
    }
}
