package com.github.scalaz_examples.order

import scalaz._, Scalaz._
import scalaz.Ordering._

object OrderExample extends App {
  // in both java and scala, the <, > operators are implemented for int, but nothing else
  // If you want the same functionality, you need to go with Comparator or Ordering from guava
  // since < and > are function of things that are Ordered, why can't they be generalized?
  (1 < 2).println
  ("a" < "b").println

  // ok, so in scala, as long as you implement Ordered[A] you can do < and >.  But what about classes we don't control?
  case class Foo(name: String)
//  println(Foo("a") < Foo("b"))
//  could not find implicit value for parameter F0: scalaz.Order[com.github.scalaz_examples.order.OrderExample.Foo]
//  println(Foo("a") < Foo("b"))
//            ^

  // thats where scalaz's Order comes in!  All types will have < and > under the condition there is a instance
  // of the Order[A] typeclass that matches the type in question
  implicit val fooOrder: Order[Foo] = Order.orderBy(_.name)
  println(Foo("a") < Foo("b"))

  // now, since you can compare le, eq, lt, we can do fun things like get min/max!
  println(Foo("a") max Foo("b"))

  // you also now have access to get an type that explains the order
  println(Foo("a") ?|? Foo("b"))
  // LT
  // this could be useful for matching
  Foo("a") ?|? Foo("b") match {
    case LT | EQ => println("see?")
    case GT => "what?  How did it get here?!?!?!".println
  }
  // to me this is easier to read than nexted if statements.  Just have to get used to the ?|? operator
}
