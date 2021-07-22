package com.qzero.server.plugin.loader;

public class PluginLoaderFactory {

    private static JarPluginLoader jarPluginLoader=new JarPluginLoader();

    public enum PluginFileType{
        JAR,XML
    }

    public static PluginLoader getPluginLoader(PluginFileType fileType){
        switch (fileType){
            case JAR:
                return jarPluginLoader;
            case XML:
                break;
            default:
                throw new IllegalArgumentException("Plugin file is neither a xml file nor a jar file");
        }
        return null;//TODO FINISH IT
    }

}
