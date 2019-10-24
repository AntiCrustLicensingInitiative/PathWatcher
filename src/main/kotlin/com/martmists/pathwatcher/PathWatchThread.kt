package com.martmists.pathwatcher

import net.minecraft.server.dedicated.MinecraftDedicatedServer
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.lang.management.ManagementFactory
import kotlin.system.exitProcess


class PathWatchThread(val path: String) : Thread() {
    override fun run() {
        val currentDirectory  = File(path)
        val watchService = FileSystems.getDefault().newWatchService()
        val pathToWatch = currentDirectory.toPath()

        val pathKey = pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE)

        while (true) {
            val watchKey = watchService.take()

            for (event in watchKey.pollEvents()) {
                val cmd = StringBuilder()
                cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ")
                for (jvmArg in ManagementFactory.getRuntimeMXBean().inputArguments) {
                    cmd.append("$jvmArg ")
                }
                val jarpath = MinecraftDedicatedServer::class.java.protectionDomain.codeSource.location.path
                cmd.append(jarpath)
                sleep(10000) // 10 seconds delay before restart
                Runtime.getRuntime().exec(cmd.toString())
                exitProcess(0)
            }

            if (!watchKey.reset()) {
                watchKey.cancel()
                watchService.close()
                break
            }
        }
    }
}