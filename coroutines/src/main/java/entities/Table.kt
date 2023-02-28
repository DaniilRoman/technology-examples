package entities

import java.util.*

class Table(private val total: Int,private var available: Int = total) {
    val id: UUID by lazy {
        IdGen.getId()
    }

    fun isContainStrict(n: Int): Boolean {
        return available == total && available >= n
    }

    fun isContain(n: Int): Boolean {
        return available >= n
    }

    fun onAccept(peopleGroup: PeopleGroup) {
        available = available - peopleGroup.count
    }

    fun onLeave(peopleGroup: PeopleGroup) {
        available = available + peopleGroup.count
    }

    override fun toString(): String {
        return "Table: $total:$available"
    }
}