package com.gopivotal.scalaz_examples

import scalaz._
import Scalaz._

package object lens {
  case class Version(major: Int, minor: Int)
  case class Foo(name: String, version: Version)
  case class FooNode(value: Foo, children: List[FooNode] = List())

  implicit val showVersion = Show.shows[Version] {version =>
    s"${version.major}.${version.minor}"
  }
  implicit val showFoo = Show.shows[Foo] {foo =>
    s"${foo.name} :: ${foo.version.show}"
  }

  private def walkFoo(prefix: String)(node: FooNode): String = {
    val sb = new StringBuilder()
    sb.append(node.value.show)
    for {
      child <- node.children
    } yield sb.append("\n").append(prefix).append(walkFoo(prefix + prefix)(child))
    sb.toString()
  }

  implicit val showNode = Show.shows[FooNode](walkFoo("\t"))

  // have you ever had a deeply nested object, and you want to zoom-in on a smaller section of it?
  // have you ever done this with immutable objects and wanted to just change that small zoomed in section?
  val tree = FooNode(
    Foo("root", Version(0, 0)),
    List(
      FooNode(Foo("left-h1", Version(1, 0))),
      FooNode(Foo("right-h1", Version(0, 1)))
    )
  )
}
