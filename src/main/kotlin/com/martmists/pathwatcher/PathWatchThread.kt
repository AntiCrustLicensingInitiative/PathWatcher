package com.martmists.pathwatcher

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.lang.management.ManagementFactory
import kotlin.system.exitProcess


class PathWatchThread(private val paths: Array<String>) : Thread() {
    private var running = true

    override fun start() {
        this.isDaemon = true
        super.start()
    }

    override fun run() {
        val watchService = FileSystems.getDefault().newWatchService()

        paths.forEach { path ->
            println("Watching directory: $path")
            val currentDirectory = File(path)
            val pathToWatch = currentDirectory.toPath()
            val pathKey = pathToWatch.register(
                watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
            )
        }

        val watchKey = watchService.take()

        for (event in watchKey.pollEvents()) {
            sleep(PathWatcherMod.config.restart_delay)

            val server = (FabricLoader.getInstance().gameInstance as MinecraftDedicatedServer)

            if (PathWatcherMod.config.restart_script.isNotBlank())
                ProcessBuilder(PathWatcherMod.config.restart_script).inheritIO().start()
            // Runtime.getRuntime().exec(PathWatcherMod.config.restart_script)
            running = false
            server.stop(false)
            break
        }

        if (!watchKey.reset()) {
            watchKey.cancel()
            watchService.close()
        }
    }
}