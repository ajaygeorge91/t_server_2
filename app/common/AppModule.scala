package common

import com.google.inject.{ AbstractModule, Provides }
import com.outworkers.phantom.connectors.{ CassandraConnection, ContactPoints }
import javax.inject.Singleton
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import notification.SocketIOEngine
import org.janusgraph.core.{ JanusGraph, JanusGraphFactory }
import play.api.Configuration
import play.engineio.EngineIOController
import utils.AppConfig

class AppModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[EngineIOController].toProvider[SocketIOEngine]
  }

  @Provides
  @Singleton
  def provideGraph(configuration: Configuration): JanusGraph = {

    final case class JanusGraphStorageConfig(backend: String, hostname: String)

    // False warning.
    @SuppressWarnings(Array("org.wartremover.warts.Equals"))
    val config = configuration.underlying.as[JanusGraphStorageConfig](AppConfig.JanusGraphStorageConfig)

    // Janusgraph settings
    // https://docs.janusgraph.org/latest/config-ref.html
    JanusGraphFactory.build
      // JanusGraph will throw an e if a graph query cannot be answered using an index.
      // https://docs.janusgraph.org/latest/indexes.html
      // @see Graph Index (Note Section)
      //      .set("query.force-index", true)
      // Disable automatic schema creation
      // https://docs.janusgraph.org/latest/schema.html
      // @see Automatic Schema Maker
      //      .set("schema.default", "none")
      // https://docs.janusgraph.org/latest/cassandra.html
      // @see Local Server Mode
      .set("storage.backend", config.backend)
      .set("storage.hostname", config.hostname)
      .open
  }

  @Provides
  @Singleton
  def provideCassandraConnection(configuration: Configuration): CassandraConnection = {

    final case class CassandraStorageConfig(hosts: Seq[String], keyspace: String)

    // False warning.
    @SuppressWarnings(Array("org.wartremover.warts.Equals"))
    val config = configuration.underlying.as[CassandraStorageConfig](AppConfig.CassandraStorageConfig)

    ContactPoints(config.hosts).keySpace(config.keyspace)
  }

}
