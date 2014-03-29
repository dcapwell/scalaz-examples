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
//  res0.join
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

object BuildingMonads extends App {
  // "In this section, we’re going to look at an example of how a type gets made, identified as a monad and then given
  // the appropriate Monad instance. … What if we wanted to model a non-deterministic value like [3,5,9], but we wanted
  // to express that 3 has a 50% chance of happening and 5 and 9 both have a 25% chance of happening?" - LYAHFGG

  // so, following eed3si9n.com/learning-scalaz/Making+monads.html
  case class Prob[A](list: List[(A, Double)])
  trait ProbInstances {
    def flatten[B](xs: Prob[Prob[B]]): Prob[B] = {
      def multall(innerxs: Prob[B], p: Double) =
        innerxs.list map { case (x, r) => (x, p * r) }
      Prob((xs.list map { case (innerxs, p) => multall(innerxs, p) }).flatten)
    }
    import scala.language.implicitConversions
    implicit def probShow[A](implicit s: Show[List[(A, Double)]]): Show[Prob[A]] =
      Show.show(p => s.show(p.list))
    // "Is this a functor? Well, the list is a functor, so this should probably be a functor as well, because we just
    // added some stuff to the list." - LYAHFGG
    implicit val probFunctor = new Functor[Prob] with Monad[Prob] {

      def bind[A, B](fa: Prob[A])(f: (A) => Prob[B]): Prob[B] = flatten(map(fa)(f))

      def point[A](a: => A): Prob[A] = Prob((a, 1.0))

      override def map[A, B](fa: Prob[A])(f: (A) => B): Prob[B] =
        Prob(fa.list.map {case (x, p) => (f(x), p)})
    }
  }
  trait ProbFunctions {
    def apply[A](data: (A, Double)*): Prob[A] = Prob(data.toList)
  }
  case object Prob extends ProbInstances with ProbFunctions

  val prob = Prob((1, 2.0), (2, 2.0))
  prob.println

  // in console, it wouldn't include map until this was done, even though the import is in the companion object
//  import Prob._ // in console I need to import, but not when run normally
  val res2 = Prob((3, 0.5), (5, 0.25), (9, 0.25)) map {-_}
  // Prob[Int] = Prob(List((-3,0.5), (-5,0.25), (-9,0.25)))

  // lets work with Coins now
  sealed trait Coin
  case object Heads extends Coin
  case object Tails extends Coin
  implicit val coinEqual: Equal[Coin] = Equal.equalA

  def coin: Prob[Coin] = Prob(Heads -> 0.5 :: Tails -> 0.5 :: Nil)
  def loadedCoin: Prob[Coin] = Prob(Heads -> 0.1 :: Tails -> 0.9 :: Nil)

  def flipThree: Prob[Boolean] = for {
    a <- coin
    b <- coin
    c <- loadedCoin
  } yield { List(a, b, c) all {_ === Tails} }

  flipThree
  // [(false,0.025),(false,0.225),(false,0.025),(false,0.225),(false,0.025),(false,0.225),(false,0.025),(true,0.225)]
}
