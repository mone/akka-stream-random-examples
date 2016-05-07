package basics

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source, Flow}
import measurence.Base

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object CollectionsLike extends Base {

  def collectionAndFlow: Unit = {

    val resC = (1 to 42)
      .map(_ + 64)
      .filter(_ % 2 == 0)
      .take(9)
      .fold(0)(_ + _)

    val flow1 = Flow[Int]
      .map(_ + 64)
      .filter(_ % 2 == 0)
      .take(9)
      .fold(0)(_ + _)

    // run it to get the result
    val resF: Future[Int] = Source(1 to 42).via(flow1).runWith(Sink.last[Int])

    println(resC)
    resF.onSuccess(print)
  }

  def flatMap: Unit = {

    val list: List[List[Int]] = List(1, 2, 3)
      .map(el => List(1, 2, 3))

    // Monad (with flatMap)
    val flattenedC: List[Int] = list.flatMap(identity)

    // Not a monad (they say the avoided the name flatMap to avoid confusion)
    val flow2: Flow[List[Int], Int, NotUsed] = Flow[List[Int]]
      .mapConcat(identity)
    val flattenedF: Future[Seq[Int]] = Source(list).via(flow2).runWith(Sink.seq[Int])

    println(flattenedC)
    flattenedF.onSuccess(print)
  }

  def mapAsync: Unit = {

    // mapAsync is nice
    val flow3: Flow[Int, String, NotUsed] = Flow[Int]
      .mapAsync(10)( value =>
        Future {
          value.toString
        }
      ) // will emit String, not Future[String}
    val asStrings: Future[Seq[String]] = Source(1 to 42).via(flow3).runWith(Sink.seq[String])

    asStrings.onSuccess(print)

  }

  val print = PartialFunction[Any, Unit]  {
    case value => println(value)
  }

}
