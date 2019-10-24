package com.martmists.pathwatcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

data class PathWatcherConfig(
    val directories: Array<String>
) {
    companion object {
        fun load(): PathWatcherConfig {
            val f = File("config/pathwatcher.yml")
            if (!f.exists()){
                val stream = javaClass.classLoader.getResourceAsStream("pathwatcher.yml")
                if (!f.parentFile.exists()) {
                    f.parentFile.mkdirs()
                }
                f.createNewFile()
                stream!!.copyTo(f.outputStream())
            }

            return try {
                val mapper = ObjectMapper(YAMLFactory())
                mapper.registerModule(KotlinModule())
                mapper.readValue(File("config/pathwatcher.yml"))
            } catch (e: Exception) {
                reset()
            }
        }

        fun reset(): PathWatcherConfig {
            File("config/pathwatcher.yml").delete()
            return load()
        }
    }
}