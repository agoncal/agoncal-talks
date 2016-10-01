package org.expenses.web.service;

import org.expenses.web.beans.Service;
import org.expenses.web.model.User;
import org.expenses.web.beans.DigestPassword;
import org.expenses.web.beans.Encrypted;

import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Transactional service for User entities.
 * <p/>
 * This class provides CRUD functionality for all User entities.
 */

@Service
public class UserService extends AbstractService<User> {

    @Inject
    @Encrypted
    private DigestPassword digestPassword;

    public UserService() {
        super(User.class);
    }

    @Override
    public User persist(User entity) {
        entity.setPassword(digestPassword.digest(entity.getPassword()));
        return super.persist(entity);
    }

    public User findByLoginPassword(String login, String password) {
        TypedQuery<User> query = getEntityManager().createNamedQuery(User.FIND_BY_LOGIN_PASSWORD, User.class);
        query.setParameter("login", login);
        query.setParameter("password", password);
        return query.getSingleResult();
    }

    public List<User> findByLogin(String login) {
        return getEntityManager().createNamedQuery(User.FIND_BY_LOGIN, User.class).setParameter("login", login)
                .getResultList();
    }

    @Override
    protected Predicate[] getSearchPredicates(Root<User> root, User example) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<>();

        String login = example.getLogin();
        if (login != null && !"".equals(login)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("login")),
                    '%' + login.toLowerCase() + '%'));
        }
        String password = example.getPassword();
        if (password != null && !"".equals(password)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("password")),
                    '%' + password.toLowerCase() + '%'));
        }
        String name = example.getName();
        if (name != null && !"".equals(name)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("name")),
                    '%' + name.toLowerCase() + '%'));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }
}
