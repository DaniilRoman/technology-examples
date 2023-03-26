package ru.uskov.dmitry.transferapp.rest.controller.dto.transfer

data class AmountOperationDto (
        var accountId: Long? = null,
        var amount: Long? = null
)