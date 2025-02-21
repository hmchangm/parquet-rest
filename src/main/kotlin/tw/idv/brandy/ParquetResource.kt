package tw.idv.brandy

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/parquet")
class ParquetResource(
    val parquetService: ParquetService,
) {
    @POST
    @Path("/query/{queryName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun queryWithParams(
        @RestPath queryName: String,
        body: Map<String, Any>,
    ): Response =
        runCatching {
            parquetService.executeNamedQuery(queryName, body)
        }.fold(
            onSuccess = { Response.ok(it).build() },
            onFailure = { e ->
                when (e) {
                    is IllegalArgumentException ->
                        Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
                    else ->
                        Response
                            .status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error executing query: ${e.message}")
                            .build()
                }
            },
        )

    @POST
    @Path("/refresh")
    @Produces(MediaType.TEXT_PLAIN)
    fun refreshParquetFile(): Response =
        try {
            parquetService.fetchParquetFile()
            Response.ok("Parquet file refreshed successfully").build()
        } catch (e: Exception) {
            Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error refreshing parquet file: ${e.message}")
                .build()
        }
}
