package com.github.mel4tonin4

import java.util.*

import kotlin.collections.LinkedHashSet

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

import io.ktor.application.Application

fun Application.module() {
    install(WebSockets)
    routing {
        webSocket("/ktor_fx") {
            val connection = Connection(this)

            KtorFxService.connections += connection

            try {
                connection.send("You are connected!")
                for(frame in incoming) {
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()

                        connection.send("You said: \"$receivedText\"")
                        connection.send("${KtorFxService::class.simpleName}.connectionCount = ${KtorFxService.connectionCount}")
                    }
                }
            } finally {
                KtorFxService.connections -= connection
            }
        }
    }
}

class Connection(
    val session: DefaultWebSocketServerSession
) {
    suspend fun send(text: String) {
        session.send(text)
    }
}

object KtorFxService {
    init {
        println("Creating a new ${KtorFxService::class.simpleName}")
    }
    val connections = Collections.synchronizedSet<Connection>(LinkedHashSet())
    val connectionCount get() = connections.size

    suspend fun broadcast(text: String) {
        println("Broadcasting \"$text\" to ${connections.size} clients")
        // We should synchronize(connection) but we can't because send is a suspension point
        // and can't be inside a synchronize block.
        // This is another issue
        connections.forEach { it.send(text) }
    }
}
