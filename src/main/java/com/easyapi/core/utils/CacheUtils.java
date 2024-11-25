package com.easyapi.core.utils;


import com.easyapi.core.DocContext;
import com.easyapi.core.parser.ClassNode;
import com.easyapi.core.parser.ResponseNode;
import com.easyapi.core.parser.ControllerNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.easyapi.core.constant.CoreConstants.CACHE_FILE;

/**
 * abstract doc generator
 * <p>
 * licence Apache 2.0,AGPL-3.0, from japidoc and doc-apis originated
 **/
public class CacheUtils {

    public static List<com.easyapi.core.parser.ControllerNode> getControllerNodes(String apiVersion) {
        File apiRootPath = new File(new File(DocContext.getDocPath()).getParentFile(), apiVersion);
        if (!apiRootPath.exists()) {
            return null;
        }
        File cacheFile = new File(apiRootPath, CACHE_FILE);
        if (!cacheFile.exists()) {
            return null;
        }
        try {
            com.easyapi.core.parser.ControllerNode[] controllerNodes = Utils.jsonToObject(Utils.streamToString(new FileInputStream(cacheFile)), ControllerNode[].class);
            return Arrays.asList(controllerNodes);
        } catch (IOException ex) {
            LogUtils.error("get ControllerNodes error!!!", ex);
            return null;
        }
    }

    public static void saveControllerNodes(List<com.easyapi.core.parser.ControllerNode> controllerNodes) {
        try {
            controllerNodes.forEach(controllerNode -> {
                controllerNode.getRequestNodes().forEach(requestNode -> {
                    requestNode.setControllerNode(null);
                    requestNode.setLastRequestNode(null);
                    ResponseNode responseNode = requestNode.getResponseNode();
                    responseNode.setRequestNode(null);
                    removeLoopNode(responseNode);
                });
            });
            Utils.writeToDisk(new File(com.easyapi.core.DocContext.getDocPath(), CACHE_FILE), Utils.toJson(controllerNodes));
        } catch (Exception ex) {
            LogUtils.error("Error: saveControllerNodes fail", ex);
        }
    }

    private static void removeLoopNode(ClassNode classNode) {
        classNode.setParentNode(null);
        classNode.setGenericNodes(null);
        classNode.getChildNodes().forEach(fieldNode -> {
            fieldNode.setClassNode(null);
            if (fieldNode.getChildNode() != null) {
                removeLoopNode(fieldNode.getChildNode());
            }
        });
    }


}
