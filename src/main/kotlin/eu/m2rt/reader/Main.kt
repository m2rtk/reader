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
    val app = Javalin.create {
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

    app.routes {
        get("/favicon.ico") { ctx ->
            URL("https://wanderinginn.com/favicon.ico").openStream().use { ctx.result(it) }
        }

        get("/wandering-inn") {
            it.renderWanderingInn("https://wanderinginn.com/table-of-contents/")
        }

        get("/wandering-inn/*") {
            it.renderWanderingInn("https://wanderinginn.com/${it.path().removePrefix("/wandering-inn/")}")
        }
    }

    app.start(port)
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

    contentType("text/html; charset=UTF-8")
    result(
        """
            <style>
            body {
              font: 13px Arial;
              line-height: 1.625;
              padding: 0 0.6em;
              margin: 0;
            }
            p {
              margin-bottom: 1.625em;
            }
            #content {
              margin: 0 7.6%;
            }
            .entry-content {
              padding: 1.625em 0 0;
            }
            .entry-title {
              font-size: 28px;
              font-weight: bold;
            }
            </style>
            <a href=\"$originalUrl\">original</a>
            <div id="content">
            ${content.outerHtml()}
            </div>
        """.trimIndent()
    )
}
