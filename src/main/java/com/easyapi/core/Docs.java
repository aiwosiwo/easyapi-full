package com.easyapi.core;

import com.easyapi.core.doc.HtmlDocGenerator;
import com.easyapi.core.plugin.rap.RapSupportPlugin;
import com.easyapi.core.utils.CacheUtils;
import com.easyapi.core.utils.Utils;

import static com.easyapi.core.constant.CoreConstants.*;

import java.io.File;


/**
 * docs
 * <p>
 * licence Apache 2.0,AGPL-3.0, from japidoc and easyapi originated
 **/
public class Docs {

    public static void buildHtmlDocs(DocConfig config) {
        //文档上下文初始化
        DocContext.init(config);
        //生成文档
        com.easyapi.core.doc.HtmlDocGenerator docGenerator = new HtmlDocGenerator();
        DocContext.setControllerNodeList(docGenerator.getControllerNodeList());
        try {
            docGenerator.generateDocs();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            // 清理可能产生的PageInfo临时文件
            Utils.deleteFileWithRetry(System.getProperty(USER_DIR) + File.separator + PAGE_INFO_FILE);
            Utils.deleteFileWithRetry(System.getProperty(USER_DIR) + File.separator + ES_PAGE_INFO_FILE);
        }
        //保存ControllerNode
        CacheUtils.saveControllerNodes(docGenerator.getControllerNodeList());
        //运行插件
        DocConfig docConfig = DocContext.getDocsConfig();
        if (docConfig.getRapProjectId() != null && docConfig.getRapHost() != null) {
            IPluginSupport rapPlugin = new RapSupportPlugin();
            rapPlugin.execute(docGenerator.getControllerNodeList());
        }
        for (IPluginSupport plugin : config.getPlugins()) {
            plugin.execute(docGenerator.getControllerNodeList());
        }
        // 清理 cache.json
        Utils.deleteFileWithRetry(DocContext.getDocPath() + File.separator + CACHE_FILE);
    }
}
