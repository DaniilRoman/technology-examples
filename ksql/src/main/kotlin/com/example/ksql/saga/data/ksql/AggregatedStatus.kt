package com.test.saga.data.ksql

import com.test.saga.data.Status

data class AggregatedStatus(val correlationId: String,
                            val statuses: List<Status>,
                            val sourceServices: List<String>,
                            val targetTopics: List<String>)
