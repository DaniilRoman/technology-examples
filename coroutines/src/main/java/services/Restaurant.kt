package services

import entities.OrderResolution
import entities.PeopleGroup
import entities.Table
import entities.Resolution
import kotlinx.coroutines.channels.Channel

// check free table and return resolution according by people group preferences
class Restaurant(private val tables: List<Table>,
                 private val queue: Channel<OrderResolution>) {

    fun handleOrder(peopleGroup: PeopleGroup): OrderResolution {
        tables.map { table ->
            if (table.isContainStrict(peopleGroup.count)) {
                if (peopleGroup.isAccept(table)) {
                    table.onAccept(peopleGroup)
                    return OrderResolution(peopleGroup, Resolution.ACCEPT, table)
                }
            }
            return@map table
        }.forEach { table ->
            if (table.isContain(peopleGroup.count)) {
                if (peopleGroup.isAccept(table)) {
                    table.onAccept(peopleGroup)
                    return OrderResolution(peopleGroup, Resolution.ACCEPT, table)
                }
            }
        }

        return OrderResolution(peopleGroup, Resolution.REJECT)
    }

    suspend fun handleTableLeave() {
        for(orderResolution in queue) {
            val table = tables.find { it.id == orderResolution.table!!.id }
            table!!.onLeave(orderResolution.peopleGroup)
        }
    }

    override fun toString(): String {
        return "Restaurant: { ${tables.joinToString { table -> table.toString() }} }"
    }
}