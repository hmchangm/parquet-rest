package tw.idv.brandy

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "rest")
interface QueryConfig {

    fun queries():Map<String,String>
}