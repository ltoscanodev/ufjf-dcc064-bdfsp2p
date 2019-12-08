package br.bdfs.lib.model.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ltosc
 * @param <T>
 */
public abstract class AbstractJpaController <T extends Serializable>
{
    private final Class<T> entityClass;
    
    @PersistenceContext(unitName = "bdfsPU")
    private EntityManager entityManager;
    
    public AbstractJpaController(Class<T> entityClass)
    {
        this.entityClass = entityClass;
    }
    
    private List<T> findEntities(boolean all, int maxResults, int firstResult)
            throws IllegalArgumentException, IllegalStateException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = entityManager.createQuery(cq);

        if (!all) 
        {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }

        return q.getResultList();
    }

    public final EntityManager getEntityManager() 
    {
        return entityManager;
    }
    
    @Transactional
    public void create(Object entity)
            throws IllegalArgumentException, EntityExistsException, TransactionRequiredException, PersistenceException
    {
        entityManager.persist(entity);
        entityManager.flush();
    }
    
    @Transactional
    public void edit(Object entity)
            throws IllegalArgumentException, TransactionRequiredException, PersistenceException
    {
        entityManager.merge(entity);
        entityManager.flush();
    }
    
    @Transactional
    public void remove(Object id)
            throws IllegalArgumentException, EntityNotFoundException, TransactionRequiredException, PersistenceException
    {
        T entity = findEntity(id);
        entityManager.remove(entity);
        entityManager.flush();
    }
    
    public T findEntity(Object id)
            throws IllegalArgumentException
    {
        return entityManager.find(entityClass, id);
    }
    
    public List findAllEntities() 
    {
        return findEntities(true, -1, -1);
    }
    
    public int getEntityCount()
            throws IllegalArgumentException, NoResultException, NonUniqueResultException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(entityManager.getCriteriaBuilder().count(rt));
        Query q = entityManager.createQuery(cq);

        return ((Long) q.getSingleResult()).intValue();
    }
    
    public <T> T findByNamedQuerySingle(String namedQuery, String parameterName, Object parameter)
            throws IllegalArgumentException, NoResultException, NonUniqueResultException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        TypedQuery<T> q = entityManager.createNamedQuery(namedQuery, (Class<T>) entityClass);
        q.setParameter(parameterName, parameter);

        return q.getSingleResult();
    }
    
    public <T> T findByNamedQuerySingle(String namedQuery, HashMap<String, Object> paramList)
            throws IllegalArgumentException, NoResultException, NonUniqueResultException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        TypedQuery<T> q = entityManager.createNamedQuery(namedQuery, (Class<T>) entityClass);

        Set<String> keyList = paramList.keySet();

        keyList.forEach((paramName) ->
        {
            q.setParameter(paramName, paramList.get(paramName));
        });

        return q.getSingleResult();
    }
    
    public <T> List<T> findByNamedQueryList(String namedQuery)
            throws IllegalArgumentException, NoResultException, NonUniqueResultException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        TypedQuery<T> q = entityManager.createNamedQuery(namedQuery, (Class<T>) entityClass);
        return q.getResultList();
    }
    
    public <T> List<T> findByNamedQueryList(String namedQuery, String parameterName, Object parameter) 
            throws IllegalArgumentException, NoResultException, NonUniqueResultException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        TypedQuery<T> q = entityManager.createNamedQuery(namedQuery, (Class<T>) entityClass);
        q.setParameter(parameterName, parameter);

        return q.getResultList();
    }
    
    public <T> List<T> findByNamedQueryList(String namedQuery, HashMap<String, Object> paramList)
            throws IllegalArgumentException, NoResultException, NonUniqueResultException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        TypedQuery<T> q = entityManager.createNamedQuery(namedQuery, (Class<T>) entityClass);

        Set<String> keyList = paramList.keySet();

        keyList.forEach((paramName) -> {
            q.setParameter(paramName, paramList.get(paramName));
        });

        return q.getResultList();
    }
    
    public Object executeNativeQuery(String nativeQuery)
            throws IllegalArgumentException, NoResultException, NonUniqueResultException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        Query q = entityManager.createNativeQuery(nativeQuery);
        return q.getSingleResult();
    }
    
    public Object executeNativeQuery(String nativeQuery, HashMap<String, Object> paramList)
            throws IllegalArgumentException, NoResultException, NonUniqueResultException, QueryTimeoutException, 
            TransactionRequiredException, PessimisticLockException, LockTimeoutException,
            PersistenceException
    {
        Query q = entityManager.createNativeQuery(nativeQuery);
        
        Set<String> keyList = paramList.keySet();

        keyList.forEach((paramName) -> 
        {
            q.setParameter(paramName, paramList.get(paramName));
        });
        
        return q.getSingleResult();
    }
}