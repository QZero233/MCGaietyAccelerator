package com.qzero.server.plugin.loader;

import com.qzero.server.plugin.PluginEntry;

import java.io.File;

public interface PluginLoader {

    PluginEntry loadPlugin(File file);

}
