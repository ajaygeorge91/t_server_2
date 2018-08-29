package organization

import javax.inject.Inject
import models.{ CreateOrganization, Organization }

import scala.concurrent.Future

class OrganizationService @Inject() (organizationDAO: OrganizationDAO) {

  def find(organizationID: Long): Future[Option[Organization]] = organizationDAO.find(organizationID)

  def list(userId: Long): Future[List[Organization]] = organizationDAO.list(userId)

  def create(userId: Long, createOrganization: CreateOrganization): Future[Organization] = organizationDAO.create(userId, createOrganization)

  def update(userId: Long, organization: Organization): Future[Organization] = organizationDAO.update(userId, organization)

  def delete(organizationID: Long): Future[Long] = organizationDAO.delete(organizationID)

}
