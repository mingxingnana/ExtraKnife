package com.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2018/4/10 0010.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Extra {
    String value() default "";
}
