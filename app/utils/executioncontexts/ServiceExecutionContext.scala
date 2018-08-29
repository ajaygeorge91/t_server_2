package utils.executioncontexts

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.libs.concurrent.CustomExecutionContext

class ServiceExecutionContext @Inject() (system: ActorSystem)
  extends CustomExecutionContext(system, "service.dispatcher")
