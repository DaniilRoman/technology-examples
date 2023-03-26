package ru.uskov.dmitry.transferapp.rest.controller;

import ru.uskov.dmitry.http.rest.server.api.HttpMethod;
import ru.uskov.dmitry.http.rest.server.api.annotation.Body;
import ru.uskov.dmitry.http.rest.server.api.annotation.Path;
import ru.uskov.dmitry.http.rest.server.api.annotation.PathVariable;
import ru.uskov.dmitry.http.rest.server.impl.exception.HttpRequestException;
import ru.uskov.dmitry.transferapp.rest.controller.dto.account.AccountDto;
import ru.uskov.dmitry.transferapp.rest.controller.dto.account.CreateAccountDto;
import ru.uskov.dmitry.transferapp.services.AccountManagementService;
import ru.uskov.dmitry.transferapp.services.exception.AccountNotFoundException;
import ru.uskov.dmitry.transferapp.storage.entity.Account;

@Path("/accounts")
public class AccountManagementController {

    private final AccountManagementService service;

    public AccountManagementController(AccountManagementService service) {
        this.service = service;
    }

    @Path(value = "/create", method = HttpMethod.PUT)
    public AccountDto createAccount(@Body CreateAccountDto request) {
        Account account = service.createAccount(request.getDescription());
        return AccountDto.Companion.create(account);
    }

    @Path(value = "/${id}/block", method = HttpMethod.POST)
    public void blockAccount(@PathVariable("id") long accountId) {
        try {
            service.blockAccount(accountId);
        } catch (AccountNotFoundException e) {
            throw new HttpRequestException(404, "Account with id '" + accountId + " not found");
        }
    }

    @Path(value = "/${id}", method = HttpMethod.GET)
    public AccountDto getAccount(@PathVariable("id") long accountId) {
        try {
            Account account =  service.getAccount(accountId);
            return AccountDto.Companion.create(account);
        } catch (AccountNotFoundException e) {
            throw new HttpRequestException(404, "Account with id '" + accountId + " not found");
        }
    }

}
