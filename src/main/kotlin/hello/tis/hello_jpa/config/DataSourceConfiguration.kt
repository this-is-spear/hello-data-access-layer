package hello.tis.hello_jpa.config

import io.hypersistence.utils.logging.InlineQueryLogEntryCreator
import net.ttddyy.dsproxy.listener.ChainListener
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource

@Configuration
class DataSourceConfiguration {
    @Bean
    fun dataSource(): DataSource {
        val dataSource = EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("testdb")
            .build()
        val listener = ChainListener()
        val loggingListener = SLF4JQueryLoggingListener()
        loggingListener.queryLogEntryCreator = InlineQueryLogEntryCreator()
        listener.addListener(loggingListener)
        listener.addListener(DataSourceQueryCountListener())
        return ProxyDataSourceBuilder
            .create(dataSource)
            .name("DS-Proxy")
            .listener(listener)
            .build()
    }
}
