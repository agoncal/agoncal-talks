package org.expenses.web.beans;

import javax.enterprise.inject.Intercepted;
import javax.enterprise.inject.spi.Bean;
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

    @Inject @Intercepted
    private Bean<?> intercepted;

    @AroundInvoke
    private Object intercept(InvocationContext ic) throws Exception {
        logger.info("> > " + intercepted.getBeanClass().getName() + " - " + ic.getMethod().getName());
        try {
            return ic.proceed();
        } finally {
            logger.info("< < " + intercepted.getBeanClass().getName() + " - " + ic.getMethod().getName());
        }
    }
}
