package org.expenses.web.view.expense;

import org.expenses.web.model.Reimbursement;
import org.expenses.web.service.ReimbursementService;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Backing bean for Reimbursement entities.
 * <p/>
 * This class provides CRUD functionality for all Reimbursement entities. It focuses purely on Java EE 6 standards (e.g.
 * <tt>&#64;ConversationScoped</tt> for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or custom base class.
 */

@Named
@ConversationScoped
public class ReimbursementsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Conversation conversation;
    @Inject
    private ReimbursementService service;

    private Long id;
    private Reimbursement reimbursement;

    private int page;
    private long count;
    private List<Reimbursement> pageItems;
    private Reimbursement example = new Reimbursement();

   /*
    * Support searching Reimbursement entities with pagination
    */

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reimbursement getReimbursement() {
        return this.reimbursement;
    }

    public void setReimbursement(Reimbursement reimbursement) {
        this.reimbursement = reimbursement;
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

    public long getCount() {
        return this.count;
    }

   /*
    * Support listing and POSTing back Reimbursement entities (e.g. from inside an HtmlSelectOneMenu)
    */
}
