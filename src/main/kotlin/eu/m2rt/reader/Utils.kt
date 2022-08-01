package eu.m2rt.reader

import io.javalin.http.Context
import org.jsoup.nodes.Element

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

class MissingElementException(identifier: String) : RuntimeException("Missing $identifier")

private fun <T> T?.orThrowMissingException(element: String): T {
    if (this == null) {
        throw MissingElementException(element)
    } else {
        return this
    }
}
