package com.github.scalaz_examples.magic

import scalaz._, Scalaz._

// http://eed3si9n.com/learning-scalaz/Monoid.html
object ItsAllMonoidToMe extends App {
  // in FirstElementInCollection.scala, the monoid trait was created.  It showed the basic example of what monoids
  // are, but lets go over them in more detail

  // if you look at the * operator on ints, when you call any number * 1, you get back that number
  42 * 1 assert_=== 42

  // when you look at ++ with List, if you call List ++ Nil you get back the list
  List(1, 2, 3) ++ Nil assert_=== List(1, 2, 3)

  // if you do + on its, when you call any number + 0, you get back that number
  42 + 0 assert_=== 42

  // see a pattern?

  // now, look at those operations and use them with multiple values
  (3 * 2) * (8 * 5) assert_=== 3 * (2 * 8) * 5
  (3 + 2) + (8 + 5) assert_=== 3 + (2 + 8) + 5
  // they are also associative!

  // so whats a monoid?  something with a zero that when passed to a mappend(a1, a2) returns the other value passed in
  // and mappend is associative.

  // "A monoid is when you have an associative binary function and a value which acts as an identity with respect to
  // that function." - LYAHFGG

  List(1, 2, 3) mappend List(4, 5, 6) assert_=== List(1, 2, 3, 4, 5, 6)
  // the more scalaz way
  List(1, 2, 3) |+| List(4, 5, 6) assert_=== List(1, 2, 3, 4, 5, 6)

  Monoid[List[Int]].zero assert_=== List()
  Monoid[Int].zero assert_=== 0

  (42 |+| Monoid[Int].zero) assert_=== 42

  // "Another type which can act like a monoid in two distinct but equally valid ways is Bool. The first way is to have
  // the or function || act as the binary function along with False as the identity value. … The other way for Bool to
  // be an instance of Monoid is to kind of do the opposite: have && be the binary function and then make True the
  // identity value." - LYAHFGG

  // so ints and booleans both have multiple ways to be Monoids, so how can we deal with that?
  // thats where Tagged Types come in!

  // ||
  println((Tags.Disjunction(true) |+| Tags.Disjunction(false)))
  // true

  // &&
  println((Tags.Conjunction(true) |+| Tags.Conjunction(false)))
  // false

  println(Monoid[Boolean @@ Tags.Disjunction].zero |+| Tags.Disjunction(true))
  // true

  println(Monoid[Boolean @@ Tags.Disjunction].zero |+| Monoid[Boolean @@ Tags.Disjunction].zero)
  // false cause false || false === false

  // now for ints
  println(Tags.Multiplication(10) |+| Monoid[Int @@ Tags.Multiplication].zero)
  // 10

  println(Tags.Multiplication(42) |+| Tags.Multiplication(2))
  // 84


  // so what else are Monoids?

  // Ordering!
//  (Ordering.LT |+| Ordering.GT).println
//  value |+| is not a member of object scalaz.Ordering.LT
//  (Ordering.LT |+| Ordering.GT).println
//           ^
  // The whole object type is hitting us again...
  ((Ordering.LT: Ordering) |+| (Ordering.GT: Ordering)).println
  // LT

  ((Ordering.GT: Ordering) |+| (Ordering.LT: Ordering)).println
  // GT

  ((Ordering.LT: Ordering) |+| (Ordering.GT: Ordering)) |+| (Ordering.LT: Ordering) assert_=== (Ordering.LT: Ordering) |+| ((Ordering.GT: Ordering) |+| (Ordering.LT: Ordering))
  ((Ordering.GT: Ordering) |+| (Ordering.LT: Ordering)) |+| (Ordering.GT: Ordering) assert_=== (Ordering.GT: Ordering) |+| ((Ordering.LT: Ordering) |+| (Ordering.GT: Ordering))

  (Monoid[Ordering].zero |+| (Ordering.GT: Ordering)).println
  // GT

  // so some monoid ordering fun!
  // "OK, so how is this monoid useful? Let’s say you were writing a function that takes two strings, compares their
  // lengths, and returns an Ordering. But if the strings are of the same length, then instead of returning EQ right
  // away, we want to compare them alphabetically." - LYAHFGG
  def lengthCompare(lhs: String, rhs: String): Ordering = (lhs.length ?|? rhs.length) |+| (lhs ?|? rhs)

  lengthCompare("abc", "def").println
  // LT
  lengthCompare("zen", "ants").println
  // LT (cause zen is shorter)
}
