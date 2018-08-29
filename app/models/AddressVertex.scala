package models

import com.example.database.graph.schema.RelationTypes.PropertyKeys
import com.example.database.graph.schema.VertexLabels
import gremlin.scala.{ Key, id, label }

@label(VertexLabels.Address)
final case class AddressVertex(
  @id id: Option[Long],
  addressLocality: String,
  streetAddress: Option[String],
  addressRegion: Option[String],
  addressCountry: Option[String],
  postalCode: Option[String]
)

object AddressVertex {

  val providerID: Key[String] = Key(PropertyKeys.ProviderID)
  val providerKey: Key[String] = Key(PropertyKeys.ProviderKey)

  def fromAddress(address: Address): AddressVertex = AddressVertex(
    id = None,
    addressLocality = address.addressLocality,
    streetAddress = address.streetAddress,
    addressRegion = address.addressRegion,
    addressCountry = address.addressCountry,
    postalCode = address.postalCode
  )
  def toAddress(addressVertex: AddressVertex): Address = Address(
    addressLocality = addressVertex.addressLocality,
    streetAddress = addressVertex.streetAddress,
    addressRegion = addressVertex.addressRegion,
    addressCountry = addressVertex.addressCountry,
    postalCode = addressVertex.postalCode
  )
}

