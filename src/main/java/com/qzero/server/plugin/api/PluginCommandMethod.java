package com.qzero.server.plugin.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginCommandMethod {

    String commandName();
    int parameterCount() default 0;
    boolean needServerSelected() default true;

}
