package basics

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.{Done, NotUsed}
import akka.stream.scaladsl.{Source, Flow, Sink, RunnableGraph}

import scala.concurrent.Future

object HelloFlow {

  def hello(): Unit = {
    implicit val system = ActorSystem() // akka stream runs on Actors
    implicit val materializer = ActorMaterializer() // to run a stream you need a materializer

    val source: Source[Int, NotUsed] = Source(1 to 42)
    val flow: Flow[Int, Int, NotUsed] = Flow[Int].map(_ + 1)
    val sink: Sink[Any, Future[Done]] = Sink.foreach(println)
    val graph: RunnableGraph[NotUsed] =
      source
        .via(flow)
        .to(sink)

    graph.run() // (materializer) is implicit, if you are wondering it's used here
  }
}
