package ru.kudesunik.kudesunetwork.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Marked resource permanently allocates memory without cleaning for the entire duration of the program;
 * <br>This annotation is used to indicate that marked resource does not leak memory
 * @author Kudesunik
 *
 */

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE})
public @interface PermanentAllocation {
	//PermanentAllocation
}
