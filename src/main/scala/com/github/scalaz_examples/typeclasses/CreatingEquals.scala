package com.github.scalaz_examples.typeclasses

object CreatingEquals extends App {
  // as you see in the equal package, the equal typeclass can be very useful to make sure that
  // equality checks are type-safe.  But how could you implement this without scalaz?

  // simple Equal interface to define the equal behavior
  trait Equal[A] {
    def equal(a1: A, a2: A): Boolean
  }

  // so how do we decorate (pimp) other objects to get this behavior?  With scala implicits, you can define a function
  // that takes a A and creates a Opt[A] that has new methods.
  trait InstanceOpt[A] {
    def self: A
    def ===(a2: A)(implicit eq: Equal[A]): Boolean = eq.equal(self, a2)
    def =/=(a2: A)(implicit eq: Equal[A]): Boolean = !eq.equal(self, a2)
    def println(prefix: String = ""): Unit = Predef.println(s"${prefix}${self}") // too lazy to include show
  }
  import scala.language.implicitConversions
  implicit def toInstance[A](a: A): InstanceOpt[A] = new InstanceOpt[A] {def self: A = a}
  // an implication with Int
  implicit val intEqual: Equal[Int] = new Equal[Int] {
    override def equal(a1: Int, a2: Int): Boolean = a1 == a2
  }

  // now we can use === with int
  (1 =/= 3).println("1 =/= 3: ")
  (3 === 3).println("3 === 3: ")

  // but int === string won't compile
//  1 === "1"
// type mismatch;
//  found   : String("1")
//  required: Int
//  1 === "1"
//     ^

  // This is slightly different than how scalaz implemented equals, but its a very simple one that lets you
  // override the Equal used without defining different implicits.  The spirit of the implementation is the same.

  // This lets you define new behaviors without using inheritance, but can you still use the object like it was?
//  def selfEquals[A](eq: Equal[A]) = eq.equal(eq, eq)
//  selfEquals(1)
//  type mismatch;
//  found   : Int(1)
//  required: com.github.scalaz_examples.typeclasses.CreatingEquals.Equal[?]
//  selfEquals(1)
//         ^

  // this should make sense since A is not of type Equal[A].  We can get what we want using the same trick InstanceOpt does
  def selfEquals[A](a: A)(implicit eq: Equal[A]): Boolean = eq.equal(a, a)
  selfEquals(1) println("selfEquals(1): ")
}
