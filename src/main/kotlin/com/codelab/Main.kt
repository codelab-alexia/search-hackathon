package com.codelab

import com.codelab.broker.StanService
import com.codelab.storage.MongoService
import com.google.gson.Gson
import com.mongodb.client.MongoCollection
import io.quarkus.mongodb.reactive.ReactiveMongoCollection
import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.Startup
import io.quarkus.runtime.annotations.QuarkusMain
import org.bson.*
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
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
        val gson = Gson()

        @Inject
        @field: Default
        lateinit var stanService: StanService

        @Inject
        @field: Default
        lateinit var mongoService: MongoService

        override fun run(vararg args: String?): Int {
            stanService.connection.subscribe("NEW_HACKATHON_CREATED") { message ->
                val hackathon = gson.fromJson(String(message.data), Map::class.java) as Map<String, Any?>
                mongoService.saveEvents("hackathons", listOf(hackathon))
                println("New hackathon created: - ${String(message.data)}")
            }

            Quarkus.waitForExit()
            return 0
        }

    }
}

@Path("/test")
class TestResource {

    val gson = Gson()

    @Inject
    @field: Default
    lateinit var mainService: Main.App

    @GET
    @Path("/mongo")
    @Produces(MediaType.APPLICATION_JSON)
    fun mongoTest() : String {
        return gson.toJson(getHackathonsCollection().find().map { document ->
            return@map document.toMap()
        }.collectItems().asList().await().indefinitely())
    }

    @GET
    @Path("/search")
    fun searchTest(@QueryParam("name") name: String) : String {
        val document = BsonDocument().append("name", BsonString(name))

        val result = getHackathonsCollection().find(document).collectItems().first().await().indefinitely()
        return result.toString()
    }

    @GET
    @Path("/broker")
    @Produces(MediaType.TEXT_PLAIN)
    fun search(@QueryParam("s") value: String?) : String {
        mainService.stanService.connection.publish("SEARCH", (value?:"empty query param").toByteArray())
        return "Olha o log da aplicacao, cara"
    }

    private fun getHackathonsCollection() : ReactiveMongoCollection<Document> {
        return mainService.mongoService.getCollection("hackathons")
    }
}

