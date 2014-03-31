package com.github.scalaz_examples.magic

import scalaz._, Scalaz._

object MonadTransformers extends App {
  // we have seen several different Monads so far, and you may have been wondering how I can use the different monads
  // so no one monad matches my use-case.  Maybe I need state but I also want there to be failure cases that option
  // handles, how do I get both?

  // lets add the examples from learning-scalaz/Monad+transformers.html
  def myName(step: String): Reader[String, String] = Reader {step + ", I am " + _}
  def localExample: Reader[String, (String, String, String)] = for {
    a <- myName("First")
    b <- myName("Second") >=> Reader { _ + "dy"}
    c <- myName("Third")
  } yield (a, b, c)

  localExample("Fred")
  // (String, String, String) = (First, I am Fred,Second, I am Freddy,Third, I am Fred)

  // now lets say that we don't want reader to return the Id monad, but Option, so failures can happen
  type ReaderTOption[A, B] = ReaderT[Option, A, B]
  object ReaderTOption extends KleisliFunctions with KleisliInstances {
    def apply[A, B](f: A => Option[B]): ReaderTOption[A, B] = kleisli(f)
  }

  // so lets say we have a Config type and want to read values out of it
  type Config = Map[String, String]
  def configure(key: String) = ReaderTOption[Config, String] {_.get(key)}

  // now if we call configure, we get back an instance that is capable of reading the key provided, but doesn't
  // read it yet (no config defined yet).
  configure("foo.bar.baz")(Map("foo.bar.baz" -> "biz"))
  // Some(biz)
  configure("does.not.exist")(Map("foo.bar.baz" -> "biz"))
  // None
}
