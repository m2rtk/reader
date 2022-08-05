package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import org.slf4j.LoggerFactory


fun main(vararg args: String) {
    val port = args.firstOrNull()?.toInt() ?: 8080
    val log = LoggerFactory.getLogger("eu.m2rt.reader")
    val app: Javalin = Javalin.create {
        it.showJavalinBanner = false
        it.enableCorsForAllOrigins()

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

    app.routes {
        get { it.resultHtml(index) }
    }

    app.start(port)
}

