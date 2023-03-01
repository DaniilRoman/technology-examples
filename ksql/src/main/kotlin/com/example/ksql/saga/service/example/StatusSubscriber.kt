package com.test.saga.service.example

import io.confluent.ksql.api.client.Row
import mu.KotlinLogging
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class StatusSubscriber: Subscriber<Row> {
    val log = KotlinLogging.logger {}

    override fun onSubscribe(p0: Subscription?) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: Throwable?) {
        TODO("Not yet implemented")
    }

    override fun onComplete() {
        TODO("Not yet implemented")
    }

    override fun onNext(p0: Row?) {
        TODO("Not yet implemented")
    }
}