import basics.{Async, CollectionsLike, HelloFlow, RecoverFailure}
import materialize.TestKit
import measurence.Base
import measurence.graphs.complex.BuilderPitfallReuse
import retry.RetryFlow
import thermostat.Thermostat

import scala.util.Try

object Testing extends App with Base {

  HelloFlow.hello

  CollectionsLike.collectionAndFlow
  CollectionsLike.flatMap
  CollectionsLike.mapAsync

  RecoverFailure.recover
  RecoverFailure.recoverWith
  RecoverFailure.supervision

  BuilderPitfallReuse.otherReuse
  BuilderPitfallReuse.reuse
  Try { BuilderPitfallReuse.reuseError }
  BuilderPitfallReuse.standardReuse

  Thermostat.runThermostat
  Thermostat.runThermostatFeedbackLoop

  Async.sync
  Async.async

  TestKit.publisher
  TestKit.subscriber
  TestKit.both
}