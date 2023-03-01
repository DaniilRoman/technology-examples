package com.test.saga.service.example

import com.test.saga.data.ksql.AggregatedStatus
import com.test.saga.data.Status

fun AggregatedStatus.resolveStatus(): Status {
    val allAccepted = this.statuses.all { it == Status.ACCEPT }
    return if (allAccepted) {
        Status.ACCEPT
    } else {
        Status.REJECTED
    }
}

fun AggregatedStatus.resolveTargetStatus() = this.targetTopics.toSet()
