package organizationvehicle.models

import com.example.database.graph.schema.RelationTypes.EdgePropertyKeys
import gremlin.scala.Key

final case class OrganizationVehicle(
  organizationId: Long,
  vehicleId: Long,
  mode: String
)

object OrganizationVehicle {

  val mode: Key[String] = Key(EdgePropertyKeys.Mode)

}