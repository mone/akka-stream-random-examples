package measurence

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait Base {

  implicit lazy val system = ActorSystem()
  implicit lazy val materializer = ActorMaterializer()

}
