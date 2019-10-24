package com.martmists.pathwatcher

import net.fabricmc.api.DedicatedServerModInitializer

class PathWatcherMod : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        PathWatchThread(config.directories).start()
    }

    companion object {
        val config = PathWatcherConfig.load()
    }
}