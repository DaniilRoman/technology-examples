package ru.uskov.dmitry.transferapp;

import org.junit.Assert;
import org.junit.Test;
import ru.uskov.dmitry.transferapp.rest.controller.dto.account.AccountDto;
import ru.uskov.dmitry.transferapp.rest.controller.dto.account.CreateAccountDto;

import java.io.IOException;
import java.util.Date;

public class AccountManagementTest extends AbstractIntegrationTest {

    @Test
    public void createAccountTest() throws IOException {
        AccountDto dto = executeRequest("/accounts/create", "PUT", new CreateAccountDto("TestAccount"), 200, AccountDto.class);
        Assert.assertEquals("TestAccount", dto.getDescription());
        Assert.assertEquals(0l, (long)dto.getAmount());
        Assert.assertTrue(dto.getLastUpdate().before(new Date()));
        Assert.assertTrue(dto.getCreationTime().before(new Date()));
        Assert.assertEquals(dto.getLastUpdate(), dto.getCreationTime());
        Assert.assertFalse(dto.getBlocked());

        executeRequest("/accounts/create", "PUT", new CreateAccountDto(null), 200, AccountDto.class);
    }

    @Test
    public void blockAccount() throws IOException {
        AccountDto dto = executeRequest("/accounts/create", "PUT", new CreateAccountDto("TestAccount"), 200, AccountDto.class);
        executeRequest("/accounts/" + dto.getId() + "/block", "POST", null, 200, null);

        AccountDto dto2 = executeRequest("/accounts/" + dto.getId(), "GET", null, 200, AccountDto.class);
        Assert.assertTrue(dto2.getBlocked());

        executeRequest("/accounts/" + 999 + "/block", "POST", null, 404, null);
    }


    @Test
    public void getAccount() throws IOException {
        AccountDto dto = executeRequest("/accounts/create", "PUT", new CreateAccountDto("TestAccount"), 200, AccountDto.class);

        AccountDto dto2 = executeRequest("/accounts/" + dto.getId(), "GET", null, 200, AccountDto.class);

        Assert.assertEquals(dto.getId(), dto2.getId());
        Assert.assertEquals(dto.getAmount(), dto2.getAmount());
        Assert.assertEquals(dto.getBlocked(), dto2.getBlocked());
        Assert.assertEquals(dto.getCreationTime(), dto2.getCreationTime());
        Assert.assertEquals(dto.getDescription(), dto2.getDescription());
        Assert.assertEquals(dto.getLastUpdate(), dto2.getLastUpdate());

        executeRequest("/accounts/" + 999, "GET", null, 404, null);
    }
}
