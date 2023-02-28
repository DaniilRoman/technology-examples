import entities.OrderResolution
import services.ManagementService
import services.Restaurant
import entities.PeopleGroup
import entities.Table
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import services.OrderChecker
import services.OrderStore

fun main(args: Array<String>): Unit = runBlocking {
    val orderStore = OrderStore()
    val peopleQueue = Channel<PeopleGroup>()
    val tableLeave = Channel<OrderResolution>()

    val restaurant = Restaurant(listOf(Table(4), Table(3)), tableLeave)
    println(restaurant)
    async {
        restaurant.handleTableLeave()
    }



    async {
        addPeoplesTo(peopleQueue)
    }

    async {
        ManagementService(restaurant, peopleQueue, orderStore).handle()
    }

    val scheduler = OrderChecker(orderStore, peopleQueue, tableLeave)
    scheduler.scheduleAccepted()
    scheduler.scheduleRejected()

    println("Done")
}

private suspend fun addPeoplesTo(queue: Channel<PeopleGroup>) {
    listOf(PeopleGroup(4, 3),
            PeopleGroup(2, 2),
            PeopleGroup(3, 2))
            .forEach { group -> queue.send(group) }
}








