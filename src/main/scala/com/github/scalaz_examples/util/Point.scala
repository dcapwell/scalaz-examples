package com.github.scalaz_examples.util

import scalaz._
import Scalaz._

object Point extends App {
  // as we saw in ShortHandExamples, we can wrap values in Some in a short hand and more readable format (depending on
  // your preferences)
  1.some.println

  // thats great and all, but I have my own custom object that I would like to wrap this in
  // or there is a Monad that I can use and no shorthand 1.awesomeMonad
  // thats where point comes in
  1.point[Option].println
  1.point[List].println

  // if working in java, you can use point to wrap values into scala types
  // this can be tricky because of null.  This fully depends on the implication of Applicative
  // for example, optionInstance says point(a) = Some(a).  So
//  class JavaClass
//  implicit val javaClassShow: Show[JavaClass] = Show.showFromToString
//  val javaInteropt: JavaClass = null
//  javaInteropt.point[Option].println
//  Exception in thread "main" scala.MatchError: null
// option show has a match error since it doesnt expect null
  //TODO file a bug to use Option rather than Some
  // when working with java, this may be safer, since you can handle the null checks
//  (javaInteropt |> {a => Option(a)}).println
  // or just wrap it Option(javaInteropt)

  // how point works is the same as all other type classes; you define an trait Foo[Y], and a implicit x
  // that implements that interface for the given type Y (in this case Int).
  // @see Scalaz.optionInstance

  // to create own own Applicative, we just follow the same pattern as with every typeclass
  case class Foo[A](value: A)
  implicit val fooApplicative: Applicative[Foo] = new Applicative[Foo] {
    def point[A](a: => A): Foo[A] = Foo(a)
    // convert Foo[A] to Foo[B].  Used to implement map
    def ap[A, B](fa: => Foo[A])(f: => Foo[(A) => B]): Foo[B] = Foo(f.value(fa.value))
  }
  implicit def fooShow[A]: Show[Foo[A]] = Show.showFromToString

  1.point[Foo].println
  1.point[Foo].map{_ => "one"}.println

  // ok, so I can do this, but why would I since I can just wrap the value directly?
  // main use-case I can see is that anything that works on higher-kinds can construct them without
  // needing to know how
  import scala.language.higherKinds
  def wrapAndString[M[_] : Applicative, A](data: A) = Applicative[M].point(s"$data")
  wrapAndString[List, Int](1).println
}
