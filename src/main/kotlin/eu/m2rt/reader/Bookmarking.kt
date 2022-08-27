package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.core.plugin.Plugin
import io.javalin.http.Context
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Bookmarking(private val bookmarks: Bookmarks) : Plugin {

    override fun apply(app: Javalin) {
        app.routes {
            post("/bookmarks") { save(it) }
        }
    }

    private fun save(ctx: Context) {
        val bookmark = ctx.bodyAsClass<Bookmark>()

        bookmarks.save(bookmark)
    }
}

data class Bookmark(
    val time: Instant = Instant.now(),
    val path: String,
    val text: String
)

interface Bookmarks {
    fun save(bookmark: Bookmark)
}


class FileBookmarks(private val directory: Path) : Bookmarks {
    private val TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm")

    override fun save(bookmark: Bookmark) {
        val filename = TIME_FORMAT.format(bookmark.time.atZone(ZoneOffset.UTC)) +
                "-" +
                bookmark.path.replace('/', '_').removeSurrounding("_") +
                ".txt"

        val file = directory.resolve(filename)

        Files.newBufferedWriter(file).use { out ->
            out.write(bookmark.path);
            out.write("\n")
            out.write(bookmark.time.toString());
            out.write("\n")
            out.write("\n")
            out.write(bookmark.text)
        }
    }
}

