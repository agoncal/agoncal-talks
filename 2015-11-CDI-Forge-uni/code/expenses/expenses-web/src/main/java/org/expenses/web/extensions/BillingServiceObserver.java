package org.expenses.web.extensions;

import org.expenses.billing.service.BillingService;
import org.expenses.web.model.Reimbursement;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.ObserverMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

public class BillingServiceObserver implements ObserverMethod<Reimbursement> {
    @Override
    public Class<?> getBeanClass() {
        return BillingService.class;
    }

    @Override
    public Type getObservedType() {
        return Reimbursement.class;
    }

    @Override
    public Set<Annotation> getObservedQualifiers() {
        return Collections.emptySet();
    }

    @Override
    public Reception getReception() {
        return Reception.ALWAYS;
    }

    @Override
    public TransactionPhase getTransactionPhase() {
        return TransactionPhase.IN_PROGRESS;
    }

    @Override
    public void notify(Reimbursement event) {
        new BillingService().reimbursementToBill(event.getUser().getName(), event.getConference().getName(), event.getTotalAmount());
    }
}
