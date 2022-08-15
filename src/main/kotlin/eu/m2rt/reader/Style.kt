package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.core.plugin.Plugin
import io.javalin.http.Cookie

class Style : Plugin {

    private val cssOptions = listOf("dark", "default")
    private val cssFiles = cssOptions.associateWith { getResourceAsText("/assets/$it.css") }

    override fun apply(app: Javalin) {
        app.before {
            val style = it.cookie("style")

            if (style == null) {
                it.cookie(Cookie("style", "default"))
            }
        }

        app.routes {
            get("/style.css") {
                val style = it.cookie("style") ?: "default"
                it.result(cssFiles[style] ?: cssFiles["default"]!!)
            }
            post("/style") {
                val cookie = it.cookie("style")
                val newStyleIndex = cookie?.let { c -> cssOptions.indexOf(c) + 1 } ?: 0

                if (newStyleIndex == 0 || newStyleIndex >= cssOptions.size) {
                    it.cookie(Cookie("style", cssOptions[0]))
                } else {
                    it.cookie(Cookie("style", cssOptions[newStyleIndex]))
                }
            }
        }
    }
}