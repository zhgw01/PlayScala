package com.learn.scalatest

/**
 * Author: zhanggo
 * Date: 7/13/2014.
 */

import org.scalatest.Failed

import collection.mutable.ListBuffer

class FixtureSpec extends UnitSpec{

  override def withFixture(test: NoArgTest) = {
    info("Setup the fixture here")
    super.withFixture(test) match {
      case failed: Failed =>
        info("test case failed, clean up")
        failed

      case other =>
        info("cleanup fixture")
        other
    }
  }


  "1 + 1 = 2" should "succeed" in {
    assertResult(2) {
      1 + 1
    }
  }



}
