package ru.uskov.dmitry.transferapp;

import org.junit.Assert;
import org.junit.Test;
import ru.uskov.dmitry.transferapp.rest.controller.dto.account.AccountDto;
import ru.uskov.dmitry.transferapp.rest.controller.dto.account.CreateAccountDto;
import ru.uskov.dmitry.transferapp.rest.controller.dto.transfer.AmountOperationDto;
import ru.uskov.dmitry.transferapp.rest.controller.dto.transfer.TransactionDto;

import java.io.IOException;

public class TransferTest extends AbstractIntegrationTest {

    @Test
    public void replenish() throws IOException {
        AccountDto dto = executeRequest("/accounts/create", "PUT", new CreateAccountDto("TestAccount"), 200, AccountDto.class);

        AmountOperationDto operationResult = executeRequest("/transfer/replenish", "POST", new AmountOperationDto(dto.getId(), 200L), 200, AmountOperationDto.class);
        Assert.assertEquals(dto.getId(), operationResult.getAccountId());
        Assert.assertEquals(200L, (long) operationResult.getAmount());

        AccountDto dto2 = executeRequest("/accounts/" + dto.getId(), "GET", null, 200, AccountDto.class);
        Assert.assertEquals(200L, (long) dto2.getAmount());

        //corner cases
        executeRequest("/transfer/replenish", "POST", new AmountOperationDto(dto.getId(), Long.MAX_VALUE - 100), 400, null);
        executeRequest("/transfer/replenish", "POST", new AmountOperationDto(dto.getId(), -100L), 400, null);
        executeRequest("/transfer/replenish", "POST", new AmountOperationDto(dto.getId(), null), 400, null);
        executeRequest("/transfer/replenish", "POST", new AmountOperationDto(null, 100L), 400, null);
        executeRequest("/transfer/replenish", "POST", new AmountOperationDto(999L, 100L), 400, null);
    }


    @Test
    public void withdrawing() throws IOException {
        AccountDto dto = executeRequest("/accounts/create", "PUT", new CreateAccountDto("TestAccount"), 200, AccountDto.class);

        executeRequest("/transfer/replenish", "POST", new AmountOperationDto(dto.getId(), 300L), 200, AmountOperationDto.class);

        AmountOperationDto operationResult = executeRequest("/transfer/withdrawing", "POST", new AmountOperationDto(dto.getId(), 100L), 200, AmountOperationDto.class);
        Assert.assertEquals(dto.getId(), operationResult.getAccountId());
        Assert.assertEquals(200L, (long) operationResult.getAmount());

        AccountDto dto2 = executeRequest("/accounts/" + dto.getId(), "GET", null, 200, AccountDto.class);
        Assert.assertEquals(200L, (long) dto2.getAmount());

        //corner cases
        //invalid amount
        executeRequest("/transfer/withdrawing", "POST", new AmountOperationDto(dto.getId(), 300L), 400, null);
        executeRequest("/transfer/withdrawing", "POST", new AmountOperationDto(dto.getId(), -100L), 400, null);

        //null
        executeRequest("/transfer/withdrawing", "POST", new AmountOperationDto(dto.getId(), null), 400, null);
        executeRequest("/transfer/withdrawing", "POST", new AmountOperationDto(null, 100L), 400, null);


        //not exist accounts
        executeRequest("/transfer/withdrawing", "POST", new AmountOperationDto(999L, 100L), 400, null);
    }


    @Test
    public void transfer() throws IOException {
        AccountDto account1 = executeRequest("/accounts/create", "PUT", new CreateAccountDto("TestAccount"), 200, AccountDto.class);
        AccountDto account2 = executeRequest("/accounts/create", "PUT", new CreateAccountDto("TestAccount"), 200, AccountDto.class);


        executeRequest("/transfer/replenish", "POST", new AmountOperationDto(account1.getId(), 1000L), 200, AmountOperationDto.class);
        executeRequest("/transfer/replenish", "POST", new AmountOperationDto(account2.getId(), 1000L), 200, AmountOperationDto.class);

        executeRequest("/transfer/transaction", "POST", new TransactionDto(account1.getId(), account2.getId(), 500L), 200, null);

        account1 = executeRequest("/accounts/" + account1.getId(), "GET", null, 200, AccountDto.class);
        account2 = executeRequest("/accounts/" + account2.getId(), "GET", null, 200, AccountDto.class);
        Assert.assertEquals(500L, (long) account1.getAmount());
        Assert.assertEquals(1500L, (long) account2.getAmount());


        //invalid amount
        executeRequest("/transfer/transaction", "POST", new TransactionDto(account1.getId(), account2.getId(), -100L), 400, null);
        executeRequest("/transfer/transaction", "POST", new TransactionDto(account1.getId(), account2.getId(), Long.MAX_VALUE-1), 400, null);

        //null
        executeRequest("/transfer/transaction", "POST", new TransactionDto(null, account2.getId(), 100L), 400, null);
        executeRequest("/transfer/transaction", "POST", new TransactionDto(account1.getId(), null, 100L), 400, null);

        //same accountIds
        executeRequest("/transfer/transaction", "POST", new TransactionDto(account1.getId(), account1.getId(), 100L), 400, null);

        //not exist accounts
        executeRequest("/transfer/transaction", "POST", new TransactionDto(999L, account2.getId(), 100L), 400, null);
        executeRequest("/transfer/transaction", "POST", new TransactionDto(account1.getId(), 999L, 100L), 400, null);

    }
}
