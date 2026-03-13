
import config.connectToDatabase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationFailedCause
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory
import routes.matchRoutes
import routes.testRoutes
import java.security.KeyStore
import kotlin.time.Duration.Companion.seconds


//qIjNttqlsU_i_1B22gH9e3Bw0ugbFdGCIIxrGv0N-Te0d1OElK_dMCpvLjI-K6q4ECBpdWW62RcgVg for my acc uuid



private fun ApplicationEngine.Configuration.envConfig() {
//    val keyAlias: String = System.getenv("keyAlias") ?: ""
//    val jksPass: String = System.getenv("jksPass") ?: ""
//
//    val keyStore = KeyStore.getInstance("JKS").apply {
//        val keystoreStream = object {}.javaClass.getResourceAsStream("/certs/keystore.jks")
//        requireNotNull(keystoreStream) { "Keystore not found in resources!" }
//        keystoreStream.use {
//            load(it, jksPass.toCharArray())
//        }
//    }
//        sslConnector(
//        keyStore = keyStore,
//        keyAlias = keyAlias,
//        keyStorePassword = { jksPass.toCharArray() },
//        privateKeyPassword = { jksPass.toCharArray() }) {
//        port = 8443
//    }

    connector {
        host = "127.0.0.1"
        port = 9090
    }



}

fun main() {

    val tempApikey =System.getenv("apiKey")


    embeddedServer(Netty, applicationEnvironment { log = LoggerFactory.getLogger("ktor.application") }, {envConfig()}) {
        install(Authentication) {
            provider("apiKey") {
                authenticate { context ->
                    val apiKey = context.call.request.headers["apiKey"]
                    if (apiKey == tempApikey) {
                        context.principal(UserIdPrincipal("api-user"))
                    } else {
                        context.challenge("apiKey", AuthenticationFailedCause.InvalidCredentials) { challenge, call ->
                            call.respond(HttpStatusCode.Unauthorized, "Invalid or missing API key")
                            challenge.complete()
                        }
                    }
                }
            }
        }
        install(RateLimit) {
            register(RateLimitName("protected")) {
                rateLimiter(limit = 20, refillPeriod = 60.seconds)
            }
        }
        connectToDatabase()
        routing {
            // Thread-safe set for all connected sessions
            // Launch a coroutine for periodic broadcasting
            authenticate("apiKey") {
                rateLimit(RateLimitName("protected")){
                    //routes here
                    testRoutes()
                    matchRoutes()
                }
            }
        }

    }.start(wait = true)
}

