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
        List<?> res = template.find("from User where id=?",id);
        if(res.size()>0) return (User)res.get(0);
        else return null;
    }

    @Override
    public User findByName(String name) {
        List<?> res = template.find("from User where userName=?",name);
        if(res.size()>0) return (User)res.get(0);
        else return null;
    }

    @Transactional
    @Override
    public int add(User value) {
        return (int)template.save(value);
    }

    @Override
    public void delete(User value) {
        template.delete(value);
    }

    @Transactional
    @Override
    public void update(User newValue) {
        template.update(newValue);
    }
}
