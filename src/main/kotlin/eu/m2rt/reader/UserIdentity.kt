package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.core.plugin.Plugin
import io.javalin.http.Context
import java.util.*

data class User(val id: String)

fun Context.user(): User {
    val readerValue = cookie("reader") ?: throw RuntimeException("No reader cookie value set")
    return User(readerValue)
}

class UserIdentity : Plugin {
    override fun apply(app: Javalin) {
        app.before {
            it.ensureCookie("reader") { UUID.randomUUID().toString() }
        }
    }
}