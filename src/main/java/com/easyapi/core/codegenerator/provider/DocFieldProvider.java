package com.easyapi.core.codegenerator.provider;


import com.easyapi.core.utils.Utils;
import com.easyapi.core.codegenerator.IFieldProvider;
import com.easyapi.core.codegenerator.model.FieldModel;
import com.easyapi.core.parser.ClassNode;
import com.easyapi.core.parser.FieldNode;

import java.util.ArrayList;
import java.util.List;

/**
 * doc field provider
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public class DocFieldProvider implements IFieldProvider {

    @Override
    public List<FieldModel> provideFields(ClassNode respNode) {
        List<FieldNode> recordNodes = respNode.getChildNodes();
        if (recordNodes == null || recordNodes.isEmpty()) {
            return null;
        }
        List<FieldModel> entryFieldList = new ArrayList<>();
        FieldModel entryField;
        for (FieldNode recordNode : recordNodes) {
            entryField = new FieldModel();
            String fieldName = DocFieldHelper.getPrefFieldName(recordNode.getName());
            entryField.setCaseFieldName(Utils.capitalize(fieldName));
            entryField.setFieldName(fieldName);
            entryField.setFieldType(DocFieldHelper.getPrefFieldType(recordNode.getType()));
            entryField.setRemoteFieldName(recordNode.getName());
            entryField.setComment(recordNode.getDescription());
            entryFieldList.add(entryField);
        }
        return entryFieldList;
    }

}
