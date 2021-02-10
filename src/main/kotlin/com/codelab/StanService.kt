package com.codelab

import io.nats.streaming.Options
import io.nats.streaming.StreamingConnection
import io.nats.streaming.StreamingConnectionFactory
import io.quarkus.runtime.Startup
import org.eclipse.microprofile.config.ConfigProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.enterprise.context.ApplicationScoped


class StanService : AutoCloseable {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private val factory: StreamingConnectionFactory
    val connection: StreamingConnection

    init {
        LOGGER.info("Starting nats streaming connection")

        val config = ConfigProvider.getConfig()
        val options = Options.Builder()
            .natsUrl(config.getValue("CLUSTER_URL", String::class.java))
            .clusterId(config.getValue("CLUSTER_ID", String::class.java))
            .clientId(config.getValue("CLUSTER_CLIENT_ID", String::class.java))
            .build()

        this.factory = StreamingConnectionFactory(options)
        this.connection = factory.createConnection()
    }

    @PreDestroy
    override fun close() {
        connection.close()
    }

}
