package com.codelab

import io.quarkus.mongodb.reactive.ReactiveMongoClient
import io.quarkus.mongodb.reactive.ReactiveMongoCollection
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MongoService {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Inject
    lateinit var mongoClient: ReactiveMongoClient

    init {
        LOGGER.info("Starting Mongo service")
    }

    fun getCollection(collectionName: String): ReactiveMongoCollection<Document> {
        return mongoClient.getDatabase("testQuarkus").getCollection(collectionName)
    }
}