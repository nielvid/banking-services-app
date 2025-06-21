package com.nielvid.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nielvid.entities.Account;

public class AcountDaoImpl implements Dao<Account, Integer> {

    @Override
    public Optional<Account> findById(Integer id) {
        // Implementation for finding Integerccount by ID
        return Optional.empty();
    }

    @Override
    public Optional<Account> findOne(Integer id) {
        // Implementation for finding one account by ID
        return Optional.empty();
    }

    @Override
    public Account create(Account entity) {
       return new Account();
    }

    @Override
    public void update(Account entity, Integer y) {
        // Implementation for updating an account
    }

    @Override
    public void delete(Integer id) {
        // Implementation for deleting an account
    }

    @Override
    public List<Account> findAll() {
        // Implementation for finding all accounts
        return new ArrayList<>();
    }

}
