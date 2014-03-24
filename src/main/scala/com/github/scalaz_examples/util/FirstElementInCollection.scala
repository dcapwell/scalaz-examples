package com.github.scalaz_examples.util

object FirstElementInCollection extends App {
  // some times you want to add behaviors to a given collection; say List[A].  You could do the following
  implicit class FirstElementList[A](list: List[A]) {
    def first: A = list.head
  }

  println(List(1, 2, 3).first)
}

object FirstElementHigherOrder extends App {
  // thats fine and all, but what if I could get first to work for more types... Would have to repeat multiple times
  // OR... MA!
  trait FirstElementOpt[M[_], A] { self =>
    def first(): A
  }

  implicit def seqFirst[A](seq: Seq[A]): FirstElementOpt[Seq, A] = new FirstElementOpt[Seq, A] {
    def first(): A = seq.head
  }

  println(Seq(1, 2, 3) first)
  // all sub implementations will match
  println(List(Set(1), List("two")) first)
  println(Vector("one", "two", "three") first)

  // now we can add it to non collection classes!
  case class Foo[A](value: A)
  implicit def fooFirst[A](foo: Foo[A]): FirstElementOpt[Foo, A] = new FirstElementOpt[Foo, A] {
    override def first(): A = foo value
  }

  println(Foo("hello") first)
}
