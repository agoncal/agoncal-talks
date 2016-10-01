package org.expenses.web.view.admin;

import org.expenses.web.beans.Wizard;
import org.expenses.web.model.Reimbursement;
import org.expenses.web.service.ReimbursementService;

import javax.enterprise.context.Conversation;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Backing bean for Reimbursement entities.
 * <p/>
 * This class provides CRUD functionality for all Reimbursement entities. It focuses purely on Java EE 6 standards (e.g.
 * <tt>&#64;ConversationScoped</tt> for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or custom base class.
 */

@Wizard
public class ReimbursementBean implements Serializable {

    private static final long serialVersionUID = 1L;

	/*
     * Support creating and retrieving Reimbursement entities
	 */

    private Long id;
    private Reimbursement reimbursement;
    @Inject
    private Conversation conversation;
    @Inject
    private ReimbursementService service;
    private int page;
    private long count;
    private List<Reimbursement> pageItems;
    private Reimbursement example = new Reimbursement();
    private Reimbursement add = new Reimbursement();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   /*
    * Support updating and deleting Reimbursement entities
    */

    public Reimbursement getReimbursement() {
        return this.reimbursement;
    }

    public void setReimbursement(Reimbursement reimbursement) {
        this.reimbursement = reimbursement;
    }

   /*
    * Support searching Reimbursement entities with pagination
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
            this.reimbursement = this.example;
        } else {
            this.reimbursement = findById(getId());
        }
    }

    public Reimbursement findById(Long id) {

        return this.service.findById(id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.service.persist(this.reimbursement);
                return "search?faces-redirect=true";
            } else {
                this.service.merge(this.reimbursement);
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

        // Populate this.count
        this.count = this.service.count(example);

        // Populate this.pageItems
        this.pageItems = this.service.page(example, page, getPageSize());

    }

    public List<Reimbursement> getPageItems() {
        return this.pageItems;
    }

	/*
    * Support listing and POSTing back Reimbursement entities (e.g. from inside an HtmlSelectOneMenu)
	 */

    public long getCount() {
        return this.count;
    }

    public List<Reimbursement> getAll() {

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
