package org.expenses.web.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "t_expense")
public class Expense implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Version
    @Column(name = "version")
    private int version;

    private String description;

    @Temporal(TemporalType.DATE)
    private Date date;

    private Float amount;

    @Enumerated
    private ExpenseType expenseType;

    @Enumerated
    private Currency currency;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Expense expense = (Expense) o;
        return Objects.equals(description, expense.description) &&
                Objects.equals(date, expense.date) &&
                Objects.equals(amount, expense.amount) &&
                Objects.equals(expenseType, expense.expenseType) &&
                Objects.equals(currency, expense.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, date, amount, expenseType, currency);
    }

    @Override
    public String toString() {
        return description + " - " + amount;
    }
}