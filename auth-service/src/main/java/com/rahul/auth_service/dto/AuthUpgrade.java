package com.rahul.auth_service.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD}) // Can be applied to classes or methods
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
public @interface AuthUpgrade {
    AuthUpgradeModule module();
    AuthAction action();
}
