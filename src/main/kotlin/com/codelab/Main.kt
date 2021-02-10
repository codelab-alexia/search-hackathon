package com.codelab

import com.google.gson.Gson
import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.Startup
import io.quarkus.runtime.annotations.QuarkusMain
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@QuarkusMain
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        Quarkus.run(App::class.java, *args)
    }

    @Startup
    @Singleton
    class App : QuarkusApplication {
        val stanService = StanService()
        val mongoService = MongoService()

        override fun run(vararg args: String?): Int {
            stanService.connection.subscribe("SEARCH") { message ->
                println("SEARCH message from broker - ${String(message.data)}")
            }

            Quarkus.waitForExit()
            return 0
        }

    }
}

@Path("/test")
class HelloWorld {

    val gson = Gson()

    @Inject
    lateinit var mainService: Main.App

    @GET
    @Path("/mongo")
    @Produces(MediaType.APPLICATION_JSON)
    fun mongoTest() : String {
        return gson.toJson(mainService.mongoService.getCollection("user").find().map { document ->
            return@map document["name"] as String
        }.collectItems().asList().await().indefinitely())
    }

    @GET
    @Path("/search")
    @Produces(MediaType.TEXT_PLAIN)
    fun search() : String {
        mainService.stanService.connection.publish("SEARCH", "searchString".toByteArray())
        return "ae"
    }
}

