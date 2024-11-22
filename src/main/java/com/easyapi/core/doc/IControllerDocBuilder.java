package com.easyapi.core.doc;

import com.easyapi.core.parser.ControllerNode;

import java.io.IOException;

/**
 * controller doc builder
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public interface IControllerDocBuilder {

    String buildDoc(ControllerNode controllerNode) throws IOException;

}
