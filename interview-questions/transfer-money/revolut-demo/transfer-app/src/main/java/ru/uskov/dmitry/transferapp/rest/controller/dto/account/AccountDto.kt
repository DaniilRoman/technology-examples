package ru.uskov.dmitry.transferapp.rest.controller.dto.account

import ru.uskov.dmitry.transferapp.storage.entity.Account
import java.util.*

data class AccountDto(
        var id: Long? = null,
        var description: String? = null,
        var amount: Long? = null,
        var lastUpdate: Date? = null,
        var creationTime: Date? = null,
        var blocked: Boolean? = null
) {
    companion object {
        fun create(account: Account): AccountDto {
            return AccountDto(
                    account.id,
                    account.description,
                    account.amount,
                    account.lastUpdate,
                    account.creationTime,
                    account.blocked
            )
        }
    }
}

