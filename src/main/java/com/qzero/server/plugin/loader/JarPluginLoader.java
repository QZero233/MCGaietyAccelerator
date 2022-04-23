package com.qzero.server.plugin.loader;

import com.qzero.server.plugin.PluginEntry;
import com.qzero.server.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class JarPluginLoader implements PluginLoader {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public PluginEntry loadPlugin(File file) {
        try {
            URL url=new URL("file:"+file.getAbsolutePath());
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());

            InputStream is = classLoader.getResourceAsStream("pluginEntry");
            byte[] entryClassNameBuf= StreamUtils.readDataFromInputStream(is);
            if(entryClassNameBuf==null)
                throw new IllegalArgumentException("Can not find pluginEntry file");

            String entryClassName=new String(entryClassNameBuf);
            Class entryClass=classLoader.loadClass(entryClassName);
            PluginEntry entry= (PluginEntry) entryClass.newInstance();
            return entry;
        } catch (Exception e) {
            log.error("Failed to load plugin with file named "+file.getAbsolutePath());
            return null;
        }
    }

}
