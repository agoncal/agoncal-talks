package org.expenses.web.beans;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;
import java.lang.annotation.*;

@Stereotype
@Named
@ConversationScoped
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Wizard {
}