package com.github.scalaz_examples.show

import scalaz._
import Scalaz._

object Example extends App {
  // How many times have you done obj.toString to find out that you forgot to implement it and get Example$Foo@36ebe108
  // Normally when this happens, you go "doh!" and go implement a toString or make the class a case class.
  // Why can't this be a compile time error?

  class Foo(val name: String)
  class Bar(val name: String)

  val foo = new Foo("hello")

  println(s"${foo}")

  implicit val foos = Show.shows[Foo] {foo =>
    s"Name: ${foo.name}"
  }

  println(foo.shows)

  // why do I have to do println(foo.shows)!  so much typing!
  foo.println

  val bar = new Bar("won't compile")
//  bar.println // let it fail to compile
}
