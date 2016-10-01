package org.expenses.web.view.expense;

import org.expenses.web.beans.Wizard;
import org.expenses.web.model.*;
import org.expenses.web.service.CurrencyService;
import org.expenses.web.service.ReimbursementService;
import org.expenses.web.service.UserService;
import org.expenses.web.view.account.LoggedIn;

import javax.enterprise.context.Conversation;
import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;

/**
 * Main Backing bean for creating expensee.
 * <p/>
 * This class provides CRUD functionality for all Expense entities. It focuses purely on Java EE 6 standards (e.g.
 * <tt>&#64;ConversationScoped</tt> for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or custom base class.
 */

@Wizard
public class ExpenseesBean implements Serializable {

    @Inject
    private ReimbursementService service;

    @Inject
    private UserService userService;

    @Inject
    private FacesContext facesContext;

    @Inject
    private Conversation conversation;

    @Inject
    private CurrencyService currencyService;

    @Inject @LoggedIn
    private Instance<User> loggedInUser;

    private Currency currency;

    private Conference conference;

    private Expense expense = new Expense();

    private Reimbursement reimbursement;

    public String addExpense() {
        if (this.conversation.isTransient()) {
            this.conversation.begin();
            reimbursement = new Reimbursement();
        }

        expense.setCurrency(currency);
        reimbursement.add(expense);
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Expense added " + expense.getDescription(), ""));
        expense = new Expense();
        return null;
    }

    public String recap() {
        reimbursement.setUser(loggedInUser.get());
        reimbursement.setDate(new Date());
        reimbursement.setConference(conference);
        reimbursement.setCurrency(currency);
        return "/expense/recap";
    }

    public String back() {
        return "/expense/create";
    }

    public String confirm() {
        reimbursement.setCurrency(currency);
        service.persist(reimbursement);
        this.conversation.end();
        return "/index";
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public Expense getExpense() {
        return expense;
    }

    public Reimbursement getReimbursement() {
        return reimbursement;
    }

    public void setReimbursement(Reimbursement reimbursement) {
        this.reimbursement = reimbursement;
    }

    public float getTotalAmount() {
        if (currency.equals(reimbursement.getCurrency()))
            return reimbursement.getTotalAmount();
        else
            return currencyService.change(reimbursement.getTotalAmount(), currency);
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
