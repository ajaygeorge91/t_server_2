package organization

import com.example.database.graph.schema.RelationTypes.EdgeLabels
import common.{ BaseRepo, UserOrganizationHelper }
import gremlin.scala._
import javax.inject.Inject
import organization.models._
import utils.exceptions.NoPermissionForRole
import utils.executioncontexts.DatabaseExecutionContext

import scala.concurrent.{ Future, Promise }
import scala.util.{ Failure, Success, Try }

/**
 * Give access to the user object.
 */
class OrganizationDAOImpl @Inject() ()(implicit ec: DatabaseExecutionContext)
  extends BaseRepo
  with OrganizationDAO
  with UserOrganizationHelper {

  override def list(userID: Long): Future[List[Organization]] = {
    Future {
      val listTry = for {
        userVertex <- getVertex(userID)
      } yield {
        val orgList = userVertex.out(EdgeLabels.member_of_organization).toList()
        orgList.map(organizationVertex => OrganizationVertex.toOrganization(organizationVertex.toCC[OrganizationVertex]))
      }
      listTry match {
        case Failure(exception) => Future.failed(exception)
        case Success(value) => Future.successful(value)
      }
    }.flatten
  }

  override def find(organizationID: Long): Future[Option[Organization]] = {
    val p: Promise[Option[Organization]] = Promise[Option[Organization]]()
    Future {
      for {
        organizationVertex <- getVertex(organizationID)
      } yield {
        OrganizationVertex.toOrganization(organizationVertex.toCC[OrganizationVertex])
      }
    } map {
      case Failure(_) => p.success(None)
      case Success(value) => p.success(Some(value))
    }
    p.future
  }

  override def create(userId: Long, createOrganization: CreateOrganization): Future[Organization] = {

    val createdOrganizationTry: Future[Try[Vertex]] = Future {
      for {
        userV <- getVertex(userId)
      } yield {
        val createdOrganizationVertex = graph + OrganizationVertex.create(createOrganization)
        userV --- (EdgeLabels.member_of_organization, UserOrganization.role -> Role.ADMIN) --> createdOrganizationVertex
        graph.tx().commit()
        createdOrganizationVertex
      }
    }
    createdOrganizationTry.map {
      case Failure(exception) =>
        Future.failed(exception)
      case Success(createdOrganization) =>
        Future.successful(OrganizationVertex.toOrganization(createdOrganization.toCC[OrganizationVertex]))
    }.flatten
  }

  override def update(userId: Long, organization: Organization): Future[Organization] = {

    val updatedOrganizationTry = Future {
      for {
        userV <- getVertex(userId)
        organizationV <- getVertex(organization.id)
        _ <- getUserOrganizationRelationship(userV, organizationV, List(Role.ADMIN))
      } yield {
        val updatedOrganizationVertex = organizationV.updateAs[OrganizationVertex](o => OrganizationVertex.update(o, organization))
        graph.tx().commit()
        updatedOrganizationVertex
      }
    }
    updatedOrganizationTry.map {
      case Failure(exception) => Future.failed(exception)
      case Success(updatedOrganization) =>
        Future.successful(OrganizationVertex.toOrganization(updatedOrganization.toCC[OrganizationVertex]))
    }.flatten
  }

  override def delete(organizationID: Long): Future[Long] = ???
}
