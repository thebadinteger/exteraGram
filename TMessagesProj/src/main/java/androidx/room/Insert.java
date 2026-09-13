package androidx.room;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {
    int onConflict() default OnConflictStrategy.ABORT;
}
