package notification

import java.security.MessageDigest

object UserChannelUtils {

  def getChannelFromUserId(userId: Long): String =
    MessageDigest.getInstance("MD5").digest(String.valueOf(userId).getBytes).mkString

}
