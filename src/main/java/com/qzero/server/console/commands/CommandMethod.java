package com.qzero.server.console.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMethod {

    String commandName();
    int parameterCount() default 0;
    boolean needServerSelected() default true;
    int minAdminPermission() default 0;

}
