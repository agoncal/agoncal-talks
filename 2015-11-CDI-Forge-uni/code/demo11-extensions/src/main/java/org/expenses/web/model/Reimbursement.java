package org.expenses.web.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

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

    @Temporal(TemporalType.DATE)
    private Date date;

    @OneToMany(fetch = EAGER, cascade = PERSIST)
    private Set<Expense> expenses = new HashSet<>();

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void add(Expense expense) {
        expenses.add(expense);
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

    public Float getTotalAmount() {
        Float totalAmount = 0f;
        for (Expense expense : expenses) {
            totalAmount += expense.getAmount();
        }
        return totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Reimbursement that = (Reimbursement) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(user, that.user) &&
                Objects.equals(conference, that.conference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, user, conference);
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (id != null)
            result += "id: " + id;
        if (date != null)
            result += ", date: " + date;
        if (expenses != null)
            result += ", expenses: " + expenses;
        if (currency != null)
            result += ", currency: " + currency;
        return result;
    }
}