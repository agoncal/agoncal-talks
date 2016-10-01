package org.expenses.web.view.admin;

import org.expenses.web.model.Expense;
import org.expenses.web.service.ExpenseService;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Backing bean for Expense entities.
 * <p/>
 * This class provides CRUD functionality for all Expense entities. It focuses purely on Java EE 6 standards (e.g.
 * <tt>&#64;ConversationScoped</tt> for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or custom base class.
 */

@Named
@ConversationScoped
public class ExpenseBean implements Serializable {

    private static final long serialVersionUID = 1L;

	/*
     * Support creating and retrieving Expense entities
	 */

    private Long id;
    private Expense expense;
    @Inject
    private Conversation conversation;
    @Inject
    private ExpenseService service;
    private int page;
    private long count;
    private List<Expense> pageItems;
    private Expense example = new Expense();
    private Expense add = new Expense();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   /*
    * Support updating and deleting Expense entities
    */

    public Expense getExpense() {
        return this.expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

   /*
    * Support searching Expense entities with pagination
    */

    public String create() {

        this.conversation.begin();
        this.conversation.setTimeout(1800000L);
        return "create?faces-redirect=true";
    }

    public void retrieve() {

        if (FacesContext.getCurrentInstance().isPostback()) {
            return;
        }

        if (this.conversation.isTransient()) {
            this.conversation.begin();
            this.conversation.setTimeout(1800000L);
        }

        if (this.id == null) {
            this.expense = this.example;
        } else {
            this.expense = findById(getId());
        }
    }

    public Expense findById(Long id) {

        return this.service.findById(id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.service.persist(this.expense);
                return "search?faces-redirect=true";
            } else {
                this.service.merge(this.expense);
                return "view?faces-redirect=true&id=" + this.expense.getId();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete() {
        this.conversation.end();

        try {
            Expense deletableEntity = findById(getId());

            this.service.remove(deletableEntity);
            return "search?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return 10;
    }

    public Expense getExample() {
        return this.example;
    }

    public void setExample(Expense example) {
        this.example = example;
    }

    public String search() {
        this.page = 0;
        return null;
    }

    public void paginate() {

        // Populate this.count
        this.count = this.service.count(example);

        // Populate this.pageItems
        this.pageItems = this.service.page(example, page, getPageSize());

    }

    public List<Expense> getPageItems() {
        return this.pageItems;
    }

	/*
    * Support listing and POSTing back Expense entities (e.g. from inside an HtmlSelectOneMenu)
	 */

    public long getCount() {
        return this.count;
    }

    public List<Expense> getAll() {

        return this.service.listAll();
    }

	/*
     * Support adding children to bidirectional, one-to-many tables
	 */

    public Converter getConverter() {

        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context,
                                      UIComponent component, String value) {

                return service.findById(Long.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context,
                                      UIComponent component, Object value) {

                if (value == null) {
                    return "";
                }

                return String.valueOf(((Expense) value).getId());
            }
        };
    }

    public Expense getAdd() {
        return this.add;
    }

    public Expense getAdded() {
        Expense added = this.add;
        this.add = new Expense();
        return added;
    }
}
