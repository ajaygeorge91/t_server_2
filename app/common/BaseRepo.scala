package common

import com.typesafe.scalalogging.LazyLogging
import gremlin.scala._
import org.janusgraph.core.{ JanusGraph, JanusGraphFactory }
import utils.exceptions.VertexNotFound
import utils.executioncontexts.DatabaseExecutionContext

import scala.util.{ Failure, Success, Try }

trait BaseRepo extends LazyLogging {

  implicit val dbExecutionContext: DatabaseExecutionContext = implicitly

  val gremlinGraph: JanusGraph = JanusGraphFactory.build()
    //.set("storage.backend", "inmemory")
    .set("storage.backend", "cassandra")
    .set("storage.hostname", "127.0.0.1")
    .open

  implicit val graph: ScalaGraph = gremlinGraph.asScala

  protected def getVertex(vertexId: Long): Try[Vertex] = {
    graph.V(vertexId).headOption() match {
      case Some(vertex) => Success(vertex)
      case None => Failure(VertexNotFound(s"Vertex not found for Id: $vertexId"))
    }
  }

}
