package org.expenses.web.view.admin;

import org.expenses.web.model.Conference;
import org.expenses.web.model.Currency;
import org.expenses.web.model.Reimbursement;
import org.expenses.web.model.User;

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
 * Backing bean for Reimbursement entities.
 * <p/>
 * This class provides CRUD functionality for all Reimbursement entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ReimbursementBean implements Serializable {

    private static final long serialVersionUID = 1L;

	/*
     * Support creating and retrieving Reimbursement entities
	 */

    private Long id;
    private Reimbursement reimbursement;
    @Inject
    private Conversation conversation;
    @PersistenceContext(unitName = "expenses-pu", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<Reimbursement> pageItems;
    private Reimbursement example = new Reimbursement();
    @Resource
    private SessionContext sessionContext;
    private Reimbursement add = new Reimbursement();

    public Long getId() {
        return this.id;
    }

	/*
	 * Support updating and deleting Reimbursement entities
	 */

    public void setId(Long id) {
        this.id = id;
    }

    public Reimbursement getReimbursement() {
        return this.reimbursement;
    }

	/*
	 * Support searching Reimbursement entities with pagination
	 */

    public void setReimbursement(Reimbursement reimbursement) {
        this.reimbursement = reimbursement;
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
            this.reimbursement = this.example;
        } else {
            this.reimbursement = findById(getId());
        }
    }

    public Reimbursement findById(Long id) {

        return this.entityManager.find(Reimbursement.class, id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.reimbursement);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.reimbursement);
                return "view?faces-redirect=true&id="
                        + this.reimbursement.getId();
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
            Reimbursement deletableEntity = findById(getId());

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

    public Reimbursement getExample() {
        return this.example;
    }

    public void setExample(Reimbursement example) {
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
        Root<Reimbursement> root = countCriteria.from(Reimbursement.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Reimbursement> criteria = builder
                .createQuery(Reimbursement.class);
        root = criteria.from(Reimbursement.class);
        TypedQuery<Reimbursement> query = this.entityManager
                .createQuery(criteria.select(root).where(
                        getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Reimbursement> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        Currency currency = this.example.getCurrency();
        if (currency != null) {
            predicatesList.add(builder.equal(root.get("currency"), currency));
        }
        User user = this.example.getUser();
        if (user != null) {
            predicatesList.add(builder.equal(root.get("user"), user));
        }
        Conference conference = this.example.getConference();
        if (conference != null) {
            predicatesList
                    .add(builder.equal(root.get("conference"), conference));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

	/*
	 * Support listing and POSTing back Reimbursement entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

    public List<Reimbursement> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Reimbursement> getAll() {

        CriteriaQuery<Reimbursement> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(Reimbursement.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Reimbursement.class)))
                .getResultList();
    }

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

    public Converter getConverter() {

        final ReimbursementBean ejbProxy = this.sessionContext
                .getBusinessObject(ReimbursementBean.class);

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

                return String.valueOf(((Reimbursement) value).getId());
            }
        };
    }

    public Reimbursement getAdd() {
        return this.add;
    }

    public Reimbursement getAdded() {
        Reimbursement added = this.add;
        this.add = new Reimbursement();
        return added;
    }
}
