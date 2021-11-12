package org.hydev

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.hydev.Flowers.personId
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Application.configureRouting()
{
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/flowers/{personId}") {
            val personId = call.parameters["personId"].toString()
            val flowers = Flowers.select { Flowers.personId eq personId }
            call.respondText(flowers.map { it.toString() }.toString())
        }
        get("/flowers-add/{personId}") {
            val id = call.parameters["personId"].toString()

            val sel = Flowers.select { personId eq id }
            if (sel.groupedByColumns.isEmpty())
            {
                transaction {
                    Flowers.insert {
                        it[personId] = id
                        it[flowers] = 0
                    }
                }
            }

            transaction {
                Flowers.update({ personId eq id }) {
                    with(SqlExpressionBuilder) {
                        it[flowers] = flowers + 1
                    }
                }
            }
        }
    }
}
