package userauth.daos

import com.example.database.graph.schema.RelationTypes.EdgeLabels
import com.mohiva.play.silhouette.api.LoginInfo
import common.BaseRepo
import gremlin.scala._
import javax.inject.Inject
import userauth.models.{ CreateUser, LoginInfoVertex, User, UserVertex }
import utils.exceptions.VertexNotFound
import utils.executioncontexts.DatabaseExecutionContext

import scala.concurrent.{ Future, Promise }

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject() ()(implicit ec: DatabaseExecutionContext) extends BaseRepo with UserDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]] = Future {
    graph
      .V()
      .has(LoginInfoVertex.providerID, loginInfo.providerID)
      .has(LoginInfoVertex.providerKey, loginInfo.providerKey)
      .in(EdgeLabels.HasLoginInfo).headOption().map { userVertex =>

        UserVertex.toUser(userVertex.toCC[UserVertex], loginInfo)
      }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: Long): Future[Option[User]] =
    Future {
      graph.V(userID).headOption.map { userVertex =>

        val loginInfoVertex = userVertex.out(EdgeLabels.HasLoginInfo).head().toCC[LoginInfoVertex]

        UserVertex.toUser(userVertex.toCC[UserVertex], LoginInfoVertex.toLoginInfo(loginInfoVertex))

      }
    }

  def create(createUser: CreateUser, loginInfo: LoginInfo): Future[User] = {

    Future {
      val createdUserVertex = graph + UserVertex.create(createUser)
      val createdLoginInfoVertex = graph + LoginInfoVertex.fromLoginInfo(loginInfo)
      createdUserVertex --- EdgeLabels.HasLoginInfo --> createdLoginInfoVertex
      graph.tx.commit()

      val createdLoginInfo = createdLoginInfoVertex.toCC[LoginInfoVertex]
      val createdUser = createdUserVertex.toCC[UserVertex]
      UserVertex.toUser(createdUser, LoginInfoVertex.toLoginInfo(createdLoginInfo))

    }
  }

  def update(user: User): Future[User] = {

    val p: Promise[User] = Promise[User]()
    val result = Future {
      graph.V(user.id).headOption match {
        case Some(userVertex) =>
          val updatedUserVertex = userVertex.updateAs[UserVertex](uv => UserVertex.update(uv, user))
          graph.tx.commit()
          Some(updatedUserVertex)
        case None => None
      }
    }
    result.map {
      case Some(updatedUserVertex) => p.success(UserVertex.toUser(updatedUserVertex.toCC[UserVertex], user.loginInfo))
      case None => p.failure(VertexNotFound(s"User with id: ${user.id} not found"))
    }
    p.future
  }

}
