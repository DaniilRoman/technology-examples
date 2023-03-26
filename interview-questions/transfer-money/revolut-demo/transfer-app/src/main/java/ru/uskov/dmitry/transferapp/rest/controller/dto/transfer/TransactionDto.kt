package ru.uskov.dmitry.transferapp.rest.controller.dto.transfer

data class TransactionDto(
        var srcAccountId: Long? = null,
        var dstAccountId: Long? = null,
        var amount: Long? = null
)