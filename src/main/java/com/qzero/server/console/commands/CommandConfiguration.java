package com.qzero.server.console.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandConfiguration {

    boolean value() default true;

}
