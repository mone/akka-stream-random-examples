package thermostat

import akka.NotUsed
import akka.stream.scaladsl._
import akka.stream.{ClosedShape, Graph, UniformFanInShape}
import GraphDSL.Implicits._
import measurence.Base
import concurrent.duration._

import scala.util.Random

object Thermostat extends Base {

  sealed trait ApplianceState

  case class HeaterOnCoolerOff(currentTemp: Int) extends ApplianceState

  case class HeaterOffCoolerOn(currentTemp: Int) extends ApplianceState

  case class EverythingOff(currentTemp: Int) extends ApplianceState

  val diffFlow: Flow[(Int, Int), (Int, Int), NotUsed] = Flow[(Int, Int)]
    .map {
      tempPair => (tempPair._1 - tempPair._2, tempPair._1)
    }

  val stateConverter: Flow[(Int, Int), ApplianceState, NotUsed] = Flow[(Int, Int)]
    .map {
      case (diff, curr) =>
        if (diff == 0) {
          EverythingOff(curr)
        } else if (diff > 0) {
          HeaterOffCoolerOn(curr)
        } else {
          HeaterOnCoolerOff(curr)
        }
    }
    .alsoTo(Sink.foreach(println))

  def thermostatDecisionMaker(): Graph[UniformFanInShape[Int, ApplianceState], NotUsed] = {
    GraphDSL.create() { implicit builder =>

      val zip = builder.add(Zip[Int, Int])
      val diff = builder.add(diffFlow)
      val state = builder.add(stateConverter)

      zip.out ~> diff ~> state

      new UniformFanInShape(state.out, Array(zip.in0, zip.in1))
    }

  }

  def runThermostat(): Unit = {

    val currentSource = Source(List(20, 20, 20, 22, 23, 22, 22))
    val targetSource  = Source.repeat(22)

    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>

      val current = builder.add(currentSource)
      val target = builder.add(targetSource)
      val state = builder.add(thermostatDecisionMaker())
      val print = builder.add(Sink.foreach(println))

      current ~> state.in(0)
      target  ~> state.in(1)
                 state.out ~> print

      ClosedShape
    }).run()

  }

  val executorFlow: Flow[ApplianceState, Int, NotUsed] = Flow[ApplianceState]
    .map {

      case EverythingOff(curr) => curr + Random.nextInt(3) - 1

      case HeaterOffCoolerOn(curr) => curr - Random.nextInt(3)

      case HeaterOnCoolerOff(curr) => curr + Random.nextInt(3)

    }
    .alsoTo(Sink.foreach(println))

  def runThermostatFeedbackLoop(): Unit = {

    val targetSource  = Source.tick(1 second, 1 second, 22)
    val initialTemperature = Source.single(19)

    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>

      val state    = builder.add(thermostatDecisionMaker())
      val merge    = builder.add(Merge[Int](2))
      val executor = builder.add(executorFlow)
      val target   = builder.add(targetSource)
      val initial  = builder.add(initialTemperature)

      target ~>            state.in(1)
      initial ~> merge ~>  state.in(0)
                           state.out  ~> executor
                 merge <~                executor

      ClosedShape
    }).run()


  }

}