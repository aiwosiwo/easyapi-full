package com.easyapi.core;

import com.easyapi.core.annotation.DocApi;
import com.easyapi.core.annotation.DocIgnore;
import com.easyapi.core.enums.ProjectType;
import com.easyapi.core.exception.ConfigException;
import com.easyapi.core.parser.JFinalControllerParser;
import com.easyapi.core.utils.*;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.util.*;

import static com.easyapi.core.constant.CoreConstants.COMMA_SIGN;

/**
 * doc context
 * <p>
 * licence Apache 2.0,AGPL-3.0, from japidoc.
 **/
public class DocContext {

    private static String projectPath;
    private static String docPath;
    @Deprecated
    private static List<String> javaSrcPaths = new ArrayList<>();
    private static DocConfig docConfig;
    private static String currentApiVersion;
    private static List<String> apiVersionList = new ArrayList<>();
    private static com.easyapi.core.parser.AbsControllerParser controllerParser;
    private static I18n i18n;
    private static List<File> controllerFiles;
    private static com.easyapi.core.IResponseWrapper responseWrapper;
    private static List<com.easyapi.core.parser.ControllerNode> lastVersionControllerNodes;
    private static List<com.easyapi.core.parser.ControllerNode> controllerNodeList;

    public static void init(DocConfig docConfig) {
        if (docConfig.projectPath == null || !new File(docConfig.projectPath).exists()) {
            throw new com.easyapi.core.exception.ConfigException(String.format("project path doesn't exists. %s", projectPath));
        }

        if (docConfig.getApiVersion() == null) {
            throw new ConfigException("api version cannot be null");
        }

        if (docConfig.getProjectName() == null) {
            docConfig.setProjectName("easyapi");
        }

        DocContext.docConfig = docConfig;
        i18n = new I18n(docConfig.getLocale());
        DocContext.currentApiVersion = docConfig.getApiVersion();
        setProjectPath(docConfig.projectPath);
        setDocPath(docConfig);
        initApiVersions();

        File logFile = getLogFile();
        if (logFile.exists()) {
            logFile.delete();
        }

        if (docConfig.getJavaSrcPaths().isEmpty()) {
            findOutJavaSrcPathsByProjectPaths();
        } else {
            javaSrcPaths.addAll(docConfig.getJavaSrcPaths());
        }

        LogUtils.info("find java src paths:  %s", javaSrcPaths);

        ProjectType projectType = findOutProjectType();
        findOutControllers(projectType);
        initLastVersionControllerNodes();
    }

    private static void initLastVersionControllerNodes() {
        File docDir = new File(docPath).getParentFile();
        File[] childDirs = docDir.listFiles();
        if (childDirs != null && childDirs.length != 0) {
            File lastVerDocDir = null;
            for (File childDir : childDirs) {
                if (childDir.isDirectory() && !currentApiVersion.equals(childDir.getName())
                        && (lastVerDocDir == null || childDir.lastModified() > lastVerDocDir.lastModified())) {
                    lastVerDocDir = childDir;
                }
            }
            if (lastVerDocDir != null) {
                lastVersionControllerNodes = CacheUtils.getControllerNodes(lastVerDocDir.getName());
            }
        }
    }

    private static void findOutJavaSrcPathsByProjectPaths() {
        if (projectPath != null) {
            String[] split = projectPath.split(COMMA_SIGN);
            Arrays.stream(split).forEach(DocContext::findOutJavaSrcPaths);
        }
    }

    private static void findOutJavaSrcPaths(String path) {
        //try to find javaSrcPaths
        File projectDir = new File(path);

        //module name maybe:
        //include 'auth:auth-redis'
        List<String> moduleNames = Utils.getModuleNames(projectDir);
        if (!moduleNames.isEmpty()) {
            for (String moduleName : moduleNames) {
                final String moduleRelativePath = moduleName.replace(":", "/");
                String javaSrcPath = findModuleSrcPath(new File(projectDir, moduleRelativePath));
                Optional.ofNullable(javaSrcPath).ifPresent(javaSrcPaths::add);
            }
        }

        // is it a simple java project?
        if (javaSrcPaths.isEmpty()) {
            String javaSrcPath = findModuleSrcPath(projectDir);
            javaSrcPaths.add(javaSrcPath);
        }
    }

