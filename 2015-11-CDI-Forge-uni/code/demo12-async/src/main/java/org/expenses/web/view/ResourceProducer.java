package org.expenses.web.view;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

public class ResourceProducer {

    // ======================================
    // = Business methods =
    // ======================================

    @Produces
    @RequestScoped
    private FacesContext produceFacesContext() {
        return FacesContext.getCurrentInstance();
    }

}