package com.nielvid.dao;
import java.util.List;
import java.util.Optional;

public interface Dao <T, Id extends Object> {
    Optional<T> findById(Id id);
    Optional<T> findOne(Id id);
    T create(T entity);
    void update(T entity, Id id);
    void delete(Id id);
    List<T> findAll();

}
