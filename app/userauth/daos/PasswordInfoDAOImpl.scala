package userauth.daos

import java.util.UUID

import com.example.database.graph.schema.RelationTypes.EdgeLabels
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.InMemoryAuthInfoDAO
import common.BaseRepo
import javax.inject.Inject
import models.AuthToken
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import gremlin.scala._
import play.api.libs.ws.WSResponse
import play.api.libs.json.{ JsPath, Reads, Writes }
import userauth.daos.AuthTokenDAOImpl._
import userauth.models.{ LoginInfoVertex, PasswordInfoVertex, UserVertex }
import utils.exceptions.VertexNotFound
import utils.executioncontexts.DatabaseExecutionContext

import scala.concurrent
import scala.concurrent.{ Future, Promise }

/**
 * The DAO to store the password information.
 */
class PasswordInfoDAOImpl @Inject() ()(implicit databaseExecutionContext: DatabaseExecutionContext)
  extends InMemoryAuthInfoDAO[PasswordInfo]
  with BaseRepo {

  /**
   * Finds the auth info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
   */
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    graph
      .V()
      .has(LoginInfoVertex.providerID, loginInfo.providerID)
      .has(LoginInfoVertex.providerKey, loginInfo.providerKey)
      .out(EdgeLabels.HasPasswordInfo).headOption().map { passwordVertex =>

        PasswordInfoVertex.toPasswordInfo(passwordVertex.toCC[PasswordInfoVertex])
      }
  }

  /**
   * Adds new auth info for the given login info.
   *
   * @param loginInfo    The login info for which the auth info should be added.
   * @param passwordInfo The passwordInfo to add.
   * @return The added passwordInfo.
   */
  override def add(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] = {

    val p: Promise[PasswordInfo] = Promise[PasswordInfo]()
    val result = Future {
      graph
        .V()
        .has(LoginInfoVertex.providerID, loginInfo.providerID)
        .has(LoginInfoVertex.providerKey, loginInfo.providerKey)
        .headOption().map { loginInfoVertex =>
          val createdPasswordInfo = graph + PasswordInfoVertex.fromPasswordInfo(passwordInfo)
          loginInfoVertex --- EdgeLabels.HasPasswordInfo --> createdPasswordInfo
          createdPasswordInfo
        }
    }
    result.map {
      case Some(createdPasswordInfo) =>
        p.success(PasswordInfoVertex.toPasswordInfo(createdPasswordInfo.toCC[PasswordInfoVertex]))
      case None => p.failure(VertexNotFound(s"loginInfo with providerKey: ${loginInfo.providerKey} not found"))
    }
    p.future
  }

  /**
   * Updates the auth info for the given login info.
   *
   * @param loginInfo    The login info for which the auth info should be updated.
   * @param passwordInfo The passwordInfo to update.
   * @return The updated passwordInfo.
   */
  override def update(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] = {

    val p: Promise[PasswordInfo] = Promise[PasswordInfo]()
    val result = Future {
      graph
        .V()
        .has(LoginInfoVertex.providerID, loginInfo.providerID)
        .has(LoginInfoVertex.providerKey, loginInfo.providerKey)
        .out(EdgeLabels.HasPasswordInfo).headOption().map { passwordVertex =>
          val updatedPasswordInfo = passwordVertex.updateAs[PasswordInfoVertex](p => PasswordInfoVertex.update(p, passwordInfo))
          updatedPasswordInfo
        }
    }
    result.map {
      case Some(updatedPasswordInfo) =>
        p.success(PasswordInfoVertex.toPasswordInfo(updatedPasswordInfo.toCC[PasswordInfoVertex]))
      case None => p.failure(VertexNotFound(s"loginInfo with providerKey: ${loginInfo.providerKey} not found"))
    }
    p.future
  }

  /**
   * Saves the auth info for the given login info.
   *
   * This method either adds the passwordInfo if it doesn't exists or it updates the passwordInfo
   * if it already exists.
   *
   * @param loginInfo The login info for which the passwordInfo should be saved.
   * @param passwordInfo  The passwordInfo to save.
   * @return The saved passwordInfo.
   */
  override def save(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] = {
    find(loginInfo).flatMap {
      case Some(_) => update(loginInfo, passwordInfo)
      case None => add(loginInfo, passwordInfo)
    }
  }

  /**
   * Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] = ???

}

