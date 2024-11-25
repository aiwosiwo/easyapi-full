package com.easyapi.core.parser;

import com.easyapi.core.utils.Utils;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

import java.util.Arrays;

/**
 * jFinal controller parser
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class JFinalControllerParser extends AbsControllerParser {

    @Override
    protected void afterHandleMethod(RequestNode requestNode, MethodDeclaration md) {
        String methodName = md.getNameAsString();
        requestNode.setUrl(getUrl(methodName));
        md.getAnnotationByName("ActionKey").ifPresent(an -> {
            if (an instanceof SingleMemberAnnotationExpr) {
                String url = ((SingleMemberAnnotationExpr) an).getMemberValue().toString();
                requestNode.setMethod(Arrays.asList(com.easyapi.core.parser.RequestMethod.GET.name(), RequestMethod.POST.name()));
                requestNode.setUrl(Utils.removeQuotations(url));
                return;
            }
        });
    }

    private String getUrl(String methodName) {
        com.easyapi.core.parser.JFinalRoutesParser.RouteNode routeNode = JFinalRoutesParser.INSTANCE.getRouteNode(getControllerFile().getAbsolutePath());
        return routeNode == null ? "" : routeNode.basicUrl + "/" + methodName;
    }
}
