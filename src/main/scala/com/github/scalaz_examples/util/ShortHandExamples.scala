package com.github.scalaz_examples.util

import scalaz._
import Scalaz._

object ShortHandExamples extends App {
  // working with Option
  val leftOpt = 1.some // shorthand for Some(1)
  val rightOpt = 3.some

  // short-hand for if condition, return Option(data)
  ((1 < 10) option 1).println
  ((1 > 10) option 1).println

  // match against both
  // tuple is different than ->, since it uses Apply, which is like point, which will create a new Option[(_, _)]
  leftOpt tuple rightOpt match {
    case Some((left, right)) => (left, right).println
    case None => "nothing found".println
  }

  leftOpt tuple none match {
    case Some((_,_)) => assert(false, "shouldn't happen")
    case None => "None found!".println
  }

  // shorthand for getOrElse
  (leftOpt | 20).println
  (none | 20).println

  // shorthand for getOrElse Monoid#zero
  (~leftOpt).println

  // have you ever wished you could map from a type to another type (not a monad[A] to monad[B], but "i have an A, give me a B")?
  (20 |> {(value) => "hi"}).println

  // what if you are working with java, and something can be null and you want to getOrElse it?
  val javaVal: String = null
  (javaVal ?? "not null!").println

  // have you ever missed Java's ? true : false syntax?
  // you can do something similar now on any boolean
  (true ? "worked" | "broken").println
  // the eval of true and false is lazy, so if true, then false doesn't eval!
  // second, until the | is given, the function does nothing, so you could make true ? "default" a method!
  def canDo = false ? "should not have come"
  (canDo | "couldn't do!").println
}
