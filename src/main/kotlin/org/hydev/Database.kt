package org.hydev

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database

fun startDatabase()
{
    Database.connect("jdbc:h2:file:./database.h2", driver = "org.h2.Driver")
}

object Flowers: IntIdTable() {
    val personId = varchar("person_id", 50)
    val flowers = long("flowers")
}