    private static void initApiVersions() {
        File docDir = new File(docPath).getParentFile();
        String[] diffVersionApiDirs = docDir.list((dir, name) -> dir.isDirectory() && !name.startsWith("."));
        if (diffVersionApiDirs != null) {
            Collections.addAll(DocContext.apiVersionList, diffVersionApiDirs);
        }
    }

    private static ProjectType findOutProjectType() {

        //which mvc framework
        ProjectType projectType = null;

        if (docConfig.isSpringMvcProject()) {
            projectType = ProjectType.SPRING;
        } else if (docConfig.isJfinalProject()) {
            projectType = ProjectType.JFINAL;
        } else if (docConfig.isPlayProject()) {
            projectType = ProjectType.PLAY;
        } else if (docConfig.isGeneric()) {
            projectType = ProjectType.GENERIC;
        }

        if (projectType == null) {
            LogUtils.info("Error: project type not set");
            for (String javaSrcPath : javaSrcPaths) {
                File javaSrcDir = new File(javaSrcPath);
                if (Utils.isSpringFramework(javaSrcDir)) {
                    projectType = ProjectType.SPRING;
                } else if (Utils.isPlayFramework(new File(getProjectPath()))) {
                    projectType = ProjectType.PLAY;
                } else if (Utils.isJFinalFramework(javaSrcDir)) {
                    projectType = ProjectType.JFINAL;
                }

                if (projectType != null) {
                    return projectType;
                }
            }
        }

        projectType = projectType != null ? projectType : ProjectType.GENERIC;

        LogUtils.info("found it a %s project, tell us if we are wrong.", projectType);

        return projectType;
    }

    private static void findOutControllers(ProjectType projectType) {
        controllerFiles = new ArrayList<>();
        Set<String> controllerFileNames;

        for (String javaSrcPath : getJavaSrcPaths()) {
            LogUtils.info("start find controllers in path : %s", javaSrcPath);
            File javaSrcDir = new File(javaSrcPath);
            List<File> result = new ArrayList<>();
            switch (projectType) {
                case PLAY:
                    controllerParser = new com.easyapi.core.parser.PlayControllerParser();
                    controllerFileNames = new LinkedHashSet<>();
                    List<com.easyapi.core.parser.PlayRoutesParser.RouteNode> routeNodeList = com.easyapi.core.parser.PlayRoutesParser.INSTANCE.getRouteNodeList();

                    for (com.easyapi.core.parser.PlayRoutesParser.RouteNode node : routeNodeList) {
                        controllerFileNames.add(node.controllerFile);
                    }

                    for (String controllerFileName : controllerFileNames) {
                        controllerFiles.add(new File(controllerFileName));
                    }

                    break;
                case JFINAL:
                    controllerParser = new JFinalControllerParser();
                    controllerFileNames = new LinkedHashSet<>();
                    List<com.easyapi.core.parser.JFinalRoutesParser.RouteNode> jFinalRouteNodeList = com.easyapi.core.parser.JFinalRoutesParser.INSTANCE.getRouteNodeList();

                    for (com.easyapi.core.parser.JFinalRoutesParser.RouteNode node : jFinalRouteNodeList) {
                        controllerFileNames.add(node.controllerFile);
                    }

                    for (String controllerFileName : controllerFileNames) {
                        controllerFiles.add(new File(controllerFileName));
                    }
                    break;
                case SPRING:
                    controllerParser = new com.easyapi.core.parser.SpringControllerParser();
                    Utils.wideSearchFile(javaSrcDir, (f, name) -> f.getName().endsWith(".java") && ParseUtils.getCompilationUnit(f)
                                    .getChildNodesByType(ClassOrInterfaceDeclaration.class)
                                    .stream()
                                    .anyMatch(cd -> (cd.getAnnotationByName("Controller").isPresent()
                                            || cd.getAnnotationByName("RestController").isPresent())
                                            || cd.getAnnotationByName("RequestMapping").isPresent()
                                            || cd.getName().asString().endsWith("Controller")
                                            || cd.getName().asString().endsWith("Service")
                                            && !cd.getAnnotationByName(DocIgnore.class.getSimpleName()).isPresent())
                            , result, false);
                    controllerFiles.addAll(result);
                    break;
                default:
                    controllerParser = new com.easyapi.core.parser.GenericControllerParser();
                    Utils.wideSearchFile(javaSrcDir, (f, name) -> f.getName().endsWith(".java") && ParseUtils.getCompilationUnit(f)
                            .getChildNodesByType(ClassOrInterfaceDeclaration.class)
                            .stream()
                            .anyMatch(cd ->
                                    cd.getChildNodesByType(MethodDeclaration.class)
                                            .stream()
                                            .anyMatch(md -> md.getAnnotationByName(DocApi.class.getSimpleName()).isPresent())
                            ), result, false);
                    controllerFiles.addAll(result);
                    break;
            }
            for (File controllerFile : result) {
                LogUtils.info("find controller file : %s", controllerFile.getName());
            }
        }
    }

