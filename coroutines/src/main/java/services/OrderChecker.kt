package services

import entities.OrderResolution
import entities.PeopleGroup
import kotlinx.coroutines.channels.Channel
import kotlin.concurrent.fixedRateTimer

class OrderChecker(val orderStore: OrderStore,
                   val peopleQueue: Channel<PeopleGroup>,
                   val peopleLeaveQueue: Channel<OrderResolution>) {
    fun scheduleRejected() {
        fixedRateTimer(period = 5000) {
            val order = orderStore.getLatestRejected()
            order?.let {
                peopleQueue.offer(order.peopleGroup)
            }
        }
    }

    fun scheduleAccepted() {
        fixedRateTimer(period = 4000) {
            val order = orderStore.getLatestAccepted()
            order?.let {
                peopleLeaveQueue.offer(order)
            }
        }
    }
}