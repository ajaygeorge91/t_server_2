package trip

import javax.inject.Inject
import trip.models.{ CreateTrip, Trip }

import scala.concurrent.Future

abstract class TripService @Inject() () {

  def create(userId: Long, organizationId: Long, createTrip: CreateTrip): Future[Trip]

  def find(userId: Long, tripID: Long, organizationId: Long): Future[Option[Trip]]

  def list(userId: Long, organizationId: Long): Future[List[Trip]]

  def update(userId: Long, organizationId: Long, trip: Trip): Future[Trip]

  def delete(userId: Long, tripID: Long, organizationId: Long): Future[Long]

}
