package tw.idv.brandy

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestPath

@Path("/query")
class QueryResource(
    val parquetService: ParquetService,
) {
    @POST
    @Path("/{queryName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun queryWithParams(
        @RestPath queryName: String,
        body: Map<String, Any>,
    ) = parquetService.executeQuery(queryName, body)
}
