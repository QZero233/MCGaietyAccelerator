package com.qzero.server.plugin.loader;

public class PluginLoaderFactory {

    private static JarPluginLoader jarPluginLoader=new JarPluginLoader();

    public static PluginLoader getPluginLoader(String fileName){
        if(fileName.matches("\\.xml$")){
            //Xml
        }else if(fileName.matches("\\.jar$")){
            //Jar
            return jarPluginLoader;
        }else{
            throw new IllegalArgumentException(String.format("Plugin file %s is neither a xml file nor a jar file", fileName));
        }

        return null;//TODO FINISH IT
    }

}
