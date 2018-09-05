package com.example.database.graph.schema

/**
 * Edge labels and property keys are jointly referred to as relation types.
 * Names of relation types must be unique in the graph which means that
 * property keys and edge labels cannot have the same name.
 */
// https://docs.janusgraph.org/latest/schema.html
object RelationTypes {

  /**
   * Edge label names must be unique in the graph.
   */
  object EdgeLabels {

    final val HasLoginInfo: String = "hasLoginInfo"
    final val HasPasswordInfo: String = "hasPasswordInfo"
    final val member_of_organization: String = "member_of_organization"
    final val has_vehicle: String = "has_vehicle"

  }

  /**
   * Property key names must be unique in the graph.
   */
  object PropertyKeys {
    final val ProviderID: String = "providerID"
    final val ProviderKey: String = "providerKey"
  }

  object EdgePropertyKeys {
    final val Role: String = "role"
    final val Mode: String = "mode"
  }

}
