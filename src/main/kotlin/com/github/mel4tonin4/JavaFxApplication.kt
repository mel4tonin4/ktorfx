package com.github.mel4tonin4

import kotlin.random.Random

import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage

class KtorFxApplication : Application() {
    val coroutineScope = CoroutineScope(Dispatchers.JavaFx)

    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            title = "${KtorFxApplication::class.simpleName}"
            scene = Scene(
                VBox().apply {
                    children.setAll(
                        Button().apply {
                            text = "Start service"
                            onAction = EventHandler {
                                coroutineScope.launch(Dispatchers.IO) {
                                    io.ktor.server.netty.EngineMain.main(emptyArray())
                                }
                            }
                        },
                        Button().apply {
                            text = "Broadcast something"
                            onAction = EventHandler {
                                coroutineScope.launch(Dispatchers.IO) {
                                    KtorFxService.broadcast("${Random.nextInt()}")
                                }
                            }
                        }
                    )
                },
                300.0, 250.0
            )
            show()
        }
    }
}

