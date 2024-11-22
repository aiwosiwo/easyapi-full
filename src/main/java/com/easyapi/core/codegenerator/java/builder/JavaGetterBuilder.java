package com.easyapi.core.codegenerator.java.builder;


import com.easyapi.core.codegenerator.model.FieldModel;
import com.easyapi.core.codegenerator.ICodeBuilder;

/**
 * java getter builder
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class JavaGetterBuilder implements ICodeBuilder {

    private String getterTemplate;
    private FieldModel entryFieldModel;

    public JavaGetterBuilder(String getterTemplate, FieldModel entryFieldModel) {
        this.getterTemplate = getterTemplate;
        this.entryFieldModel = entryFieldModel;
    }

    @Override
    public String build() {
        getterTemplate = getterTemplate.replace("${CASE_FIELD_NAME}", entryFieldModel.getCaseFieldName());
        getterTemplate = getterTemplate.replace("${FIELD_NAME}", entryFieldModel.getFieldName());
        getterTemplate = getterTemplate.replace("${FIELD_TYPE}", entryFieldModel.getFieldType());
        getterTemplate = getterTemplate.replace("${REMOTE_FIELD_NAME}", entryFieldModel.getRemoteFieldName());
        return getterTemplate + "\n";
    }

}
