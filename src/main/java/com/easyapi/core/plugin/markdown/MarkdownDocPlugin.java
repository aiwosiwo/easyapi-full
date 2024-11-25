package com.easyapi.core.plugin.markdown;

import com.easyapi.core.DocContext;
import com.easyapi.core.IPluginSupport;
import com.easyapi.core.Resources;
import com.easyapi.core.utils.Utils;
import com.easyapi.core.parser.ControllerNode;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * abstract doc generator
 * <p>
 * licence Apache 2.0,AGPL-3.0, from japidoc and easyapi originated
 **/
public class MarkdownDocPlugin implements IPluginSupport {

    @Override
    public void execute(List<ControllerNode> controllerNodeList) {
        FileWriter docFileWriter = null;
        try {
            final Template ctrlTemplate = getDocTpl();
            final String docFileName = String.format("%s-%s.md", DocContext.getDocsConfig().getProjectName(), DocContext.getDocsConfig().getApiVersion());
            final File docFile = new File(DocContext.getDocPath(), docFileName);
            docFileWriter = new FileWriter(docFile);
            Map<String, Object> data = new HashMap<>();
            data.put("controllerNodes", controllerNodeList);
            data.put("currentApiVersion", DocContext.getCurrentApiVersion());
            data.put("projectName", DocContext.getDocsConfig().getProjectName());
            data.put("i18n", DocContext.getI18n());
            ctrlTemplate.process(data, docFileWriter);
        } catch (TemplateException | IOException ex) {
            ex.printStackTrace();
        } finally {
            Utils.closeSilently(docFileWriter);
        }
    }

    private Template getDocTpl() throws IOException {
        return Resources.getFreemarkerTemplate("api-doc.md.ftl");
    }
}
