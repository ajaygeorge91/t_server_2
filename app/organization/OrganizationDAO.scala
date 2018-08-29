package organization

import models.{ CreateOrganization, Organization }

import scala.concurrent.Future

trait OrganizationDAO {

  def find(organizationID: Long): Future[Option[Organization]]

  def list(userId: Long): Future[List[Organization]]

  def create(userId: Long, createOrganization: CreateOrganization): Future[Organization]

  def update(userId: Long, organization: Organization): Future[Organization]

  def delete(organizationID: Long): Future[Long]

}
