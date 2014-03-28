package com.github.scalaz_examples.util

import scalaz._
import Scalaz._

object ShortHandExamples extends App {
  // working with Option
  val leftOpt = 1.some // shorthand for Some(1)
  val rightOpt = 3.some

  // short-hand for if condition, return Option(data)
  (1 < 10) option 1
  // Some(1)

  (1 > 10) option 1
  // None

  // match against both
  // tuple is different than ->, since it uses Apply, which is like point, which will create a new Option[(_, _)]
  leftOpt tuple rightOpt match {
    case Some((left, right)) => (left, right) // (1, 3)
    case None => assert(false, "shouldn't happen")
  }

  leftOpt tuple none match {
    case Some((_,_)) => assert(false, "shouldn't happen")
    case None => "None found!"
  }

  // shorthand for getOrElse
  leftOpt | 20
  // 1

  none | 20
  // 20

  // shorthand for getOrElse Monoid#zero
  ~leftOpt
  // 1

  // have you ever wished you could map from a type to another type (not a monad[A] to monad[B], but "i have an A, give me a B")?
  20 |> {(value) => "hi"}
  // "hi"

  // what if you are working with java, and something can be null and you want to getOrElse it?
  val javaVal: String = null
  javaVal ?? "not null!"
  // "not null!"

  // have you ever missed Java's ? true : false syntax?
  // you can do something similar now on any boolean
  true ? "worked" | "broken"
  // "worked"

  // the eval of true and false is lazy, so if true, then false doesn't eval!
  // second, until the | is given, the function does nothing, so you could make true ? "default" a method!
  def canDo = false ? "should not have come"
  canDo | "couldn't do!"
  // "couldn't do!"

  // ranges
  1 |-> 10
  // List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
}
