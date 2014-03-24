package com.github.scalaz_examples.magic

object FirstElementInCollection extends App {
  // some times you want to add behaviors to a given collection; say List[A].  You could do the following
  implicit class FirstElementList[A](list: List[A]) {
    def first: A = list.head
  }

  println(List(1, 2, 3).first)
}

object FirstElementHigherOrder extends App {
  // thats fine and all, but what if I could get first to work for more types... Would have to repeat multiple times
  // OR... MA (higher kind)!
  import scala.language.higherKinds
  trait FirstElementOpt[M[_], A] { self =>
    def first(): A
  }
  // what is M[_]?  Its a type M, that takes a param of type _ to be defined later.  Simple example would be
  // Seq.  Seq[A] is a Seq that takes a A.  So in M[_], M == Seq, and _ will become A later on.  Lets see
  // how that works.

  import scala.language.implicitConversions
  implicit def seqFirst[A](seq: Seq[A]): FirstElementOpt[Seq, A] = new FirstElementOpt[Seq, A] {
    def first(): A = seq.head
  }
  // see the FirstElementOpt[Seq, A]?  Where you had M[_] in the trait, you replace that with Seq!
  // not only can we define behaviors to generic collections, but this will work for any generic type!

  println(Seq(1, 2, 3).first)
  // all sub implementations will match
  println(List(Set(1), List("two")).first)
  println(Vector("one", "two", "three").first)

  // now we can add it to non collection classes!
  case class Foo[A](value: A)
  implicit def fooFirst[A](foo: Foo[A]): FirstElementOpt[Foo, A] = new FirstElementOpt[Foo, A] {
    def first(): A = foo.value
  }

  println(Foo("hello").first)
}

object TypeclassesWithHigherKinds extends App {
  // the above was cool, can see how to add behaviors to collection like objects, but how does this relate to typeclasses?
  // As we see with the different typeclasses, you define the behavior, and how to get an instance to implement
  // that behavior.  Once you do that you "pimp" objects of the same type as instance to make those functions
  // work on the object rather than passing the object

  // so lets create a typeclass that works on higher kinds
  import scala.language.higherKinds
  trait Foldable[M[_]] { self =>
    def leftFold[A, B](ob: M[A])(zero: B)(fn: (B, A) => B): B
  }
  // now lets implement a Folable List
  implicit val listFolable: Foldable[List] = new Foldable[List] {
    def leftFold[A, B](ob: List[A])(zero: B)(fn: (B, A) => B): B = ob.foldLeft(zero)(fn)
  }

  // now lets add foldLeft to list
  import scala.language.implicitConversions
  implicit class ListFoldableOpt[A](list: List[A])(implicit fold: Foldable[List]) { self =>
    def leftFold[B](zero: B)(fn: (B, A) => B): B = fold.leftFold(list)(zero)(fn)
  }

  // builtin foldLeft
  println(List(1, 2, 3).foldLeft(0)(_ + _))
  // pimped foldLeft
  println(List(1, 2, 3).leftFold(0)(_ + _))
}

object FoldableMonoids extends App {
  // the above example was cool, can see how to add missing functionality to collections (java's collections?),
  // but really, the power comes in by mixing different higher kinds together; lets go over monoid.

  // a monoid is a type that has a combine function that will combine two types into a new instance of the same type
  // and a zero defining the type's zero'th element
  // and combine should be associative!
  // (a combine b) combine c == a combine (b combine c)
  /// ok?
  trait Monoid[A] {
    // in scalaz, combine is named append
    def combine(a1: A, a2: A): A
    def zero: A
  }
  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    def zero: Int = 0

    def combine(a1: Int, a2: Int): Int = a1 + a2
  }

  // so why is this so special?  Well, with the foldLeft example, what are the params?
  // (zero)(combine) [assuming B == A].
  // so if A has a zero, then I can omit the first param, and if A == B, then I can omit the second param as well!
  import scala.language.higherKinds
  trait Foldable[M[_]] { self =>
    def leftFold[A, B](ob: M[A])(zero: B)(fn: (B, A) => B): B
  }
  // now lets create one for List
  implicit val listFolable: Foldable[List] = new Foldable[List] {
    def leftFold[A, B](ob: List[A])(zero: B)(fn: (B, A) => B): B = ob.foldLeft(zero)(fn)
  }

  // now lets pimp foldLeft on list
  import scala.language.implicitConversions
  implicit class ListFoldableOpt[A](list: List[A])(implicit fold: Foldable[List]) { self=>
    // fill in all args
    def leftFold[B](zero: B)(fn: (B, A) => B): B = fold.leftFold(list)(zero)(fn)
    // no worries, I got zero
    def leftFold[B](fn: (B, A) => B)(implicit m: Monoid[B]): B = fold.leftFold(list)(m.zero)(fn)
    // no worries, let me do all the work
    def leftFold(implicit m: Monoid[A]): A = fold.leftFold(list)(m.zero)(m.combine)
  }

  println(List(1, 2, 3).leftFold{(b: Int, a: Int) => a + b})
  println(List(1, 2, 3).leftFold)
//  println(List("one", "two", "three").leftFold) // wont compile
//  could not find implicit value for parameter m: com.github.scalaz_examples.magic.FoldableMonoids.Monoid[String]
//  println(List("one", "two", "three").leftFold)
//                               ^

  // even though there is no Monoid[String] (but there can be), we can use the Monoind[Int] to make leftFold work!
  println(List("1", "2", "3").leftFold{(b: Int, a: String) => a.toInt + b})
}