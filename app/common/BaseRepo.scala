package common

import com.example.database.graph.schema.RelationTypes.EdgeLabels
import com.typesafe.scalalogging.LazyLogging
import gremlin.scala._
import javax.inject.Inject
import org.janusgraph.core.{ JanusGraph, JanusGraphFactory }
import organization.models.{ Role, UserOrganization }
import utils.exceptions.{ NoPermissionForRole, VertexNotFound }
import utils.executioncontexts.DatabaseExecutionContext
import vehicle.models.OrganizationVehicle

import scala.util.{ Failure, Success, Try }

trait BaseRepo extends LazyLogging {

  val gremlinGraph: JanusGraph = JanusGraphFactory.build()
    //    .set("storage.backend", "inmemory")
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

  protected def getUserOrganizationRelationship(userVertex: Vertex, organizationVertex: Vertex, requiredRoleList: List[String]): Try[Edge] = {

    def containsRole(role: String): Boolean = requiredRoleList.exists(f => f.equalsIgnoreCase(role))

    userVertex.outE(EdgeLabels.member_of_organization)
      .where(_.otherV().hasId(organizationVertex.id())).headOption() match {
        case Some(edge) if containsRole(edge.value2[String](UserOrganization.role)) =>
          Success(edge)
        case None => Failure(NoPermissionForRole(s"You don't have permission"))
      }
  }

  protected def getOrganizationVehicleRelationship(organizationVertex: Vertex, vehicleVertex: Vertex, requiredModeList: List[String]): Try[Edge] = {

    def containsMode(mode: String): Boolean = requiredModeList.exists(f => f.equalsIgnoreCase(mode))

    organizationVertex.outE(EdgeLabels.has_vehicle)
      .where(_.otherV().hasId(vehicleVertex.id())).headOption() match {
        case Some(edge) if containsMode(edge.value2[String](OrganizationVehicle.mode)) =>
          Success(edge)
        case None => Failure(NoPermissionForRole(s"You don't have permission"))
      }
  }
}
