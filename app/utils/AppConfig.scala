package utils

/**
 * `application.conf` paths.
 */
object AppConfig {

  // Execution context for JanusGraph queries.
  final val Dispatcher: String = "database.dispatcher"

  // Storage settings(Backend, Hostname) for JanusGraph.
  final val JanusGraphStorageConfig: String = "janusgraph.storage"
  // Storage settings(Hosts, Keyspace) for Cassandra.
  final val CassandraStorageConfig: String = "cassandra.storage"

}
