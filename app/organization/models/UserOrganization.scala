package organization.models

import com.example.database.graph.schema.RelationTypes.EdgePropertyKeys
import gremlin.scala.Key

final case class UserOrganization(
  userId: Long,
  organizationId: Long,
  role: String
)

object UserOrganization {

  val role: Key[String] = Key(EdgePropertyKeys.Role)

}