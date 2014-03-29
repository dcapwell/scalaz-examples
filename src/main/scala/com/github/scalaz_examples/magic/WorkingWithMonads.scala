package com.github.scalaz_examples.magic

import scalaz._, Scalaz._

object WorkingWithMonads extends App {
  // this section will cover useful functions related to Monad[F]

  // "It turns out that any nested monadic value can be flattened and that this is actually a property unique to monads.
  // For this, the join function exists." - LYAHFGG
  // from the Monad trait
  // def join[B](implicit ev: A <~< F[B]): F[B] = F.bind(self)(ev(_))

  val res0 = Some(1.some)
  // Some[Option[Int]]
  res0.join
//  <console>:15: error: could not find implicit value for parameter F0: scalaz.Bind[Some]
//    res0.join
//    ^

  val res1 = (Some(9.some): Option[Option[Int]])
  res1.join
  // Some(9)

  val res4 = List(List(1, 2, 3), List(4, 5, 6))
  res4.join
  // List(1, 2, 3, 4, 5, 6)
  res4.flatMap{list => list}
  // List(1, 2, 3, 4, 5, 6)

  // seems that join is just flatMap that returns the element inside
  Some(1.some).flatMap{v => v}
  // Some(1)


  // "The filterM function from Control.Monad does just what we want! … The predicate returns a monadic value whose
  // result is a Bool." - LYAHFGG
  // def filterM[M[_] : Monad](p: A => M[Boolean]): M[List[A]]
  val res9 = List(1, 2, 3) filterM { x => List(true, false) }
  // List[List[Int]] = List(List(1, 2, 3), List(1, 2), List(1, 3), List(1), List(2, 3), List(2), List(3), List())
  val res10 = List(1, 2, 3) filterM { x => List(true) }
  // List[List[Int]] = List(List(1, 2, 3))
  val res11 = List(1, 2, 3) filterM { x => List(false) }
  // List[List[Int]] = List(List())

  val res12 = List(1, 2, 3) filterM { x => List(false, false) }
  // List[List[Int]] = List(List(), List(), List(), List(), List(), List(), List(), List())

  val res13 = List(1, 2, 3) filterM { x => List(false, true) }
  // List[List[Int]] = List(List(), List(3), List(2), List(2, 3), List(1), List(1, 3), List(1, 2), List(1, 2, 3))

  val res14 = List(1, 2, 3) filterM { x => List(true, false) }
  // List[List[Int]] = List(List(1, 2, 3), List(1, 2), List(1, 3), List(1), List(2, 3), List(2), List(3), List())

  val res17 = Monad[Option].filterM(List(1, 2, 3))(x => Option(true))
  // Option[List[Int]] = Some(List(1, 2, 3))

  val res18 = Monad[Option].filterM(List(1, 2, 3))(x => Option(false))
  // Option[List[Int]] = Some(List())


  // there is also a foldLeftM which is the same as foldLeft, but the B returned is a Monad
  val res21 = List(1, 2, 3).foldLeftM(0){(acc, x) => (x > 9)? (none: Option[Int]) | (acc + x).some}
  // Option[Int] = Some(6)

  val res22 = List(2, 11, 3, 1).foldLeftM(0){(acc, x) => (x > 9)? (none: Option[Int]) | (acc + x).some}
  // Option[Int] = None
}
