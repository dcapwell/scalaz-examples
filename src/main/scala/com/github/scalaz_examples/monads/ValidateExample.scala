package com.github.scalaz_examples.monads

object ValidateExample extends App {
  // In java, if you have an error condition, the classic way is to throw an exception
  // This is not a good idea in scala (anti-pattern), so first instinct is to return the Try[A] type!
  // Great, now I have an exception or data, but Try only works with Exception, why can't my error
  // condition be another type?  Well, why not use scala's Either?
  def divide(n: Int, d: Int): Either[Int, String] =
    if(d == 0) Right("Divide by 0")
    else Left(n / d)

  // so whats wrong with the above?  Well, if you read the docs (who does that?), then you would see that by convention
  // left is for errors.  Oh, so now I have to rewrite the code to follow convention
  def betterDivide(n: Int, d: Int): Either[String, Int] =
    if(d == 0) Left("Divide by 0")
    else Right(n / d)

  // so lets use in for-comprehension
//  for { res <- betterDivide(1, 1) } yield res.println
//  value map is not a member of Either[String,Int]
//  for { res <- betterDivide(1, 1) } yield res.println
//                     ^

  // so, can't use in for-comprehension since there is no map.  And that makes sense, because the convention
  // is that Left is an error, but no where in Either does it give precedence to either.

  import scalaz._
  import Scalaz._
  // validation to the rescue!
  def valDivide(n: Int, d: Int): Validation[String, Int] =
    if(d === 0) "Divide by 0".fail
    else (n / d).success

  // prints out 1
  for { res <- valDivide(1, 1) } yield res.println
  // doesn't print out
  for { res <- valDivide(1, 0) } yield res.println

  // great, now I get what I want but convention isn't what I use, I use the Failure response, which maps to
  // the left.  Also I can use it within for-comprehension and it will fail fast!
}

import scalaz._
import Scalaz._
import com.google.common.base.Strings

object ObjectValidationExample extends App {
  // lets say we get a object from a user (maybe from REST endpoint).  This user may not have created the object
  // properly, so we want to make sure that the object is good before doing work.  We could validate the full
  // object and return whats missing!
  case class User(user: String, id: String, age: Int)
  implicit val userShow = Show.showFromToString[User]
  def validateUser(foo: User) =
    if(foo == null) "Foo not defined; given null".fail
    else if(Strings.isNullOrEmpty(foo.user)) "User name not defined".fail
    else if(Strings.isNullOrEmpty(foo.id)) "User id is not defined".fail
    else if(foo.age < 21) "User must be 21 or older to join".fail
    else foo.success

  // cool, now we can validate the user!
  validateUser(User(null, null, 5)).println
  // Failure("User name not defined")
  // thats not that nice, I only know the first error!  I could fix this by making an error list and... wait
  // someone must have done this for me before!
  def validateUserNotNull(foo: User): Validation[String, User] = (foo == null)? "Foo not defined; given null".fail[User] | foo.success
  def validateUserName(foo: User): Validation[String, User] = (Strings.isNullOrEmpty(foo.user))? "User name not defined".fail[User] | foo.success
  def validateUserId(foo: User): Validation[String, User] = (Strings.isNullOrEmpty(foo.id))? "User id is not defined".fail[User] | foo.success
  def validateUserAge(foo: User): Validation[String, User] = (foo.age < 21)? "User must be 21 or older to join".fail[User] | foo.success

  def validateUserAll(foo: User) =
    (validateUserNotNull(foo).toValidationNel |@|
      validateUserName(foo).toValidationNel |@|
      validateUserId(foo).toValidationNel |@|
      validateUserAge(foo).toValidationNel) ((a, b, c, d) => foo)

  // prints nothing cause it failed
  for {user <- validateUserAll(User(null, null, 5))} yield user.println
  validateUserAll(User(null, null, 5)) match {
    case Success(e) => s"Should have failed".println
    case Failure(e) => e.println
  }

  // as we see, the failed results are merged together.
}
