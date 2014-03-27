package com.github.scalaz_examples.util

import scalaz._
import Scalaz._

object NelExample extends App {
  // have you ever been bitten by getting an empty list and getting the 0th element or head?
  // thats what NonEmptyList (nel) tries to make more clear.
  // Nel exists to make a function more clear when the list returned/passed in can never be empty

  val list = NonEmptyList(1, 2, 3)
  list.println

  list.println

  // cons elements into the lists
  (4 <:: list).println
  (list :::> List(4)).println

  // returns List[Int], since the tail size is unknown
  list.tail.println

  implicitly[Monoid[Int]]
}
