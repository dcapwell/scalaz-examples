package com.github.scalaz_examples.magic

import scalaz._, Scalaz._

object ItsApplicativeToMe extends App {
  // as we saw in ValidateExample.scala, you are able to compose Validate instances together into a new one.
  // so how does that magic work?  Lets take a step back and ask our selves what is an Applicative (Validation is one)?

  import scala.language.higherKinds
  trait Applicative[F[_]] extends Functor[F] {
    // point in scalaz
    def pure[A](a: => A): F[A]
    // ap in scalaz (found in Apply which applicative extends from), but the ApplyOpt trait renames it to <*>
    def <*>[A,B](fa: => F[A])(f: => F[A => B]): F[B]
  }

  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  // so to explain Applicative, you first have to get Functor
  // so what is a Functor?  Anything with the map function.
  // this looks just like the collection api!
  println(List(1, 2, 3) map {_ + 1})

  // thats cause map on a collection is just syntax sugar of a Functor!
  // thats really all you need to know.  There are some cool things you can do with Functors

  // you can replace all the values with a new one
  (List(1, 2, 3) >| "x").println
  (List(1, 2, 3) as "x").println
  // really just List(1, 2, 3) map { "x" }

  // make a tuple of each A with a new value
  List(1, 2, 3).strengthL("x").println
  // really just map {("x", _)}
  List(1, 2, 3).strengthR("x").println
  // really just map {(_, "x")}

  // there are more things, but thats all we care about now, now lets move on to Applicative!

  // so an Applicative is-a Functor (applicative functor), so what does it add?  As we see in the trait
  // defined above, pure and <*> are the main additions.

  // so whats pure?
  // given an A, wrap the value in a higher kind F which yields F[A]
  // its really hard to explain why this is cool, but the main thing to take out of this is the constructor is
  // abstracted out!  Given any Applicative type, you can create a new instance of it the same way as anything else.
  // scalaz adds this to every object (and renames it point)
  1.point[Option].println
  1.point[List].println

  // and now, <*>
  // lets look at the type of <*> and see if we can deduce what it does
  // def <*>[A,B](fa: => F[A])(f: => F[A => B]): F[B]
  // given a higher kind F[A] and F[A => B] return an F[B]
  // I get map (given F[A] and A => B, return F[B]) but why would I ever care about this?
  // well, lets look at the list case.  Say you have a list of data and a list of functions
  val data = List(1, 2, 3)
  val funcs = List((_: Int) + 1, (_: Int) * 2, (_: Int) / 2 + 7)
  // you can apply the functions to data
  (data <*> funcs).println
  // ok, so each function was applied to each data once.  Not too sure when I would use this
  // so how does this tie together to get |@|?

  ((List(1, 2, 3) |@| List(4, 5, 6)) {_ + _}).println
  // as you see with the output, it looks like the same number of combinations where generated
  // and each pair was evaluated with +.  This feels like there is a relationship between |@| and <*>.

  // lets look at the core method |@| uses
  // def apply2[A, B, C](fa: => F[A], fb: => F[B])(f: (A, B) => C): F[C] = ap(fb)(map(fa)(f.curried))
  // ap is the other name for <*>, so to rewrite
  // def apply2[A, B, C](fa: => F[A], fb: => F[B])(f: (A, B) => C): F[C] = <*>(fb)(map(fa)(f.curried))
  // so, you map over fa using f.curried, which will return a F[B => C]
  // you then apply <*> to fb and the F[B => C], which will yield a F[C]
  // so because of <*>, I can now compose applicatives together!
  // |@| aka oink operator aka macaulay culkin operator = win!
}

object ApplicativeFun extends App {
  // lets have some fun with this
  // Let’s try implementing a function that takes a list of applicatives and returns an applicative that has a list as
  // its result value. We’ll call it sequenceA.
  import scala.language.higherKinds
  def sequenceA[F[_]: Applicative, A](list: List[F[A]]): F[List[A]] = list match {
    case Nil => (Nil: List[A]).point[F]
    case x :: xs => (x |@| sequenceA(xs)) {_ :: _}
  }
  sequenceA(List(1.some, 2.some, 3.some)).println

  // so before we move on, lets explore "lift".  When going over examples of functional programming, you will see
  // lift and currying constantly, so knowing how they work and when to use them is key to understanding
  // functors, applicative functors, and monads

  def foo(a1: Int, a2: Int): Int = a1 + a2
  // foo takes an a1 and a2 at the same time and returns an Int, so (Int, Int) => Int
  // what does currying do?

  // when you use def to create a function, that function isn't really a Function2 instance.
  // So even though the IDE will say that foo can use the functions from Function2, you can't without
  // partially applying it to make a new function

  val res24 = foo _
  // (Int, Int) => Int = <function2>

  // what happened here is that we took the function foo, and set it to val res24, but the _ notation says that
  // the arguments of foo need to be filled in, so the type of res24 is (Int, Int) => Int
  // once we do that, we have access to the methods on Function2, mainly curried

  val fooC = res24.curried
  // Int => Int => Int
  // so we took a binary function foo, and made it a unary function that returns a function Int => Int

  // why are we talking about this?  Well for one <*> seems confusing when you would ever use it, so lets go over
  // an example
  val one = 1.some
  val two = 2.some

  // so we have two Option[Int] instances and a binary function foo that works on Ints, so how do we make them
  // play nicly with each other?  Remember, functional programming is all about composing functions together, so
  // how does composition work in this case?
  // Option is a functor, so it takes a map from Int to some other type: Int => B
  // so lets try currying foo!
  val oneCurried = one map fooC
  // Option[Int => Int]
  // now that the types match for <*>, we can apply two
  (two <*> oneCurried).println
  // Some(3)

  // so <*> lets us take a unary functor and work with binary functions!

  // lets try with three params; simpler to curry up front for this example
  def bar(a: Int)(b: Int)(c: Int): Int = a + b + c
  val three = 3.some

  three <*> (two <*> one.map(bar))
  // Some(6)

  // with currying and applicative functors, you can work with generic functions that know nothing about the
  // applicative functor wrapping the object, but only know about the object itself

  // so how does this get us to lift?
  val liftedOptOfInt = Functor[Option].lift(bar)
  // Option[Int] => Option[Int => (Int => Int)]

  // as we see above, lift takes a function A => B and returns F[A] => F[B]

  // this gives us something very similar to what we were doing above, but lets us do it at a more generic
  // sense.  Rather than mapping over a given instance of F[A], I can pass in any F[A], apply it to the function
  // and get back the results

  three <*> (two <*> liftedOptOfInt(one))
  // Some(6)

  // my main issue with this is the order in which I must apply things
  // I look at the function def and type, its left to right
  // I see the above code and its applied right to left
  // Is there a way to "swap" the oder?
  // I really want to write liftedOptOfInt(one) <*> two <*> three!

  // I am not hinting at anything!  I just don't know yet!
}
