package com.learn.scalatest

/**
 * Author: zhanggo
 * Date: 7/13/2014.
 */
class AssertionSpec extends UnitSpec{
  "5 - 2 == 2" should "fail" in {
    assume(false)

    val a = 5
    val b = 2
    assertResult(2) {
      a - b
    }
  }

  ignore should "not run after tagging ignore" in {

  }
}
