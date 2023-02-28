package utils

import entities.PeopleGroup
import entities.Table
import kotlin.random.Random


fun generatePeoples(n: Int): List<PeopleGroup> {
    val groups = mutableListOf<PeopleGroup>()
    for (i in 1..n) {
        groups.add(generatePeople())
    }

    return groups
}

fun generatePeople(): PeopleGroup {
    val count = Random.nextInt(1, 5)
    val threshold = Random.nextInt(5, 15)
    return PeopleGroup(count, threshold)
}

fun generateTables(n: Int): List<Table> {
    val tables = mutableListOf<Table>()
    for (i in 1..n) {
        tables.add(generateTable())
    }

    return tables
}

fun generateTable(): Table{
    val total = Random.nextInt(1, 7)
    return Table(total)
}