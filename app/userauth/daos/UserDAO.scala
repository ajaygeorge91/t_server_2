package userauth.daos

import com.mohiva.play.silhouette.api.LoginInfo
import userauth.models.{ CreateUser, User }

import scala.concurrent.Future

/**
 * Give access to the user object.
 */
trait UserDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]]

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: Long): Future[Option[User]]

  def create(createUser: CreateUser, loginInfo: LoginInfo): Future[User]
  def update(user: User): Future[User]
}
