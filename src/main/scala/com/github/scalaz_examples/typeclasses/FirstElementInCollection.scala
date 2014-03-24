package com.github.scalaz_examples.typeclasses

object FirstElementInCollection extends App {
  // some times you want to add behaviors to a given collection; say List[A].  You could do the following
  implicit class FirstElementList[A](list: List[A]) {
    def first: A = list.head
  }

  println(List(1, 2, 3).first)

  // thats fine and all, but what if I could get first to work for more types... Would have to repeat multiple times
  // OR... MA!
  trait FirstElementOpt[M[_], A] { self =>
    def !(): A
  }

  implicit def seqFirst[A](seq: Seq[A]): FirstElementOpt[Seq, A] = new FirstElementOpt[Seq, A] {
    def !(): A = seq.head
  }

  println(Seq(1, 2, 3) !)
  println(List(1, 2, 3) !)
  println(Vector(1, 2, 3) !)

  // now we can add it to non collection classes!
  case class Foo[A](value: A)
  implicit def fooFirst[A](foo: Foo[A]): FirstElementOpt[Foo, A] = new FirstElementOpt[Foo, A] {
    override def !(): A = foo value
  }

  println(Foo("hello") !)
}
