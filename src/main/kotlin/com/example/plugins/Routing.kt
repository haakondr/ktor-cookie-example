package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.hex

fun Application.configureRouting() {
    data class SessionCleartext(val text: String)
    data class SessionEncrypted(val text: String)

    install(Sessions) {
        cookie<SessionCleartext>("session_cleartext")
        cookie<SessionEncrypted>("session_encrypted") {
            val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
            val secretSignKey = hex("6819b57a326945c1968f45236589")
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    routing {
        get("/") {
            val cookieContent = "This is my cookie! There are many like it, but this one is mine"
            call.sessions.set("session_cleartext", SessionCleartext(cookieContent))
            call.sessions.set("session_encrypted", SessionEncrypted(cookieContent))

            call.respondText("cookies updated, check devtools -> application -> Cookies")
        }
    }
}
