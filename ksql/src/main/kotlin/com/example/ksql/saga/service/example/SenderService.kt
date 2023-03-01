package com.test.saga.service.example

import com.test.saga.data.Event
import com.test.saga.data.Status
import java.util.*


class SagaServerService {

    fun registerService() {

    }

    fun unregisterService() {
        // if sender -> all
        // if resender -> only one
    }

    /////////////////////////////////////////////

    fun aggregate() {

    }





}

////////////////////////////////////////////////////////

interface AdminSagaActorInterface {
    fun registerSagaService()
}

interface SagaActorInterface: AdminSagaActorInterface {

    fun doChanges(event: Event)

    fun commitChanges(event: Event)

    fun revertChanges(event: Event)

}

class PizzaOrderService: SagaActorInterface {
    override fun registerSagaService() {
        TODO("Not yet implemented")
    }

    override fun doChanges(event: Event) {
        val correlationId = UUID.randomUUID().toString()
        val orderEvent = Event(
            correlationId,
            Status.NEW,
            "OrderService",
            "order-completed-topic",
            "{}"
        )

//        sentEvent(orderEvent, "order-topic")
    }

    override fun commitChanges(event: Event) {
        TODO("Not yet implemented")
    }

    override fun revertChanges(event: Event) {
        TODO("Not yet implemented")
    }
}

//////////////////////////////////////////////////////////////////

class PizzaCheckIngredientsService: SagaActorInterface {
    override fun registerSagaService() {
        TODO("Not yet implemented")
    }

    override fun doChanges(event: Event) {
        TODO("Not yet implemented")
    }

    override fun commitChanges(event: Event) {
        TODO("Not yet implemented")
    }

    override fun revertChanges(event: Event) {
        TODO("Not yet implemented")
    }

}

class PizzaBookCookService: SagaActorInterface {
    override fun registerSagaService() {
        TODO("Not yet implemented")
    }

    override fun doChanges(event: Event) {
        TODO("Not yet implemented")
    }

    override fun commitChanges(event: Event) {
        TODO("Not yet implemented")
    }

    override fun revertChanges(event: Event) {
        TODO("Not yet implemented")
    }

}