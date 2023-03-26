package com.example.dao;

import com.example.model.Account;

import java.util.List;

public interface AccountDAO {
    void create(Account account);
    List<Account> findAll();
    Account get(String id);
    void update(Account account);
}
