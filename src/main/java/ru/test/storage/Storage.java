package ru.test.storage;

import java.util.Collection;

public interface Storage<T> {
    Collection<T> getAll();
    T findById(int id);
    T findByName(String name);
    int add(T value);
    void delete(T value);
    void update(T newvalue);
}
