package com.github.scalaz_examples.monads

import scalaz._, Scalaz._

object LetThereBeState extends App {
  // in normal java projects, mutable state is the norm.  Knowing when state changes and which threads are effected
  // is a tough problem.  With the state monad, it tries to capture the state and its changes and pass it along
  // that way no mutable state ever gets changed, but the system can move forward.  So how does this look like?

  // lets say you have a Building and Person types.  A building can have many persons living in it at once.  How
  // would this look with the state monad?
  type Person = String
  case class Building(residents: List[Person])

  // since this class is immutable, you could keep creating new copies and passing it around, but lets absorb changes
  // in a state

  def moveIn(family: List[Person]) = State[Building, Unit] { building =>
    (Building(building.residents ++ family), ())
  }

  def moveOut(family: List[Person]) = State[Building, List[Person]] { building =>
    val people = building.residents.filterNot(family.contains)
    (Building(people), family)
  }

  def history = for {
    newFamily <- moveIn(List("john", "amy"))
    breakup <- moveOut(List("john"))
    dating <- moveIn(List("tim"))
    withChild <- moveIn(List("jill"))
  } yield withChild

  // as we see with the moveIn and moveOut functions, a new state is generated based off the input.  The value of the
  // building is not changed though.  These are all lazy since no building is defined yet.  Lets try creating a building.

  val building = Building(List())

  // lets fast-forward though history
  history(building)
  // (Building(List(amy, tim, jill)),())

  // as we see with the output, once we add the building, we get back the state after all transformations are done
  // one other thing to note is that the history function is taking advantage of the flatMap nature of the state monad!
  // no where in the code do we pass around state, yet the state falls though each call!  So how would the same example
  // look in normal java (but scala syntax)?

  var b2 = Building(List())
  b2 = b2.copy(residents = b2.residents ++ List("john", "amy"))
  b2 = b2.copy(residents = b2.residents.filterNot(List("john").contains))
  b2 = b2.copy(residents = "tim" :: b2.residents)
  b2 = b2.copy(residents = "jill" :: b2.residents)
  // Building(List(jill, tim, amy))

  // we see we get the same as the above, but you could see that there is a whole lot more going on because Building
  // is immutable, so this is what the code would look like mutable

  class Building2(var residents: collection.mutable.ListBuffer[String])
  val b3 = new Building2(collection.mutable.ListBuffer())
  b3.residents.append("john", "amy")
  b3.residents.-=("john")
  b3.residents.append("tim")
  b3.residents.append("jill")
  // Building2(ListBuffer(amy, tim, jill))

  // again, we get the same thing but now this object isn't safe to pass around.  And any update is not thread-safe
  // so if you pass this object around, synchronized or locks are needed

  // another advantage of the State monad is that since you build up the functions to apply once a state is provided
  // the caller of these functions get to control when mutations happen or build up their own histories and functions.
  // we defer state changes until the last possible moment when its really needed rather than sprinkling the changes
  // all over

  // also, once you get used to the for notation for chaining States, the code clearly shows the intent and
  // looks very similar to the mutable code.
}
