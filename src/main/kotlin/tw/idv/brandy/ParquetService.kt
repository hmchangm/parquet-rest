package tw.idv.brandy

import io.quarkus.logging.Log
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@ApplicationScoped
class ParquetService(
    val s3Client: S3Client,
    @ConfigProperty(name = "parquet.s3.bucket") val bucket: String,
    @ConfigProperty(name = "parquet.s3.key") val key: String,
    @ConfigProperty(name = "parquet.local.path") val localPath: String,
    @ConfigProperty(name = "parquet.queries") val queriesMap: Map<String, String>
) {

    private val jdbi: Jdbi by lazy { Jdbi.create("jdbc:duckdb:") }

    @Scheduled(every = "1h")
    fun scheduledFetch() {
        fetchParquetFile()
    }

    fun fetchParquetFile() {
        try {
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()

            val response = s3Client.getObject(getObjectRequest)
            val file = File(localPath)

            file.parentFile?.mkdirs()
            Files.copy(response, file.toPath(), StandardCopyOption.REPLACE_EXISTING)

        } catch (e: Exception) {
            Log.error("Error fetching file", e)
        }
    }

    fun getQueryTemplate(queryName: String): String? {
        return queriesMap[queryName]
    }

    fun executeNamedQuery(queryName: String, parameters: Map<String,Any>): List<Map<String, Any>> {
        val queryTemplate = getQueryTemplate(queryName) ?:
        throw IllegalArgumentException("Query with name '$queryName' not found in configuration")

        // Replace named parameters in the query
        var finalQuery = queryTemplate
        parameters.forEach { param ->
            // Simple parameter replacement - in production, use proper parameterized queries
            finalQuery = finalQuery.replace(":${param.key}", "'${param.value}'")
        }

        return executeQuery(finalQuery)
    }

    fun executeQuery(query: String): List<Map<String, Any>> =
        jdbi.withHandleUnchecked { handle ->
            // Execute the query and map results to a list of maps
            handle.createQuery(query)
                .mapToMap()
                .list()

        }
}
