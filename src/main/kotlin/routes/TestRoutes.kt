package routes

import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.testRoutes(){

    get("/helloworld"){
        call.respondText("Error Generating")
    }
}