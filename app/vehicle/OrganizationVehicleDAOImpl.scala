package vehicle

import com.example.database.graph.schema.RelationTypes.EdgeLabels
import common.{ BaseRepo, OrganizationVehicleHelper, UserOrganizationHelper }
import gremlin.scala._
import javax.inject.Inject
import organization.models.Role
import utils.executioncontexts.DatabaseExecutionContext
import vehicle.models.{ CreateVehicle, _ }

import scala.concurrent.{ Future, Promise }
import scala.util.{ Failure, Success, Try }

/**
 * Give access to the user object.
 */
class OrganizationVehicleDAOImpl @Inject() ()(implicit ec: DatabaseExecutionContext)
  extends BaseRepo
  with OrganizationVehicleDAO
  with UserOrganizationHelper
  with OrganizationVehicleHelper {

  override def create(userId: Long, organizationId: Long, createVehicle: CreateVehicle): Future[models.Vehicle] = {

    val createdVehicleTry: Future[Try[Vertex]] = Future {
      for {
        userV <- getVertex(userId)
        organizationV <- getVertex(organizationId)
        _ <- getUserOrganizationRelationship(userV, organizationV, Role.all)
      } yield {
        val createdVehicleVertex = graph + models.VehicleVertex.create(createVehicle)
        organizationV --- (EdgeLabels.has_vehicle, models.OrganizationVehicle.mode -> createVehicle.vehicleMode) --> createdVehicleVertex
        graph.tx().commit()
        createdVehicleVertex
      }
    }
    createdVehicleTry.map {
      case Failure(exception) =>
        Future.failed(exception)
      case Success(createdVehicle) =>
        Future.successful(models.VehicleVertex.toVehicle(createdVehicle.toCC[models.VehicleVertex]))
    }.flatten
  }

  override def update(userId: Long, organizationId: Long, vehicle: models.Vehicle): Future[models.Vehicle] = {

    val updatedVehicleTry = Future {
      for {
        userV <- getVertex(userId)
        organizationV <- getVertex(organizationId)
        _ <- getUserOrganizationRelationship(userV, organizationV, Role.all)
        vehicleV <- getVertex(vehicle.id)
        _ <- getOrganizationVehicleRelationship(userV, vehicleV, Mode.all)
      } yield {
        val updatedVehicleVertex = vehicleV.updateAs[models.VehicleVertex](o => models.VehicleVertex.update(o, vehicle))
        graph.tx().commit()
        updatedVehicleVertex
      }
    }
    updatedVehicleTry.map {
      case Failure(exception) => Future.failed(exception)
      case Success(updatedVehicle) =>
        Future.successful(models.VehicleVertex.toVehicle(updatedVehicle.toCC[models.VehicleVertex]))
    }.flatten
  }

  override def list(userId: Long, organizationId: Long): Future[List[models.Vehicle]] = {
    Future {
      val listTry = for {
        userV <- getVertex(userId)
        organizationV <- getVertex(organizationId)
        _ <- getUserOrganizationRelationship(userV, organizationV, Role.all)
      } yield {
        val orgList = organizationV.out(EdgeLabels.has_vehicle).toList()
        orgList.map(vehicleVertex => models.VehicleVertex.toVehicle(vehicleVertex.toCC[models.VehicleVertex]))
      }
      listTry match {
        case Failure(exception) => Future.failed(exception)
        case Success(value) => Future.successful(value)
      }
    }.flatten
  }

  override def find(userId: Long, organizationId: Long, vehicleID: Long): Future[Option[models.Vehicle]] = {
    val p: Promise[Option[models.Vehicle]] = Promise[Option[models.Vehicle]]()
    Future {
      for {
        userV <- getVertex(userId)
        organizationV <- getVertex(organizationId)
        _ <- getUserOrganizationRelationship(userV, organizationV, Role.all)
        vehicleV <- getVertex(vehicleID)
        _ <- getOrganizationVehicleRelationship(userV, vehicleV, Mode.all)
      } yield {
        models.VehicleVertex.toVehicle(vehicleV.toCC[models.VehicleVertex])
      }
    } map {
      case Failure(e) => {
        logger.error(e.getMessage, e)
        p.success(None)
      }
      case Success(value) => p.success(Some(value))
    }
    p.future
  }

  override def delete(userId: Long, organizationId: Long, vehicleID: Long): Future[Long] = ???
}
