package com.easyapi.core.parser;

import com.easyapi.core.utils.LogUtils;
import com.easyapi.core.utils.Utils;
import com.easyapi.core.DocContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * play routes parser
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class PlayRoutesParser {

    private String routeFile;
    private String javaSrcPath;

    private List<RouteNode> routeNodeList = new ArrayList<>();

    private PlayRoutesParser() {
        this.routeFile = com.easyapi.core.DocContext.getProjectPath().concat("conf/routes");
        javaSrcPath = DocContext.getJavaSrcPaths().get(0);
        parse();
    }

    public static PlayRoutesParser INSTANCE = new PlayRoutesParser();

    private void parse() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(routeFile)));
            String lineText;
            String[] nodes;
            while ((lineText = reader.readLine()) != null) {
                lineText = lineText.trim();
                if (!Utils.isNotEmpty(lineText) || lineText.startsWith("#")) {
                    continue;
                }
                nodes = lineText.split("\\s+");
                if (nodes.length < 3 || nodes[2].matches("\\d+")) {
                    continue;
                }

                String[] actions = nodes[2].split("\\.");
                if (actions.length == 1) {
                    continue;
                }

                StringBuilder ctrlPathBuilder = new StringBuilder();
                ctrlPathBuilder.append("controllers");
                for (int i = 0; i != actions.length - 1; i++) {
                    ctrlPathBuilder.append('/');
                    ctrlPathBuilder.append(actions[i]);
                }

                String controllerFileName = getControllerFile(ctrlPathBuilder.toString());
                if (!new File(controllerFileName).exists()) {
                    continue;
                }

                RouteNode routeNode = new RouteNode(nodes[0].trim(), nodes[1].trim(), controllerFileName, actions[actions.length - 1]);

                routeNodeList.add(routeNode);
            }
        } catch (IOException e) {
            LogUtils.error("parse Play Routes Error", e);
        } finally {
            Utils.closeSilently(reader);
        }
    }

    public RouteNode getRouteNode(File controllerFile, String methodName) {
        Optional<RouteNode> routeNode = routeNodeList.stream().filter(node -> controllerFile.getAbsolutePath().equals(node.controllerFile) && node.actionMethod.equals(methodName)).findFirst();
        return routeNode.isPresent() ? routeNode.get() : null;
    }

    public List<RouteNode> getRouteNodeList() {
        return routeNodeList;
    }

    private String getControllerFile(String relativePath) {
        return javaSrcPath + relativePath + ".java";
    }

    public static class RouteNode {
        public final String method;
        public final String routeUrl;
        public final String controllerFile;
        public final String actionMethod;

        public RouteNode(String method, String routeUrl, String controllerFile, String actionMethod) {
            this.method = method;
            this.routeUrl = routeUrl;
            this.controllerFile = controllerFile;
            this.actionMethod = actionMethod;
        }
    }
}
