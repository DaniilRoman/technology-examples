package com.test.saga.data

data class Event(val correlationId: String,
                 val status: Status,
                 val sourceService: String,
                 val targetTopic: String,
                 val payload: String)
