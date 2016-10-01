package org.expenses.banking;

import org.expenses.web.model.Reimbursement;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Logger;

public class BankingService {

    @Inject
    private Logger logger;

    public void reimbursementToBePaid(@Observes Reimbursement event) {
        waitAbit();
        logger.info(" ");
        logger.info("-- BankingService -- ");
        logger.info("-- " + event);
        logger.info("-------------------- ");
    }

    private void waitAbit() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
    }
}