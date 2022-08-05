package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.http.Cookie
import io.javalin.http.staticfiles.Location
import org.slf4j.LoggerFactory
import java.util.*

val CSS_OPTIONS = listOf("dark", "default")
val CSS = CSS_OPTIONS.associateWith { getResourceAsText("/assets/$it.css") }

fun main(vararg args: String) {
    val port = args.firstOrNull()?.toInt() ?: 8080
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
    }

    WanderingInnProxy(getResourceAsText("/wandering-inn-wrapper.html")).registerRoutes(app)

    val index = getResourceAsText("/index.html")

    app.before {
        val style = it.cookie("style")

        if (style == null) {
            it.cookie(Cookie("style", "default"))
        }

        val id = it.cookie("reader")

        if (id == null) {
            it.cookie(Cookie("reader", UUID.randomUUID().toString()))
        }
    }

    app.routes {
        get { it.resultHtml(index) }
        get("/style.css") {
            val style = it.cookie("style") ?: "default"
            it.result(CSS[style] ?: CSS["default"]!!)
        }
        post("/style") {
            val cookie = it.cookie("style")
            val newStyleIndex = cookie?.let { c -> CSS_OPTIONS.indexOf(c) + 1 } ?: 0

            if (newStyleIndex == 0 || newStyleIndex >= CSS_OPTIONS.size) {
                it.cookie(Cookie("style", CSS_OPTIONS[0]))
            } else {
                it.cookie(Cookie("style", CSS_OPTIONS[newStyleIndex]))
            }
        }

    }

    app.start(port)
}

