package org.expenses.web.beans;

import javax.enterprise.inject.Stereotype;
import javax.transaction.Transactional;
import java.lang.annotation.*;

@Stereotype
@Transactional
@Loggable
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Service {
}