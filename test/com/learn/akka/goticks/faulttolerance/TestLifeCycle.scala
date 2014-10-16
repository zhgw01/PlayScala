package com.learn.akka.goticks.faulttolerance

import akka.goticks.faulttolerance.LifeCycleHooks
import akka.testkit.TestActorRef
import com.learn.akka.AkkaTestSpec

class TestLifeCycle extends AkkaTestSpec{

  "The child" should "log lifecycle hooks" in {
    val childActor = TestActorRef[LifeCycleHooks]
    childActor ! "restart"

    childActor ! "msg"
    expectMsg("msg")
  }
}
