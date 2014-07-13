package com.learn.scalatest

/**
 * Author: zhanggo
 * Date: 7/13/2014.
 */

import org.scalatest._

abstract class UnitSpec extends FlatSpec with Matchers
                                         with OptionValues
                                         with Inside
                                         with Inspectors
