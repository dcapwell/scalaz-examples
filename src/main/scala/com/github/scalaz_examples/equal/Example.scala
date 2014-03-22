package com.github.scalaz_examples.equal

import scalaz._
import Scalaz._

object WhyShouldICare extends App {
  // In normal java, == is only reference compair, so you must always use an 'equals' method
  // In normal scala, == is a function that will mostly just call equals
  // what is the issue with equals? def equals(obj: AnyRef): Boolean
  // IT TAKES EVERYTHING!

  val string = "Hello World"
  val number = 1234

  assert(string != number, "The values are diff, why did they match?")

  // whats the problem?  It gave the right answer!
  // well, the == would never work here since the types are not similar, so why can't the compiler tell me?
}

object BasicExample extends App {
  // how can we get the above to fail at compile time?
  val number = 1234
  val revNumber = 4321

  assert(number =/= revNumber, "The numbers are diff, but they matched?")
  assert(number === number, "Should have matched")

  // ok, so whats different so far?
  val string = "1234"
  // assert(number === string, "Shouldn't have compiled!")
  // GREAT!  This check didn't make sense, so now the compiler catches it!
}

object WhatIfIWantFuzzyMatching extends App {
  // name must be intInstance to override existing intInstance implicit
  implicit object intInstance extends Equal[Int] {
    override def equal(left: Int, right: Int): Boolean = {
      val leftMod = left % 2
      val rightMod = right % 2
      leftMod == rightMod
    }
  }

  val even = 2
  val odd = 3

  assert(even =/= odd, "Shouldn't have matched!")

  val evenMultTwo = even * 2

  assert(even === evenMultTwo, "Both are even, so should have matched")
}

object WhatIfIWantToSwitchBack extends App {
  // so what if I want to switch back to the other Equals?
  object modEqualsInt extends Equal[Int] {
    override def equal(left: Int, right: Int): Boolean = {
      val leftMod = left % 2
      val rightMod = right % 2
      leftMod == rightMod
    }
  }

  implicit var intInstance: Equal[Int] = Scalaz.intInstance

  assert(2 =/= 4)

  intInstance = modEqualsInt

  assert(2 === 4)

  intInstance = Scalaz.intInstance

  assert(2 =/= 4)
}
