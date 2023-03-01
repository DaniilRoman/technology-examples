package com.test.saga

import com.test.saga.service.ServiceRuleDto
import io.confluent.ksql.api.client.Row
import mu.KotlinLogging
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class SagaStatusSubscriber(val rule: ServiceRuleDto) : Subscriber<Row> {
    private var subscription: Subscription? = null

    private val log = KotlinLogging.logger {}

    @Synchronized
    override fun onSubscribe(subscription: Subscription) {
        log.info("Subscriber is subscribed.")
        this.subscription = subscription
        subscription.request(1)
        log.info("======== SUBSCRIPT =========")
    }

    @Synchronized
    override fun onNext(row: Row) {
        log.info("======== onNext =========")
        val jsonString = row.asObject().toJsonString()
        log.info("Row JSON: $jsonString")
//        try {
//            val statusEntity = OBJECT_MAPPER.readValue(
//                jsonString,
//                StatusTable::class.java
//            )
//            consumedItems.add(statusEntity)
//            val targetService = resolveTargetStatus(statusEntity)
////            val targetTopic = statusEntity.targetTopics.stream().findAny().get() // TODO
//
//            // TODO send to target topic
//            log.info(
//                "Item: {}, targetService: {}",
//                statusEntity,
//                targetService,
////                targetTopic
//            )
//        } catch (e: JsonProcessingException) {
//            log.error("Unable to parse json", e)
//        }

        // Request the next row
        subscription!!.request(1)
    }


    @Synchronized
    override fun onError(t: Throwable) {
        log.error("Received an error", t)
    }

    @Synchronized
    override fun onComplete() {
        log.info("Query has ended.")
    }
}