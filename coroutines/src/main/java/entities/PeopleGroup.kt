package entities

import java.util.*

object IdGen {
    fun getId(): UUID {
        return UUID.randomUUID()
    }
}

class PeopleGroup(val count: Int, var timeWaiting: Int,
                  private val threshold: Int = 0,
                  private val onlyEmptyTable: Boolean = false) {
    val id: UUID by lazy {
        IdGen.getId()
    }

    private var table: Table? = null

    fun isAccept(table: Table): Boolean {
        return if (onlyEmptyTable) {
            table.isContainStrict(count)
        } else true
    }

    override fun toString(): String {
        return "Group: {count: $count; timeWaiting: $timeWaiting; threshold: $threshold ::::: Table: $table}"
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as PeopleGroup).id
    }
}