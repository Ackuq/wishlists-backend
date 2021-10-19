package io.github.ackuq.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {
    
    install(Locations) {

        
    }

    routing {
        get("/") {
                call.respondText("Hello World!")
            }
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
        
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
