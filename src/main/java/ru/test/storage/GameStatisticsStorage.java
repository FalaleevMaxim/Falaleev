package ru.test.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.test.model.GameStatistics;

import java.util.Collection;
import java.util.List;

@Repository
public class GameStatisticsStorage implements Storage<GameStatistics>{
    private final HibernateTemplate template;

    @Autowired
    public GameStatisticsStorage(HibernateTemplate template) {
        this.template = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GameStatistics> getAll() {
        return (List<GameStatistics>)template.find("from GameStatistics");
    }

    @Override
    public GameStatistics findById(int id) {
        List<?> res = template.find("from GameStatistics where id=?",id);
        if(res.size()>0) return (GameStatistics) res.get(0);
        else return null;
    }

    @Override
    public GameStatistics findByName(String name) {
        List<?> res = template.find("from GameStatistics where user_id=(select id from User where userName=?)",name);
        if(res.size()>0) return (GameStatistics) res.get(0);
        else return null;
    }

    @Transactional
    @Override
    public int add(GameStatistics value) {
        return (int)template.save(value);
    }

    @Override
    public void delete(GameStatistics value) {
        template.delete(value);
    }

    @Transactional
    @Override
    public void update(GameStatistics newvalue) {
        template.update(newvalue);
    }
}
