package vehicle

import javax.inject.Inject
import vehicle.models.{ CreateVehicle, Vehicle }

import scala.concurrent.Future

class VehicleService @Inject() (vehicleDAO: OrganizationVehicleDAO) {

  def find(userId: Long, vehicleID: Long, organizationId: Long): Future[Option[Vehicle]] = vehicleDAO.find(userId, vehicleID, organizationId)

  def list(userId: Long, organizationId: Long): Future[List[Vehicle]] = vehicleDAO.list(userId, organizationId)

  def create(userId: Long, organizationId: Long, createVehicle: CreateVehicle): Future[Vehicle] = vehicleDAO.create(userId, organizationId, createVehicle)

  def update(userId: Long, organizationId: Long, vehicle: Vehicle): Future[Vehicle] = vehicleDAO.update(userId, organizationId, vehicle)

  def delete(userId: Long, vehicleID: Long, organizationId: Long): Future[Long] = vehicleDAO.delete(userId, vehicleID, organizationId)

}
