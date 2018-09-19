import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._
import sbt.{Resolver, _}

name := "app"

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.0.1",
  scalaVersion := Version.Scala,
  resolvers += Resolver.mavenLocal,
  resolvers += Resolver.jcenterRepo,
  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"
)

lazy val commonDependencies = Seq(
  "com.iheart" %% "ficus" % "1.4.3",
  specs2 % Test,
  Library.ScalaGuice,
  Library.QuartzScheduler,
  guice
) ++ Seq(Library.Logging)

lazy val common = (project in file("common"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )


lazy val geodata = (project in file("geodata"))
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies ++ Library.Silhouette
  )
  .dependsOn(common)
  .enablePlugins(PlayScala)


lazy val database = (project in file("database"))
  .settings(
    commonSettings,
    name := "database",
    libraryDependencies ++= Seq(Library.PhantomDsl) ++ commonDependencies ++ Library.GremlinScala ++ Library.JanusGraph
  )


lazy val app = (project in file("."))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      ehcache,
      Library.PlaySocketIo,
      filters
    ) ++ commonDependencies ++ Library.Silhouette ++ Library.Web ++ Library.Mailer
  )
  .aggregate(common, geodata, database)
  .dependsOn(common, geodata, database)
  .enablePlugins(PlayScala)



routesImport += "utils.route.Binders._"

// https://github.com/playframework/twirl/issues/105
TwirlKeys.templateImports := Seq()

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  //"-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  // Play has a lot of issues with unused imports and unsued params
  // https://github.com/playframework/playframework/issues/6690
  // https://github.com/playframework/twirl/issues/105
  "-Xlint:-unused,_"
)

//********************************************************
// Scalariform settings
//********************************************************

scalariformAutoformat := true

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentConstructorArguments, false)
  .setPreference(DanglingCloseParenthesis, Preserve)
