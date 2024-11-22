package com.easyapi.core.parser;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Arrays;

/**
 * play controller parser
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class PlayControllerParser extends AbsControllerParser {

    @Override
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
        PlayRoutesParser.RouteNode routeNode = PlayRoutesParser.INSTANCE.getRouteNode(getControllerFile(), md.getNameAsString());
        if (routeNode == null) {
            return;
        }

        String method = routeNode.method.toUpperCase();
        if ("*".equals(method)) {
            requestNode.setMethod(Arrays.asList(com.easyapi.core.parser.RequestMethod.GET.name(), com.easyapi.core.parser.RequestMethod.POST.name()));
        } else {
            requestNode.addMethod(RequestMethod.valueOf(method).name());
        }

        requestNode.setUrl(routeNode.routeUrl);

        md.getParameters().forEach(p -> {
            String paraName = p.getName().asString();
            ParamNode paramNode = requestNode.getParamNodeByName(paraName);
            if (paramNode != null) {
                p.getAnnotationByName("Required").ifPresent(r -> {
                    paramNode.setRequired(true);
                });
            }
        });
    }

}
