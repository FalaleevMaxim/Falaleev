package ru.test.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public class UserStorage implements Storage<User> {
    private final HibernateTemplate template;

    @Autowired
    public UserStorage(HibernateTemplate template) {
        this.template = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<User> getAll() {
        return (List<User>)template.find("from User");
    }

    @Override
    public User findById(int id) {
        return null;
    }

    @Override
    public User findByName(String name) {
        return null;
    }

    @Transactional
    @Override
    public int add(User value) {
        return (int)template.save(value);
    }

    @Override
    public void delete(User value) {

    }

    @Transactional
    @Override
    public void update(User newvalue) {
        template.update(newvalue);
    }
}
