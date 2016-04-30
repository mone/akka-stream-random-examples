package measurence.graphs.complex

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl._
import GraphDSL.Implicits._
import measurence.Base

object BuilderPitfallReuse extends Base {

  /**
    * We know we can reuse the same Graph instance (in this case the plusOne Flow)
    * to materialise two different streams at the same time
    */
  def standardReuse(): Unit = {

    val plusOne = Flow[Int].map(_ + 1)

    Source(1 to 10).via(plusOne).to(Sink.foreach(println)).run()
    Source(1 to 10).via(plusOne).to(Sink.foreach(println)).run()

  }

  /**
    * We must pay attention though creating complex Graph:s
    * If we call the builder.add method what we get back is not
    * a reusable Graph, but a Shape expressing the ports to be wired
    * The following example compiles, but will throw an exception
    * at runtime because we're reusing the ports twice
    */
  def reuseError(): Unit = {
    val flow = Flow.fromGraph(GraphDSL.create() { implicit builder =>

      val bcast = builder.add(Broadcast[Int](2))
      val plusOne:FlowShape[Int, Int] = builder.add(Flow[Int].map(_ + 1))
      val merge = builder.add(Merge[Int](2))

      bcast.out(0) ~> plusOne ~> merge
      bcast.out(1) ~> plusOne ~> merge

      FlowShape(bcast.in, merge.out)

    })

    Source(1 to 10).via(flow).to(Sink.foreach(println)).run()

  }

  /**
    * Anyway we can still reuse a Graph in a complex graph by skipping
    * the builder.add call. The flip side is that if we forget to wire up
    * the port(s) of such Graph:s the builder won't be able to notify us.
    * Use with care
    */
  def reuse(): Unit = {
    val flow = Flow.fromGraph(GraphDSL.create() { implicit builder =>

      val bcast = builder.add(Broadcast[Int](2))
      val plusOne:Flow[Int, Int, NotUsed] = Flow[Int].map(_ + 1)
      val merge = builder.add(Merge[Int](2))

      bcast.out(0) ~> plusOne ~> merge
      bcast.out(1) ~> plusOne ~> merge

      FlowShape(bcast.in, merge.out)

    })

    Source(1 to 10).via(flow).to(Sink.foreach(println)).run()

  }

  /**
    * Or by using the add call twice. In this case we get the best of both worlds
    * in exchange from some extra coding
    */
  def otherReuse(): Unit = {
    val flow = Flow.fromGraph(GraphDSL.create() { implicit builder =>

      val plusOne:Flow[Int, Int, NotUsed] = Flow[Int].map(_ + 1)

      val bcast = builder.add(Broadcast[Int](2))
      val plusOneA = builder.add(plusOne)
      val plusOneB = builder.add(plusOne)
      val merge = builder.add(Merge[Int](2))

      bcast.out(0) ~> plusOneA ~> merge
      bcast.out(1) ~> plusOneB ~> merge

      FlowShape(bcast.in, merge.out)

    })

    Source(1 to 10).via(flow).to(Sink.foreach(println)).run()

  }

}
