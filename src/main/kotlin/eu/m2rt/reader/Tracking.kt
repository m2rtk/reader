package eu.m2rt.reader

import com.fasterxml.jackson.annotation.JsonProperty
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.core.plugin.Plugin
import io.javalin.http.Cookie
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class Tracking(private val trackingDatabase: TrackingDatabase) : Plugin {

    companion object {
        private val log = LoggerFactory.getLogger(Tracking::class.java)
    }

    override fun apply(app: Javalin) {
        app.before {
            val id = it.cookie("reader")

            if (id == null) {
                it.cookie(Cookie("reader", UUID.randomUUID().toString()))
            }
        }

        app.routes {
            post("/report") {
                val reader = it.cookie("reader") ?: return@post
                val data = it.bodyAsClass<ReaderData>()
                val timer = Timer.start()
                trackingDatabase.get(reader).save(data)
                log.info("DATA: {} {} saved in {}ms", reader, data, timer.millis)
            }
        }
    }
}

data class ReaderData(
    @JsonProperty("path")
    val path: String,
    @JsonProperty("t")
    val timestamp: Long,
    @JsonProperty("y")
    val y: Int,
    @JsonProperty("my")
    val maxY: Int,
    @JsonProperty("p")
    val progress: Double,
    @JsonProperty("v")
    val visible: Boolean
)

interface TrackingDatabase {
    fun get(user: String): UserData

    interface UserData {
        fun save(data: ReaderData)
    }
}


class CsvTrackingDatabase(private val directory: Path) : TrackingDatabase {

    private val cache = mutableMapOf<String, CsvFile>()

    override fun get(user: String): TrackingDatabase.UserData {
        return cache.computeIfAbsent(user) {
            val path = directory.resolve("$user.csv")

            if (!Files.exists(path)) {
                Files.createFile(path)
            }

            CsvFile(path)
        }
    }

    inner class CsvFile(private val file: Path) : TrackingDatabase.UserData {

        private val out: BufferedWriter by lazy {
            Files.newOutputStream(file).bufferedWriter()
        }

        override fun save(data: ReaderData) { // todo thread safety
            val visible = if (data.visible) 1 else 0
            val progress = String.format("%.4f", data.progress)
            out.appendLine(
                "${data.timestamp},${data.path},${data.y},${data.maxY},${progress},${visible}"
            )
            out.flush()
        }
    }
}
