package tw.idv.brandy

import jakarta.enterprise.context.ApplicationScoped
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked

@ApplicationScoped
class ParquetService(
    val config: QueryConfig,
) {
    private val jdbi: Jdbi by lazy { Jdbi.create("jdbc:duckdb:") }

    fun executeQuery(
        queryName: String,
        parameters: Map<String, Any>,
    ): List<Map<String, Any>> {
        val query =
            config.queries[queryName]
                ?: throw IllegalArgumentException("Query with name '$queryName' not found in configuration")

        return jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(query)
                .bindMap(parameters)
                .mapToMap()
                .list()
        }
    }
}
