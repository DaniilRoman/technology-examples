package ru.uskov.dmitry.transferapp.rest.controller;

import ru.uskov.dmitry.http.rest.server.api.HttpMethod;
import ru.uskov.dmitry.http.rest.server.api.annotation.Body;
import ru.uskov.dmitry.http.rest.server.api.annotation.Path;
import ru.uskov.dmitry.http.rest.server.impl.exception.HttpRequestException;
import ru.uskov.dmitry.transferapp.exception.InvalidTransferArgumentException;
import ru.uskov.dmitry.transferapp.rest.controller.dto.transfer.AmountOperationDto;
import ru.uskov.dmitry.transferapp.rest.controller.dto.transfer.TransactionDto;
import ru.uskov.dmitry.transferapp.services.TransferService;
import ru.uskov.dmitry.transferapp.services.exception.AccountNotFoundException;

@Path("/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService service) {
        transferService = service;
    }

    @Path(value = "/replenish", method = HttpMethod.POST)
    public AmountOperationDto replenish(@Body AmountOperationDto request) {
        checkNull(request.getAccountId(), "accountId");
        checkNull(request.getAmount(), "amount");
        try {
            long newAmount = transferService.replenish(request.getAccountId(), request.getAmount());
            return new AmountOperationDto(request.getAccountId(), newAmount);
        } catch (InvalidTransferArgumentException | AccountNotFoundException e) {
            throw new HttpRequestException(400, e.getMessage());
        }
    }

    private void checkNull(Object ob, String argumentName) {
        if(ob == null) {
            throw new HttpRequestException(400, "'" + argumentName + "' mustn't be null");
        }
    }

    @Path(value = "/withdrawing", method = HttpMethod.POST)
    public AmountOperationDto withdrawing(@Body AmountOperationDto request) {
        checkNull(request.getAccountId(), "accountId");
        checkNull(request.getAmount(), "amount");
        try {
            long newAmount = transferService.withdrawing(request.getAccountId(), request.getAmount());
            return new AmountOperationDto(request.getAccountId(), newAmount);
        } catch (InvalidTransferArgumentException | AccountNotFoundException e) {
            throw new HttpRequestException(400, e.getMessage());
        }
    }

    @Path(value = "/transaction", method = HttpMethod.POST)
    public void transaction(@Body TransactionDto request) {
        checkNull(request.getSrcAccountId(), "srcAccountId");
        checkNull(request.getDstAccountId(), "dstAccountId");
        checkNull(request.getAmount(), "amount");
        try {
            transferService.transaction(request.getSrcAccountId(), request.getDstAccountId(), request.getAmount());
        } catch (InvalidTransferArgumentException | AccountNotFoundException e) {
            throw new HttpRequestException(400, e.getMessage());
        }
    }


}
