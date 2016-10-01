package org.expenses.web.service;

import javax.enterprise.inject.Alternative;

@Alternative
public class RateServiceMock implements Rateable {

    public double rate() {
        return 99.99;
    }
}
