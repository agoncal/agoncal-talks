package org.expenses.web.view.admin;

import org.expenses.web.model.Currency;
import org.expenses.web.model.Expense;
import org.expenses.web.model.ExpenseType;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean for Expense entities.
 * <p/>
 * This class provides CRUD functionality for all Expense entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
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
    @PersistenceContext(unitName = "expenses-pu", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<Expense> pageItems;
    private Expense example = new Expense();
    @Resource
    private SessionContext sessionContext;
    private Expense add = new Expense();

    public Long getId() {
        return this.id;
    }

	/*
	 * Support updating and deleting Expense entities
	 */

    public void setId(Long id) {
        this.id = id;
    }

    public Expense getExpense() {
        return this.expense;
    }

	/*
	 * Support searching Expense entities with pagination
	 */

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

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

        return this.entityManager.find(Expense.class, id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.expense);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.expense);
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

            this.entityManager.remove(deletableEntity);
            this.entityManager.flush();
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

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Expense> root = countCriteria.from(Expense.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Expense> criteria = builder.createQuery(Expense.class);
        root = criteria.from(Expense.class);
        TypedQuery<Expense> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Expense> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        String description = this.example.getDescription();
        if (description != null && !"".equals(description)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("description")),
                    '%' + description.toLowerCase() + '%'));
        }
        ExpenseType expenseType = this.example.getExpenseType();
        if (expenseType != null) {
            predicatesList.add(builder.equal(root.get("expenseType"),
                    expenseType));
        }
        Currency currency = this.example.getCurrency();
        if (currency != null) {
            predicatesList.add(builder.equal(root.get("currency"), currency));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

	/*
	 * Support listing and POSTing back Expense entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

    public List<Expense> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Expense> getAll() {

        CriteriaQuery<Expense> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(Expense.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Expense.class))).getResultList();
    }

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

    public Converter getConverter() {

        final ExpenseBean ejbProxy = this.sessionContext
                .getBusinessObject(ExpenseBean.class);

        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context,
                                      UIComponent component, String value) {

                return ejbProxy.findById(Long.valueOf(value));
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
