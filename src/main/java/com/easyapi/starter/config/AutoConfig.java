package com.easyapi.starter.config;

import com.easyapi.core.Docs;
import com.easyapi.core.DocConfig;
import com.easyapi.core.LogUtils;
import com.easyapi.core.plugin.markdown.MarkdownDocPlugin;
import com.easyapi.starter.property.DocConfigProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;

/**
 * 自动生成装配
 **/
@Configuration
@EnableConfigurationProperties(value = {DocConfigProperties.class})
@ConditionalOnProperty(prefix = "easyapi", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class AutoConfig implements InitializingBean {
    @Autowired
    private DocConfigProperties docConfigProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!docConfigProperties.isEnable()) {
            LogUtils.info("easyapi not enabled, won't generate docs");
            return;
        }
        CompletableFuture.runAsync(this::doGenerate)
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    private void doGenerate() {
        DocConfig docConfig = new DocConfig();
        // 项目根目录
        docConfig.setProjectPath(docConfigProperties.getProjectPath());
        // 项目名称
        docConfig.setProjectName(docConfigProperties.getProjectName());
        // 声明该API的版本
        docConfig.setApiVersion(docConfigProperties.getDocVersion());
        // 生成API 文档所在目录
        docConfig.setDocsPath(docConfigProperties.getDocPath());
        // 配置自动生成
        docConfig.setAutoGenerate(docConfigProperties.isAutoGenerate());
        // 设置默认语言
        docConfig.setLocale(docConfigProperties.getLocale());
        // 添加MarkDown文档
        if (docConfigProperties.isGenerateMarkDown()) {
            docConfig.addPlugin(new MarkdownDocPlugin());
        }
        // 执行生成文档
        Docs.buildHtmlDocs(docConfig);
    }
}
