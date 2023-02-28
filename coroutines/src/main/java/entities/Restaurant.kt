package entities


data class OrderResolution(val peopleGroup: PeopleGroup, val resolution: Resolution, val table: Table? = null)

enum class Resolution {
    ACCEPT, REJECT
}