    private static String findModuleSrcPath(File moduleDir) {

        List<File> result = new ArrayList<>();
        Utils.wideSearchFile(moduleDir, (file, name) -> {
            if (name.endsWith(".java") && file.getAbsolutePath().contains("src")) {
                Optional<PackageDeclaration> opPackageDeclaration = ParseUtils.getCompilationUnit(file).getPackageDeclaration();
                if (opPackageDeclaration.isPresent()) {
                    String packageName = opPackageDeclaration.get().getNameAsString();
                    if (Utils.hasDirInFile(file, moduleDir, "test") && !packageName.contains("test")) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return !Utils.hasDirInFile(file, moduleDir, "test");
            }
            return false;
        }, result, true);

        if (result.isEmpty()) {
            LogUtils.warn("cannot find any java file, skip this module : " + moduleDir.getName());
            return null;
        }

        File oneJavaFile = result.get(0);
        Optional<PackageDeclaration> opPackageDeclaration = ParseUtils.getCompilationUnit(oneJavaFile).getPackageDeclaration();
        String parentPath = oneJavaFile.getParentFile().getAbsolutePath();
        if (opPackageDeclaration.isPresent()) {
            return parentPath.substring(0, parentPath.length() - opPackageDeclaration.get().getNameAsString().length());
        } else {
            return parentPath + "/";
        }
    }

    public static File getLogFile() {
        return new File(DocContext.getDocPath(), "docapis.log");
    }

    public static String getProjectPath() {
        return projectPath;
    }

    private static void setProjectPath(String projectPath) {
        if (projectPath != null) {
            DocContext.projectPath = new File(projectPath).getAbsolutePath() + "/";
        }
    }

    public static String getDocPath() {
        return docPath;
    }

    private static void setDocPath(DocConfig config) {
        if (config.docsPath == null || config.docsPath.isEmpty()) {
            config.docsPath = projectPath + "docapis";
        }

        File docDir = new File(config.docsPath, config.apiVersion);
        if (!docDir.exists()) {
            docDir.mkdirs();
        }
        DocContext.docPath = docDir.getAbsolutePath();
    }

    public static List<String> getJavaSrcPaths() {
        return javaSrcPaths;
    }

    public static File[] getControllerFiles() {
        return controllerFiles.toArray(new File[controllerFiles.size()]);
    }

    public static com.easyapi.core.parser.AbsControllerParser controllerParser() {
        return controllerParser;
    }

    public static com.easyapi.core.IResponseWrapper getResponseWrapper() {
        if (responseWrapper == null) {
            responseWrapper = new com.easyapi.core.IResponseWrapper() {
                @Override
                public Map<String, Object> wrapResponse(com.easyapi.core.parser.ResponseNode responseNode) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("code", 0);
                    resultMap.put("data", responseNode);
                    resultMap.put("msg", "success");
                    return resultMap;
                }
            };
        }
        return responseWrapper;
    }

    public static List<com.easyapi.core.parser.ControllerNode> getControllerNodeList() {
        return controllerNodeList;
    }

    static void setControllerNodeList(List<com.easyapi.core.parser.ControllerNode> controllerNodeList) {
        DocContext.controllerNodeList = controllerNodeList;
    }

    public static DocConfig getDocsConfig() {
        return DocContext.docConfig;
    }

    public static String getCurrentApiVersion() {
        return currentApiVersion;
    }

    public static List<String> getApiVersionList() {
        return apiVersionList;
    }

    public static List<com.easyapi.core.parser.ControllerNode> getLastVersionControllerNodes() {
        return lastVersionControllerNodes;
    }

    public static I18n getI18n() {
        return i18n;
    }

    static void setResponseWrapper(IResponseWrapper responseWrapper) {
        DocContext.responseWrapper = responseWrapper;
    }
}
