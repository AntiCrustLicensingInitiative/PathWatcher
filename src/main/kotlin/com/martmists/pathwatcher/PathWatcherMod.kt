package com.martmists.pathwatcher

import net.fabricmc.api.DedicatedServerModInitializer

class PathWatcherMod : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        val thread = PathWatchThread(config.directories)
        thread.isDaemon = true
        thread.start()
    }

    companion object {
        val config = PathWatcherConfig.load()
    }
}