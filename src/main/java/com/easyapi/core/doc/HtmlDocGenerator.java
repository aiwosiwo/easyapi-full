package com.easyapi.core.doc;

import com.easyapi.core.LogUtils;
import com.easyapi.core.Resources;
import com.easyapi.core.Utils;
import com.easyapi.core.DocContext;
import com.easyapi.core.parser.ControllerNode;
import com.easyapi.core.parser.RequestNode;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.easyapi.core.constant.CoreConstants.HTML_PATH;
import static com.easyapi.core.constant.CoreConstants.SEPATOR;

/**
 * html doc generator
 * <p>
 * licence Apache 2.0,AGPL-3.0, from japidoc and easyapi originated
 **/
public class HtmlDocGenerator extends AbsDocGenerator {

    public HtmlDocGenerator() {
        super(com.easyapi.core.DocContext.controllerParser(), new HtmlControllerDocBuilder());
    }

    @Override
    void generateIndex(List<ControllerNode> controllerNodeList) {
        FileWriter docFileWriter = null;
        try {
            LogUtils.info("doc-apis generate index start !");
            final Template ctrlTemplate = getIndexTpl();
            final File docFile = new File(com.easyapi.core.DocContext.getDocPath(), "index.html");
            docFileWriter = new FileWriter(docFile);
            Map<String, Object> data = new HashMap<>();
            controllerNodeList.forEach(controllerNode -> {
                List<RequestNode> requestNodes = controllerNode.getRequestNodes();
                requestNodes.forEach(requestNode -> {
                    String codeFileUrl = requestNode.getCodeFileUrl();
                    requestNode.setCodeFileUrl(HTML_PATH + SEPATOR + codeFileUrl);
                });
            });
            data.put("controllerNodeList", controllerNodeList);
            data.put("currentApiVersion", com.easyapi.core.DocContext.getCurrentApiVersion());
            data.put("apiVersionList", com.easyapi.core.DocContext.getApiVersionList());
            data.put("projectName", com.easyapi.core.DocContext.getDocsConfig().getProjectName());
            data.put("i18n", com.easyapi.core.DocContext.getI18n());
            data.put("watermark", com.easyapi.core.DocContext.getDocsConfig().getWatermark());
            ctrlTemplate.process(data, docFileWriter);
            LogUtils.info("doc-apis generate index success !");
        } catch (TemplateException | IOException ex) {
            LogUtils.error("doc-apis generate index fail !", ex);
        } finally {
            Utils.closeSilently(docFileWriter);
        }
    }

    private Template getIndexTpl() throws IOException {
        return Resources.getFreemarkerTemplate("api-index.html.ftl");
    }
}
