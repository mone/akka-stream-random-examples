package materialize

import akka.stream.scaladsl.{Keep, Source, Sink, Flow}
import akka.stream.testkit.scaladsl.{TestSource, TestSink}
import measurence.Base

object TestKit extends Base {

  def publisher(): Unit = {

    val mySink = Sink.foreach(println)

    val pub = TestSource.probe[String].toMat(mySink)(Keep.left).run

    pub.sendNext("Gathering")
  }

  def subscriber(): Unit = {

    val mySource = Source(1 to 42)

    val sub = mySource.toMat(TestSink.probe[Any])(Keep.right).run

    sub.requestNext(1)

  }

  def both(): Unit = {

    val myFlow = Flow[String].map { v => v.take(1) }

    val (pub, sub) = TestSource.probe[String]
      .via(myFlow)
      .toMat(TestSink.probe[Any])(Keep.both)
      .run()

    sub.request(1)
    pub.sendNext("Gathering")
    sub.expectNext("G")

  }
}
