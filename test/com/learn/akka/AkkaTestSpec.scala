package com.learn.akka

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._

trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with FlatSpecLike =>

  override protected def afterAll() = {
    super.afterAll()
    system.shutdown()
  }

}

abstract class AkkaTestSpec extends TestKit(ActorSystem("testSystem"))
                            with FlatSpecLike
                            with ShouldMatchers
                            with ImplicitSender
                            with StopSystemAfterAll
                            with OptionValues
                            with Inside
                            with Inspectors

