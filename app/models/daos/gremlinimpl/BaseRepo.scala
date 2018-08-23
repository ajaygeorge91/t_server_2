package com.scienaptic.core.repos

import com.typesafe.scalalogging.LazyLogging
import gremlin.scala._
import models.daos.gremlinimpl.DatabaseExecutionContext
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph

abstract class BaseRepo(implicit ec: DatabaseExecutionContext) extends LazyLogging {

  val location: String = "/tmp/neo4j"
  val neoGraph: Neo4jGraph = Neo4jGraph.open(location)
  implicit val graph: ScalaGraph = neoGraph.asScala

}
