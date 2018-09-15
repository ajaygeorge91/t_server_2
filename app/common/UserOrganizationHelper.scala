package common

import com.example.database.graph.schema.RelationTypes.EdgeLabels
import com.typesafe.scalalogging.LazyLogging
import gremlin.scala._
import org.janusgraph.core.{ JanusGraph, JanusGraphFactory }
import organization.models.UserOrganization
import utils.exceptions.{ NoPermissionForRole, VertexNotFound }
import utils.executioncontexts.DatabaseExecutionContext

import scala.util.{ Failure, Success, Try }

trait UserOrganizationHelper extends LazyLogging {

  private implicit val dbExecutionContext: DatabaseExecutionContext = implicitly

  private implicit val graph: ScalaGraph = implicitly

  protected def getUserOrganizationRelationship(userVertex: Vertex, organizationVertex: Vertex, requiredRoleList: List[String]): Try[Edge] = {

    def containsRole(role: String): Boolean = requiredRoleList.exists(f => f.equalsIgnoreCase(role))

    userVertex.outE(EdgeLabels.member_of_organization)
      .where(_.otherV().hasId(organizationVertex.id())).headOption() match {
        case Some(edge) if containsRole(edge.value2[String](UserOrganization.role)) =>
          Success(edge)
        case None => Failure(NoPermissionForRole(s"You don't have permission"))
      }
  }

}
