package com.learn.scalatest

import collection.mutable.Stack

class StackSpec extends UnitSpec{

  "A Stack" should "pop values in first-in-last-out order" in {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    assert(stack.pop() == 2)
    assert(stack.pop() == 1)
  }

  it should "throw NoSuchElementException if an empty stack is pop" in {
    val emptyStack = new Stack[String]
    intercept[NoSuchElementException] {
      emptyStack.pop()
    }
  }
}
