package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.plugin.Plugin
import io.javalin.http.Context
import org.jsoup.Jsoup
import java.net.URL

class WanderingInnProxy(
    private val wrapperHtml: String = getResourceAsText("/wandering-inn-wrapper.html")
): Plugin {

    override fun apply(app: Javalin) {
        app.routes {
            ApiBuilder.get("/wandering-inn") {
                it.renderWanderingInn("https://wanderinginn.com/table-of-contents/")
            }

            ApiBuilder.get("/wandering-inn/*") {
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