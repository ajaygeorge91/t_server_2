package organizationvehicle

import organizationvehicle.models.{ CreateVehicle, Vehicle }

import scala.concurrent.Future

trait OrganizationVehicleDAO {
  def create(userId: Long, organizationId: Long, createVehicle: CreateVehicle): Future[models.Vehicle]
  def update(userId: Long, organizationId: Long, vehicle: models.Vehicle): Future[models.Vehicle]
  def list(userId: Long, organizationId: Long): Future[List[models.Vehicle]]
  def find(userId: Long, organizationId: Long, vehicleID: Long): Future[Option[models.Vehicle]]
  def delete(userId: Long, organizationId: Long, vehicleID: Long): Future[Long]
}
