package common

import com.example.database.graph.schema.RelationTypes.EdgeLabels
import com.typesafe.scalalogging.LazyLogging
import gremlin.scala._
import organization.models.UserOrganization
import utils.exceptions.NoPermissionForRole
import utils.executioncontexts.DatabaseExecutionContext

import scala.util.{ Failure, Success, Try }

trait OrganizationVehicleHelper extends LazyLogging {

  private implicit val dbExecutionContext: DatabaseExecutionContext = implicitly

  private implicit val graph: ScalaGraph = implicitly

  protected def getOrganizationVehicleRelationship(organizationVertex: Vertex, vehicleVertex: Vertex, requiredModeList: List[String]): Try[Edge] = {

    def containsMode(mode: String): Boolean = requiredModeList.exists(f => f.equalsIgnoreCase(mode))

    organizationVertex.outE(EdgeLabels.has_vehicle)
      .where(_.otherV().hasId(vehicleVertex.id())).headOption() match {
        case Some(edge) if containsMode(edge.value2[String](UserOrganization.role)) =>
          Success(edge)
        case None => Failure(NoPermissionForRole(s"You don't have permission"))
      }
  }

}
