package org.expenses.web.view.admin;

import org.expenses.web.beans.Wizard;
import org.expenses.web.model.User;
import org.expenses.web.service.UserService;

import javax.enterprise.context.Conversation;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Backing bean for User entities.
 * <p/>
 * This class provides CRUD functionality for all User entities. It focuses purely on Java EE 6 standards (e.g.
 * <tt>&#64;ConversationScoped</tt> for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or custom base class.
 */

@Wizard
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

	/*
     * Support creating and retrieving User entities
	 */

    private Long id;
    private User user;
    @Inject
    private Conversation conversation;
    @Inject
    private UserService service;
    private int page;
    private long count;
    private List<User> pageItems;
    private User example = new User();
    private User add = new User();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   /*
    * Support updating and deleting User entities
    */

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

   /*
    * Support searching User entities with pagination
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
            this.user = this.example;
        } else {
            this.user = findById(getId());
        }
    }

    public User findById(Long id) {

        return this.service.findById(id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.service.persist(this.user);
                return "search?faces-redirect=true";
            } else {
                this.service.merge(this.user);
                return "view?faces-redirect=true&id=" + this.user.getId();
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
            User deletableEntity = findById(getId());

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

    public User getExample() {
        return this.example;
    }

    public void setExample(User example) {
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

    public List<User> getPageItems() {
        return this.pageItems;
    }

	/*
    * Support listing and POSTing back User entities (e.g. from inside an HtmlSelectOneMenu)
	 */

    public long getCount() {
        return this.count;
    }

    public List<User> getAll() {

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

                return String.valueOf(((User) value).getId());
            }
        };
    }

    public User getAdd() {
        return this.add;
    }

    public User getAdded() {
        User added = this.add;
        this.add = new User();
        return added;
    }
}
