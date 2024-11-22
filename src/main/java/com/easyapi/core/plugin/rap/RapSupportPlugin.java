package com.easyapi.core.plugin.rap;


import com.easyapi.core.DocConfig;
import com.easyapi.core.LogUtils;
import com.easyapi.core.http.DHttpRequest;
import com.easyapi.core.http.DHttpUtils;
import com.easyapi.core.http.DHttpResponse;
import com.easyapi.core.parser.ControllerNode;

import java.io.IOException;
import java.util.*;

/**
 * rap support plugin
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class RapSupportPlugin implements com.easyapi.core.IPluginSupport {

    private String rapHost;
    private Integer projectId;
    private String cookie;

    private List<com.easyapi.core.parser.ControllerNode> controllerNodeList;

    @Override
    public void execute(List<ControllerNode> controllerNodeList) {
        this.controllerNodeList = controllerNodeList;
        postToRap();
    }

    /**
     * do post
     */
    private void postToRap() {

        DocConfig docConfig = com.easyapi.core.DocContext.getDocsConfig();
        if (controllerNodeList == null
                || controllerNodeList.isEmpty()
                || docConfig == null
                || docConfig.getRapHost() == null
                || docConfig.getRapProjectId() == null) {
            com.easyapi.core.LogUtils.warn("docs config properties miss, we don't think you want to post to rap!");
            return;
        }

        this.rapHost = docConfig.getRapHost();
        this.projectId = Integer.valueOf(docConfig.getRapProjectId());
        this.cookie = docConfig.getRapLoginCookie();

        if (!com.easyapi.core.Utils.isNotEmpty(cookie)) {
            String account = docConfig.getRapAccount();
            String password = docConfig.getRapPassword();
            com.easyapi.core.http.DHttpResponse response = doLogin(loginUrl(rapHost), account, password);
            this.cookie = response.getHeader("Set-Cookie");
        }

        Set<Module> moduleSet = getModuleList();

        com.easyapi.core.plugin.rap.ProjectForm projectForm = new com.easyapi.core.plugin.rap.ProjectForm();
        projectForm.setId(projectId);

        Set<com.easyapi.core.plugin.rap.DeleteActionFrom> deleteModuleForms = new HashSet<>(moduleSet.size());
        if (moduleSet != null && !moduleSet.isEmpty()) {
            for (Module module : moduleSet) {
                if (Module.NAME.equalsIgnoreCase(module.getName())) {
                    com.easyapi.core.plugin.rap.DeleteActionFrom delForm = new com.easyapi.core.plugin.rap.DeleteActionFrom();
                    delForm.setClassName("Module");
                    delForm.setId(module.getId());
                    deleteModuleForms.add(delForm);
                }
            }
        }
        projectForm.setDeletedObjectListData(com.easyapi.core.Utils.toJson(deleteModuleForms));

        com.easyapi.core.plugin.rap.Project project = com.easyapi.core.plugin.rap.Project.valueOf(projectId, controllerNodeList);
        projectForm.setProjectData(com.easyapi.core.Utils.toJson(project));

        postProject(projectForm);
    }

    public com.easyapi.core.http.DHttpResponse doLogin(String loginUrl, String userName, String password) {
        DHttpRequest request = new DHttpRequest();
        request.setAutoRedirect(false);
        request.setUrl(loginUrl);
        request.addParam("account", userName);
        request.addParam("password", password);
        try {
            return DHttpUtils.httpPost(request);
        } catch (IOException ex) {
            com.easyapi.core.LogUtils.error("login rap fail , userName : %s, pass : %s", userName, password);
            throw new RuntimeException(ex);
        }
    }

    private Set<Module> getModuleList() {
        try {
            com.easyapi.core.http.DHttpResponse modelResp = DHttpUtils.httpGet(queryModelUrl(rapHost, projectId));
            if (modelResp.getCode() == 200) {
                ModelResponse model = com.easyapi.core.Utils.jsonToObject(modelResp.streamAsString(), ModelResponse.class);
                return model.getModel().getModuleList();
            } else {
                com.easyapi.core.LogUtils.error("request module data fail, rapHost : %s, projectId : %s", rapHost, projectId);
                throw new RuntimeException("request module data fail , code : " + modelResp.getCode());
            }
        } catch (IOException e) {
            com.easyapi.core.LogUtils.error("get rap models fail", e);
            throw new RuntimeException(e);
        }
    }

    private void postProject(com.easyapi.core.plugin.rap.ProjectForm projectForm) {
        DHttpRequest request = new DHttpRequest();
        request.setUrl(checkInUrl(rapHost));
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(projectForm.getId()));
        params.put("projectData", projectForm.getProjectData());
        if (projectForm.getDeletedObjectListData() != null) {
            params.put("deletedObjectListData", projectForm.getDeletedObjectListData());
        }
        if (projectForm.getDescription() != null) {
            params.put("description", projectForm.getDescription());
        }
        if (projectForm.getVersionPosition() != null) {
            params.put("versionPosition", projectForm.getVersionPosition());
        }
        request.setParams(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Cookie", cookie);
        request.setHeaders(headers);

        try {
            DHttpResponse response = DHttpUtils.httpPost(request);
            if (response.getCode() == 200) {
                com.easyapi.core.LogUtils.info("post project to rap success, response : %s ", response.streamAsString());
            } else {
                com.easyapi.core.LogUtils.error("post project to rap fail !!! code : %s", response.streamAsString());
            }
        } catch (IOException e) {
            LogUtils.error("post project to rap fail", e);
            throw new RuntimeException(e);
        }
    }

    private String queryModelUrl(String host, Integer projectId) {
        return String.format("%s/api/queryModel.do?projectId=%s", host, projectId);
    }

    private String checkInUrl(String host) {
        return String.format("%s/workspace/checkIn.do", host);
    }

    private String loginUrl(String host) {
        return String.format("%s/account/doLogin.do", host);
    }
}
