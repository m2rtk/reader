package eu.m2rt.reader

import io.javalin.http.Context
import io.javalin.http.Cookie
import org.jsoup.nodes.Element
import java.time.Duration
import java.time.Instant

fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path)?.readText() ?: throw RuntimeException("Missing classpath file: $path")
}

fun Element.getById(id: String): Element {
    return getElementById(id).orThrowMissingException("#$id")
}

fun Element.getFirstByClass(clazz: String): Element {
    return getElementsByClass(clazz).first().orThrowMissingException(".$clazz")
}

fun Element.getFirstByTag(tag: String): Element {
    return getElementsByTag(tag).first().orThrowMissingException(tag)
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

class MissingElementException(identifier: String) : RuntimeException("Missing $identifier")

private fun <T> T?.orThrowMissingException(element: String): T {
    if (this == null) {
        throw MissingElementException(element)
    } else {
        return this
    }
}

class Timer private constructor(private val start: Instant = Instant.now()) {

    companion object {
        fun start() = Timer()
    }

    val millis: Long
        get() = get().toMillis()

    fun get(): Duration = Duration.between(start, Instant.now())
}