package com.nielvid.dao;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.nielvid.entities.User;
import com.nielvid.model.AccountType;

public interface AccountDao <T, Id extends Object> {
    Optional<T> findOne(Id id) throws SQLException;
    T create(User user, AccountType accountType) throws SQLException;
    void update(T entity, Id id);
    void delete(Id id);
    List<T> findAll()throws SQLException;

}
