package com.martmists.pathwatcher

import net.fabricmc.api.DedicatedServerModInitializer

class PathWatcherMod : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        config.directories.forEach {
            PathWatchThread(it).start()
        }
    }

    companion object {
        val config = PathWatcherConfig.load()
    }
}