package org.expenses.web.beans;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

@Loggable
@Interceptor
public class LoggingInterceptor {

    @Inject
    private Logger logger;

    @AroundInvoke
    private Object intercept(InvocationContext ic) throws Exception {
        logger.info("> > " + ic.getMethod().getDeclaringClass().getName() + " - " + ic.getMethod().getName());
        try {
            return ic.proceed();
        } finally {
            logger.info("< < " + ic.getMethod().getDeclaringClass().getName() + " - " + ic.getMethod().getName());
        }
    }
}
