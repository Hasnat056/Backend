package com.example.ktor_backend

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.serialization.gson.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Database configuration
object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val role = varchar("role", 20) // admin, teacher, student
    override val primaryKey = PrimaryKey(id)
}

fun initDatabase() {
    Database.connect("jdbc:mysql://mysql.railway.internal:3306/railway", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "wrCDXIdmCKgSdVCNFZrajmVWxxpeOMoz")
    transaction {
        SchemaUtils.create(Users)
    }
}

// JWT Authentication Config
const val SECRET = "your_secret_key"
const val ISSUER = "ktor-lms"
const val AUDIENCE = "ktor-users"

fun generateToken(email: String, role: String): String = JWT.create()
    .withIssuer(ISSUER)
    .withAudience(AUDIENCE)
    .withClaim("email", email)
    .withClaim("role", role)
    .sign(Algorithm.HMAC256(SECRET))

fun Application.module() {
    install(ContentNegotiation) { gson {} }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor realm"
            verifier(
                JWT.require(Algorithm.HMAC256(SECRET))
                    .withIssuer(ISSUER)
                    .build()
            )
            validate { credential -> JWTPrincipal(credential.payload) }
        }
    }

    // Initialize database connection
    DatabaseFactory.connect()


    routing {
        post("/login") {
            val request = call.receive<Map<String, String>>()
            val email = request["email"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing email")
            val password = request["password"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing password")

            val user = withContext(Dispatchers.IO) {
                transaction {
                    Users.select { (Users.email eq email) and (Users.password eq password) }.singleOrNull()
                }
            }

            if (user != null) {
                val token = generateToken(email, user[Users.role])
                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

        authenticate("auth-jwt") {
            get("/dashboard") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()
                val role = principal?.payload?.getClaim("role")?.asString()
                call.respond(mapOf("message" to "Welcome $email, Role: $role"))
            }
        }
    }
}

fun main() {
    initDatabase()
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}
