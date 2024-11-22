package com.easyapi.core;

import com.easyapi.core.parser.ControllerNode;

import java.util.List;

/**
 * plugin support
 * <p>
 * licence Apache 2.0, from japidoc
 **/
public interface IPluginSupport {

    void execute(List<ControllerNode> controllerNodeList);
}
