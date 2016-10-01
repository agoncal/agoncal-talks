package org.expenses.billing.service;

import java.util.logging.Logger;

public class BillingService {

    private Logger logger = Logger.getLogger(BillingService.class.getName());

    public void reimbursementToBill(String userName, String conferenceName, Float amount) {
        waitAbit();
        logger.info(" ");
        logger.info("-- BillingService -- ");
        logger.info("-- " + userName);
        logger.info("-- " + conferenceName);
        logger.info("-- " + amount);
        logger.info("-------------------- ");
    }

    private void waitAbit() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
    }
}