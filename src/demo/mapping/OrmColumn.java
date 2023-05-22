package demo.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OrmColumn {
	
	String name() default "";

	OrmPrimaryKeyTypes primaryKey() default OrmPrimaryKeyTypes.NONE;

	OrmOrderTypes order() default OrmOrderTypes.NONE;

	String table() default "";

	String foreingKey() default "";
	
}
