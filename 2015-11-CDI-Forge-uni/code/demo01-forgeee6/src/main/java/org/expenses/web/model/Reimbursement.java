package org.expenses.web.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "t_reimbursement")
public class Reimbursement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Version
    @Column(name = "version")
    private int version;

    @Column
    @Temporal(TemporalType.DATE)
    private Date date;

    @OneToMany
    private Set<Expense> expenses = new HashSet<Expense>();

    @Enumerated
    private Currency currency;

    @ManyToOne
    private User user;

    @ManyToOne
    private Conference conference;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Reimbursement)) {
            return false;
        }
        Reimbursement other = (Reimbursement) obj;
        if (id != null) {
            if (!id.equals(other.id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Expense> getExpenses() {
        return this.expenses;
    }

    public void setExpenses(final Set<Expense> expenses) {
        this.expenses = expenses;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (id != null)
            result += "id: " + id;
        result += ", version: " + version;
        if (date != null)
            result += ", date: " + date;
        if (expenses != null)
            result += ", expenses: " + expenses;
        if (currency != null)
            result += ", currency: " + currency;
        return result;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Conference getConference() {
        return this.conference;
    }

    public void setConference(final Conference conference) {
        this.conference = conference;
    }
}