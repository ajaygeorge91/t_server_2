import sbt._

object Version {
  final val Scala = "2.12.4"
  final val Silhouette = "5.0.5"
  final val JanusGraph = "0.3.0"
}

object Library {

  final val JanusGraph = Seq(
    "janusgraph-core",
    "janusgraph-cassandra",
    "janusgraph-cql",
    "janusgraph-es"
  ).map("org.janusgraph" % _ % Version.JanusGraph)

  final val GremlinScala = Seq(
    "org.apache.tinkerpop" % "gremlin-driver" % "3.3.3",
    "com.michaelpollmeier" %% "gremlin-scala" % "3.3.3.4"
  )

  final val Web = Seq(
    "org.webjars" %% "webjars-play" % "2.6.3",
    "org.webjars" % "bootstrap" % "3.3.7-1" exclude("org.webjars", "jquery"),
    "org.webjars" % "jquery" % "3.2.1",
    "com.adrianhurt" %% "play-bootstrap" % "1.4-P26-B3-SNAPSHOT"
  )

  final val Mailer = Seq(
    "com.typesafe.play" %% "play-mailer" % "6.0.1",
    "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"
  )

  final val PhantomDsl = "com.outworkers" %% "phantom-dsl" % "2.9.2"
  final val ScalaGuice =   "net.codingwell" %% "scala-guice" % "4.1.0"

  final val Logging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

  final val Silhouette = Seq("play-silhouette",
                             "play-silhouette-crypto-jca",
                             "play-silhouette-password-bcrypt",
                             "play-silhouette-testkit",
                             "play-silhouette-persistence").map("com.mohiva" %% _ % Version.Silhouette)


  final val PlayRedis = Seq(play.sbt.PlayImport.cacheApi, "com.github.karelcemus" %% "play-redis" % "2.2.0")

  final val PlaySocketIo = "com.lightbend.play" % "play-socket-io_2.12" % "1.0.0-beta-2"
  final val QuartzScheduler =  "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.1-akka-2.5.x"
  final val Mqtt =  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "0.20"

}
