package com.dunai.home.client.workspace.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Editable {
    public enum Type {
        STRING,
        NUMBER
    }
    public String key();
    public Type type();
    public int minValue() default 0;
    public int maxValue() default 0;
}
