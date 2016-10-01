package org.expenses.web.service;

import org.expenses.web.beans.Service;
import org.expenses.web.model.Currency;
import org.expenses.web.model.Expense;
import org.expenses.web.model.ExpenseType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Transactional service for Expense entities.
 * <p/>
 * This class provides CRUD functionality for all Expense entities.
 */

@Service
public class ExpenseService extends AbstractService<Expense> {

    public ExpenseService() {
        super(Expense.class);
    }

    @Override
    protected Predicate[] getSearchPredicates(Root<Expense> root, Expense example) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<>();

        String description = example.getDescription();
        if (description != null && !"".equals(description)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("description")),
                    '%' + description.toLowerCase() + '%'));
        }
        ExpenseType expenseType = example.getExpenseType();
        if (expenseType != null) {
            predicatesList.add(builder.equal(root.get("expenseType"),
                    expenseType));
        }
        Currency currency = example.getCurrency();
        if (currency != null) {
            predicatesList.add(builder.equal(root.get("currency"), currency));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }
}
