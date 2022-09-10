package eu.m2rt.reader

import io.javalin.http.Context
import io.javalin.http.Cookie
import org.jsoup.nodes.Element
import java.time.Duration
import java.time.Instant

fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path)?.readText() ?: throw RuntimeException("Missing classpath file: $path")
}

class MissingElementException(identifier: String) : RuntimeException("Missing $identifier")

fun Element.getFirst(selector: String): Element {
    return selectFirst(selector) ?: throw MissingElementException(selector)
}

fun Context.resultHtml(content: String) {
    contentType("text/html; charset=UTF-8")
    result(content)
}

const val YEAR = 60 * 60 * 24 * 365

fun Context.ensureCookie(name: String, valueProvider: () -> String) {
    val existingValue = cookie(name)

    cookie(
        Cookie(
            name = name,
            value = existingValue ?: valueProvider.invoke(),
            maxAge = YEAR
        )
    )
}


class Timer private constructor(private val start: Instant = Instant.now()) {

    companion object {
        fun start() = Timer()
    }

    val millis: Long
        get() = get().toMillis()

    fun get(): Duration = Duration.between(start, Instant.now())
}