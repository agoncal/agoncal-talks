package org.expenses.web.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

public abstract class AbstractService<T> implements Serializable {

    @PersistenceContext(unitName = "expenses-pu")
    private EntityManager em;

    private Class<T> entityClass;

    public AbstractService() {
    }

    public AbstractService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public T persist(T entity) {
        em.persist(entity);
        return entity;
    }

    public T findById(Long id) {
        return em.find(entityClass, id);
    }

    public void remove(T entity) {
        em.remove(em.merge(entity));
    }

    public T merge(T entity) {
        return em.merge(entity);
    }

    public List<T> listAll(Integer startPosition, Integer maxResult) {
        TypedQuery<T> findAllQuery = getListAllQuery();
        if (startPosition != null) {
            findAllQuery.setFirstResult(startPosition);
        }
        if (maxResult != null) {
            findAllQuery.setMaxResults(maxResult);
        }
        final List<T> results = findAllQuery.getResultList();
        return results;
    }

    public List<T> listAll() {
        return getListAllQuery().getResultList();
    }

    public TypedQuery<T> getListAllQuery() {
        CriteriaQuery<T> criteria = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        return getEntityManager().createQuery(criteria.select(criteria.from(entityClass)));
    }

    public long count(T example) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();

        // Populate count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<T> root = countCriteria.from(entityClass);
        countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root, example));
        long count = getEntityManager().createQuery(countCriteria).getSingleResult();
        return count;
    }

    public List<T> page(T example, int page, int pageSize) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();

        // Populate pageItems

        CriteriaQuery<T> criteria = builder.createQuery(entityClass);
        Root<T> root = criteria.from(entityClass);
        TypedQuery<T> query = getEntityManager()
                .createQuery(criteria.select(root).where(getSearchPredicates(root, example)));
        query.setFirstResult(page * pageSize).setMaxResults(pageSize);
        List<T> pageItems = query.getResultList();
        return pageItems;

    }

    protected abstract Predicate[] getSearchPredicates(Root<T> root, T example);
}
