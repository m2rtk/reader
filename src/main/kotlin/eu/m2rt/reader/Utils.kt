package eu.m2rt.reader

import org.jsoup.nodes.Element


fun Element.getById(id: String): Element {
    return getElementById(id).orThrowMissingException("#$id")
}

fun Element.getFirstByClass(clazz: String): Element {
    return getElementsByClass(clazz).first().orThrowMissingException(".$clazz")
}

fun Element.getFirstByTag(tag: String): Element {
    return getElementsByTag(tag).first().orThrowMissingException(tag)
}

class MissingElementException(identifier: String) : RuntimeException("Missing $identifier")

private fun <T> T?.orThrowMissingException(element: String): T {
    if (this == null) {
        throw MissingElementException(element)
    } else {
        return this
    }
}
