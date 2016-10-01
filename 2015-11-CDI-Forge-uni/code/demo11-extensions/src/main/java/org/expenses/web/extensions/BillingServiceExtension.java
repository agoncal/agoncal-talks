package org.expenses.web.extensions;


import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class BillingServiceExtension implements Extension {

    public void listen(@Observes AfterBeanDiscovery afterBeanDiscovery) {
        afterBeanDiscovery.addObserverMethod(new BillingServiceObserver());
    }
}
