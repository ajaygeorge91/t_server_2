package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import organization.{ OrganizationDAO, OrganizationDAOImpl }
import userauth.daos.{ AuthTokenDAO, AuthTokenDAOImpl }
import userauth.services.{ AuthTokenService, AuthTokenServiceImpl }
import vehicle.{ OrganizationVehicleDAO, OrganizationVehicleDAOImpl }

/**
 * The base Guice module.
 */
class BaseModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure(): Unit = {
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]

    bind[OrganizationDAO].to[OrganizationDAOImpl]
    bind[OrganizationVehicleDAO].to[OrganizationVehicleDAOImpl]

  }
}

