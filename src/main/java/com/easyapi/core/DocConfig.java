package com.easyapi.core;

import com.easyapi.core.constant.CoreConstants;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * abstract doc generator
 * <p>
 * licence Apache 2.0,AGPL-3.0, from japidoc and easyapi originated
 **/
@Data
public class DocConfig {
    /**
     * not null
     */
    String projectPath;
    /**
     * multi modules support
     */
    List<String> javaSrcPaths = new ArrayList<>();
    /**
     * default equals projectPath
     */
    String docsPath;
    /**
     * if empty, use the default resources
     */
    String resourcePath;
    /**
     * mvcFramework = [spring, play, jfinal, generic], can be empty
     */
    String mvcFramework;
    String apiVersion;
    String projectName;
    /**
     * 自动生成所有Controller的接口文档，不需要@DocApi注解
     */
    Boolean autoGenerate = Boolean.FALSE;
    /**
     * 生成文档语言，getDefault()系统默认
     */
    Locale locale = Locale.ENGLISH;
    /**
     * 是否开启对象反射
     */
    Boolean openReflection = Boolean.TRUE;
    String watermark = CoreConstants.DEFAULT_WATERMARK;
    String rapHost;
    String rapLoginCookie;
    String rapProjectId;
    String rapAccount;
    String rapPassword;
    List<com.easyapi.core.IPluginSupport> plugins = new ArrayList<>();
    boolean isSpringMvcProject() {
        return mvcFramework != null && mvcFramework.equals("spring");
    }
    boolean isPlayProject() {
        return mvcFramework != null && mvcFramework.equals("play");
    }
    boolean isJfinalProject() {
        return mvcFramework != null && mvcFramework.equals("jfinal");
    }
    boolean isGeneric() {
        return mvcFramework != null && mvcFramework.equals("generic");
    }
    public void addPlugin(IPluginSupport plugin) {
        this.plugins.add(plugin);
    }
}
