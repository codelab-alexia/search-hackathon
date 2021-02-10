package com.codelab.storage

import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.quarkus.mongodb.reactive.ReactiveMongoCollection
import io.quarkus.runtime.Startup
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PreDestroy
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.inject.Singleton

@Startup
@Singleton
class MongoService : AutoCloseable {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Inject
    @field: Default
    lateinit var mongoClient: ReactiveMongoClient

    init {
        LOGGER.info("Starting Mongo service")
    }

    fun getCollection(collectionName: String): ReactiveMongoCollection<Document> {
        return mongoClient.getDatabase("testQuarkus").getCollection(collectionName)
    }

    @PreDestroy
    override fun close() {
        mongoClient.close()
    }
}