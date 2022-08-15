package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set


fun main(vararg args: String) {
    val configuration = readConfiguration(*args)
    val log = LoggerFactory.getLogger("eu.m2rt.reader")
    val app: Javalin = Javalin.create {
        it.showJavalinBanner = false
        it.enableCorsForAllOrigins()

        it.addStaticFiles { staticFiles ->
            staticFiles.hostedPath = "/"
            staticFiles.directory = "/assets"
            staticFiles.location = Location.CLASSPATH
        }

        it.requestLogger { ctx, executionTimeMs ->
            log.info(
                "{} {} {} -> {} in {}ms",
                ctx.method(),
                ctx.path(),
                ctx.queryParamMap(),
                ctx.status(),
                executionTimeMs
            )
        }

        it.registerPlugin(Style())
        it.registerPlugin(Tracking(CsvTrackingDatabase(Path.of(configuration.trackingDataPath))))
        it.registerPlugin(WanderingInnProxy())
    }

    app.start(configuration.port)
}

data class Configuration(
    val port: Int,
    val trackingDataPath: String,
)

private fun readConfiguration(vararg args: String): Configuration {
    val props = mutableMapOf<String, String>()

    for (arg in args) {
        val (key, value) = arg.split("=", limit = 2)
        props[key.removePrefix("--")] = value
    }

    return Configuration(
        props["port"]?.toInt() ?: 8080,
        props["tracking-data-path"] ?: "",
    )
}
