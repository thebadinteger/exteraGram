package com.android.tools.r8.annotations;
import java.lang.annotation.*;
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface SynthesizedClassMap {
    Class<?>[] value() default {};
}
