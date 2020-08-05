package org.example.service;

import org.example.parameter.SimpleFilterResolver;
import org.example.repository.BaseRepository;
import org.example.repository.EntityBase;
import org.example.util.BeanCopyUtil;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseService<T extends EntityBase, ID extends Serializable> {

    public abstract BaseRepository<T, ID> repository();

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional(readOnly = true)
    public Page<T> filter(SimpleFilterResolver<T> filterResolver) {
        return repository().findAll(filterResolver.genSpecification(), filterResolver.genPageRequest());
    }

    @Transactional
    public T save(T entity) {
        entity.setCreateTime(new Date());
        return repository().save(entity);
    }

    @Transactional
    public T update(T entity) {
        EntityBase entityBase = getEntityManager().find(entity.getClass(),entity.getId());
        BeanCopyUtil.beanCopy(entity,entityBase);
        entityBase.setUpdateTime(new Date());
        getEntityManager().merge(entityBase);
        getEntityManager().flush();
        return (T) entityBase;
    }
}
