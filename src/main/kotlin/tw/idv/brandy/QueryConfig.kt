package tw.idv.brandy

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithName

@ConfigMapping(prefix = "rest")
interface QueryConfig {
    @get:WithName("queries")
    val queries: Map<String, String>
}
