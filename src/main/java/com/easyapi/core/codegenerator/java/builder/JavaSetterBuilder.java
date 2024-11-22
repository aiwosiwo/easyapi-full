package com.easyapi.core.codegenerator.java.builder;


import com.easyapi.core.codegenerator.ICodeBuilder;
import com.easyapi.core.codegenerator.model.FieldModel;

/**
 * java setter builder
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class JavaSetterBuilder implements ICodeBuilder {

    private String setterTemplate;
    private com.easyapi.core.codegenerator.model.FieldModel entryFieldModel;

    public JavaSetterBuilder(String setterTemplate, FieldModel entryFieldModel) {
        this.setterTemplate = setterTemplate;
        this.entryFieldModel = entryFieldModel;
    }

    @Override
    public String build() {
        setterTemplate = setterTemplate.replace("${REMOTE_FIELD_NAME}", entryFieldModel.getRemoteFieldName());
        setterTemplate = setterTemplate.replace("${CASE_FIELD_NAME}", entryFieldModel.getCaseFieldName());
        setterTemplate = setterTemplate.replace("${FIELD_NAME}", entryFieldModel.getFieldName());
        setterTemplate = setterTemplate.replace("${FIELD_TYPE}", entryFieldModel.getFieldType());
        return setterTemplate + "\n";
    }

}
