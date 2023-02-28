package services

import entities.PeopleGroup
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import utils.log

// get people group from queue and serve to restaurant
class ManagementService(val restaurant: Restaurant,
                        val queue: Channel<PeopleGroup>,
                        val orderStore: OrderStore) {

    suspend fun handle() {
        for(peopleGroup in queue) {
            val orderResolution = restaurant.handleOrder(peopleGroup)
            orderStore.store(orderResolution)

            log("handled: $orderResolution")
        }
    }

}
