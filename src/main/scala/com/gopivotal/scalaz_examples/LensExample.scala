package com.gopivotal.scalaz_examples

import scalaz._
import Scalaz._

case class Version(major: Int, minor: Int)
case class Foo(name: String, version: Version)
case class FooNode(value: Foo, children: Seq[FooNode])

object LensExample extends App {
  val majorVersion = Lens.lensu[Version, Int](
    get = _.major,
    set = (version, major) => version.copy(major = major)
  )

  val fooVersion = Lens.lensu[Foo, Version](
    get = _.version,
    set = (foo, version) => foo.copy(version = version)
  )

  val nodeValue = Lens.lensu[FooNode, Foo](
    get = _.value,
    set = (node, value) => node.copy(value = value)
  )

  val childNode = (index: Int) => Lens.lensu[FooNode, FooNode](
    get = _.children(index),
    set = (node, child) => node.copy(children = node.children.patch(index, Seq(child), 1))
  )

  val nodeMajorVersion = childNode(1) andThen nodeValue andThen fooVersion andThen majorVersion

  implicit val fooNodeEquals = Equal.equal[FooNode]((left, right) => left == right)

  val tree = FooNode(
    Foo("root", Version(1, 0)),
    Seq(
      FooNode(
        Foo("left", Version(1, 1)), Seq()
      ),
      FooNode(Foo("right", Version(1, 2)), Seq())
    )
  )

  // get the top level value's major version

  val root = nodeValue.get(tree)
  println(s"Root's Major version: %s", root)

  val newTree = nodeMajorVersion.set(tree, 7)
  println(s"New tree after updating version: $newTree")

  tree === newTree
  println(s"Are the trees the same?: ${tree === newTree}")

}
