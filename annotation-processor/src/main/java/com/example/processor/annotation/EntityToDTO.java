package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // Class level
@Retention(RetentionPolicy.SOURCE) // Discarded during compile-time (not included in the .class files)
public @interface EntityToDTO {
    String dtoName();
}
