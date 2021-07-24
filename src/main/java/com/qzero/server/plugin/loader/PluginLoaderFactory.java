package com.qzero.server.plugin.loader;

public class PluginLoaderFactory {

    private static JarPluginLoader jarPluginLoader=new JarPluginLoader();

    private static XmlPluginLoader xmlPluginLoader=new XmlPluginLoader();

    public enum PluginFileType{
        JAR,XML
    }

    public static PluginLoader getPluginLoader(PluginFileType fileType){
        switch (fileType){
            case JAR:
                return jarPluginLoader;
            case XML:
                return xmlPluginLoader;
            default:
                throw new IllegalArgumentException("Plugin file is neither a xml file nor a jar file");
        }
    }

}
