package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.http.Context
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.net.URL


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

    WanderingInnProxy(
        app,
        getResourceAsText("/wandering-inn-wrapper.html"),
    )

    val index = getResourceAsText("/index.html")

    app.routes {
        get("/favicon.ico") { ctx -> ctx.status(404) }
        get { it.resultHtml(index) }
    }

    app.start(port)
}

class WanderingInnProxy(
    app: Javalin,
    private val wrapperHtml: String
) {

    init {
        app.routes {
            get("/wandering-inn") {
                it.renderWanderingInn("https://wanderinginn.com/table-of-contents/")
            }

            get("/wandering-inn/*") {
                it.renderWanderingInn("https://wanderinginn.com/${it.path().removePrefix("/wandering-inn/")}")
            }
        }
    }

    private fun Context.renderWanderingInn(originalUrl: String) {
        val document = Jsoup.parse(
            URL(originalUrl),
            10_000
        )

        val content = document.body()
            .getById("content")
            .getFirstByTag("article")

        content.select("a[href]").forEach { a ->
            a.attr(
                "href",
                a.attr("href")
                    .replaceFirst("https://wanderinginn.wordpress.com/", "/wandering-inn/")
                    .replaceFirst("https://wanderinginn.com/", "/wandering-inn/")
            )
        }

        resultHtml(
            wrapperHtml
                .replace("\${ORIGINAL_URL}", originalUrl)
                .replace("\${CONTENT}", content.outerHtml())
        )
    }
}
