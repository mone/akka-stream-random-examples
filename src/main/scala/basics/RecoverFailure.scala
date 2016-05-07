package basics

import akka.stream.{Supervision, ActorAttributes}
import akka.stream.scaladsl.{Sink, Flow, Source}
import measurence.Base

object RecoverFailure extends Base {

  val failOnOne = Flow[Int]
    .map {
      e: Int =>
        if (e == 1) {
          throw new Exception("OH HAI")
        } else {
          e
        }
    }

  def recover: Unit = {

    val flow = failOnOne
      .recover {
        case _ => 1
      }

    Source(1 to 42)
      .via(flow)
      .runWith(Sink.foreach(println))

  }

  def recoverWith: Unit = {

    val flow = failOnOne
      .recoverWith {
        case _ => Source(1 to 3)
      }

    Source(1 to 42)
      .via(flow)
      .runWith(Sink.foreach(println))

  }

  def supervision: Unit = {

    val flow = failOnOne
      .withAttributes(ActorAttributes.supervisionStrategy {
        case _ => Supervision.resume
      })

    Source(1 to 42)
      .via(flow)
      .runWith(Sink.foreach(println))

  }
}
