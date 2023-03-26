package com.example.config;

import com.google.inject.AbstractModule;
import com.example.dao.AccountDAO;
import com.example.dao.AccountDAOImpl;
import com.example.dao.TransactionDAO;
import com.example.dao.TransactionDAOImpl;
import com.example.service.AccountService;
import com.example.service.TransactionHistoryService;
import com.example.service.TransferService;

public class BurlesqueModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountDAO.class).to(AccountDAOImpl.class).asEagerSingleton();
        bind(TransactionDAO.class).to(TransactionDAOImpl.class).asEagerSingleton();
        requestStaticInjection(AccountService.class);
        requestStaticInjection(TransactionHistoryService.class);
        requestStaticInjection(TransferService.class);
    }
}
