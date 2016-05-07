package basics

import akka.stream.scaladsl.{Flow, Sink, Source}
import measurence.Base
import concurrent.duration._

object Async extends Base {

  val source  = Source(1 to 10000)
  val printAndGo = Flow[Int]
    .map {
      v =>
        println(v)
        v
    }

  def sync(): Unit = {
    source
      .via(printAndGo)
      .via(printAndGo)
      .via(printAndGo)
      .via(printAndGo)
      .runWith(Sink.foreach(println))
  }

  def async():Unit = {
    source
      .via(printAndGo).async
      .via(printAndGo).async
      .via(printAndGo).async
      .via(printAndGo).async
      .runWith(Sink.foreach(println))
  }


}
