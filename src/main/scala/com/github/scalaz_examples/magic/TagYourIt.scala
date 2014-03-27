package com.github.scalaz_examples.magic

import scalaz._, Scalaz._

object TagYourIt extends App {
  // If you come from a java background, you know that whenever you want to redefine a type to be more explicit you
  // had to extend it
  trait MyInt
  trait KiloGramInt extends MyInt
  // this wouldn't work for classes you couldn't extend, so you had to start wrapping the type
  case class KiloGramInt2(value: Int)
  // this now makes it clear that your input or output is in KiloGrams, but its painful to use because its value
  // is wrapped around non-useful dressing.

  // now you start using scala and go... OMG TYPE ALIAS!  This solve everything right?!?!?!
  type KiloGramInt3 = Int
  // now I can say that my input and output are KiloGramInt3 so its very clear what the format and rules around it are!
  // but wait, since its just an alias, every int matches

  def someWorkWithKiloGramInt3(value: KiloGramInt3): KiloGramInt3 = value - 1
  someWorkWithKiloGramInt3(Enum[Int].max.get).println

  // umm, thats not fully what we want.  We want the user to be very sure they are working with kilograms!

  sealed trait KiloGram
  def KiloGram[A](a: A): A @@ KiloGram = Tag[A, KiloGram](a)

  def someWorkWithKiloGram(value: Int @@ KiloGram): Int @@ KiloGram = KiloGram(value - 1)
//  someWorkWithKiloGram(Enum[Int].max.get)
//  type mismatch;
//  found   : Int
//  required: scalaz.@@[Int,com.github.scalaz_examples.magic.TagYourIt.KiloGram]
//  (which expands to)  Int with AnyRef{type Tag = com.github.scalaz_examples.magic.TagYourIt.KiloGram}
//  someWorkWithKiloGram(Enum[Int].max.get)
//                        ^

  // cool, now that code won't compile unless the user goes through our function (or does the weird tag notation)
  // this means we could centralize validation to get the same benefits of the wrapper class, but still only work
  // with primitives!  If the user goes through the Tag notation, then they lose any centralized sanity checks
  // provided, so they could still break the code, but its more clear that they are doing that!

  // another cool thing about tagged types is that you can tag a generic type as we saw above!  That means that
  // each function can choose what types it works with, so
  def morePreciseWorkWithKiloGrams(value: Double @@ KiloGram): Double @@ KiloGram = KiloGram(value - 1)
  // lets the function work with doubles, but still keep the same tag that #someWorkWithKiloGram used, even
  // though the types where different

  // real work
  sealed trait JoulePerKiloGram
  def JoulePerKiloGram[A](a: A): A @@ JoulePerKiloGram = Tag[A, JoulePerKiloGram](a)
  def energyR(m: Double @@ KiloGram): Double @@ JoulePerKiloGram = JoulePerKiloGram(299792458.0 * 299792458.0 * m)

  val mass = KiloGram(20.0)
  // tag type makes it hard for the implicit system to find show =(
  println(energyR(mass))

  // tag types become more powerful when dealing with monoids.  Please check out the ItsAllMonoidToMe.scala
}
