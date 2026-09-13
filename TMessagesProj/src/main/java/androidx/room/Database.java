package androidx.room;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Database {
    Class<?>[] entities() default {};
    int version() default 1;
    boolean exportSchema() default true;
}
