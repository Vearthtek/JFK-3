package icallable;

import java.lang.annotation.*;

/**
 * Created by Vearthtek on 2017-06-04.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    String description() default "";
}