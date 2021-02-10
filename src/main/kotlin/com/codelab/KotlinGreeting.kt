package com.codelab

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/hello-kotlin")
class KotlinGreeting {

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(@PathParam("name") name: String = "Codelab"): String {
        return "Hello $name"
    }
}