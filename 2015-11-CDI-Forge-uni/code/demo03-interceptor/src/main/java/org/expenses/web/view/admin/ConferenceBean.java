package org.expenses.web.view.admin;

import org.expenses.web.model.Conference;
import org.expenses.web.service.ConferenceService;

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
 * Backing bean for Conference entities.
 * <p/>
 * This class provides CRUD functionality for all Conference entities. It focuses purely on Java EE 6 standards (e.g.
 * <tt>&#64;ConversationScoped</tt> for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or custom base class.
 */

@Named
@ConversationScoped
public class ConferenceBean implements Serializable {

    private static final long serialVersionUID = 1L;

	/*
     * Support creating and retrieving Conference entities
	 */

    private Long id;
    private Conference conference;
    @Inject
    private Conversation conversation;
    @Inject
    private ConferenceService service;
    private int page;
    private long count;
    private List<Conference> pageItems;
    private Conference example = new Conference();
    private Conference add = new Conference();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   /*
    * Support updating and deleting Conference entities
    */

    public Conference getConference() {
        return this.conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

   /*
    * Support searching Conference entities with pagination
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
            this.conference = this.example;
        } else {
            this.conference = findById(getId());
        }
    }

    public Conference findById(Long id) {

        return this.service.findById(id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.service.persist(this.conference);
                return "search?faces-redirect=true";
            } else {
                this.service.merge(this.conference);
                return "view?faces-redirect=true&id=" + this.conference.getId();
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
            Conference deletableEntity = findById(getId());

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

    public Conference getExample() {
        return this.example;
    }

    public void setExample(Conference example) {
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

    public List<Conference> getPageItems() {
        return this.pageItems;
    }

	/*
    * Support listing and POSTing back Conference entities (e.g. from inside an HtmlSelectOneMenu)
	 */

    public long getCount() {
        return this.count;
    }

    public List<Conference> getAll() {

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

                return String.valueOf(((Conference) value).getId());
            }
        };
    }

    public Conference getAdd() {
        return this.add;
    }

    public Conference getAdded() {
        Conference added = this.add;
        this.add = new Conference();
        return added;
    }
}
