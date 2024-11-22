package com.easyapi.core.codegenerator;


import com.easyapi.core.codegenerator.model.FieldModel;
import com.easyapi.core.parser.ClassNode;

import java.util.List;

/**
 * field provider
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public interface IFieldProvider {
    List<FieldModel> provideFields(ClassNode respNode);
}
