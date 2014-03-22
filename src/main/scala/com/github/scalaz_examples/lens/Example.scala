package com.github.scalaz_examples.lens

import scalaz._
import Scalaz._

/**
 * Before looking at this file, please review package.scala, since that will contain top level types/functions
 */

object Example extends App {
  val newTree = {
    // add new node to left tree
    val leftNode = tree.children(0).copy(children = List(FooNode(Foo("left-h2", Version(2, 0)))))
    // now update root's children
    val children: List[FooNode] = tree.children.patch(0, List(leftNode), 1)
    // now update root
    tree.copy(children = children)
  }

  tree.println
  newTree.println

  // awesome!  tree updated and it wasn't too bad...
  // just wouldn't want to go deeper... unless I refactor this into multiple functions!
  // would be nice if that existed for me though!
}

object LensTreeExample extends App {

  // so, what is a lens?
  // a lens is a function that gives you a getter and setter over a (normally) immutable object.
  // immutable objects don't have setters!  So, a lens's setter will recursively copy all objects effected
  // so

  def majorVersion = Lens.lensu[Version, Int](
    get = _.major,
    set = (version, major) => version.copy(major = major)
  )

  def fooVersion = Lens.lensu[Foo, Version](
    get = _.version,
    set = (foo, version) => foo.copy(version = version)
  )

  def nodeValue = Lens.lensu[FooNode, Foo](
    get = _.value,
    set = (node, value) => node.copy(value = value)
  )

  def childrenValue = Lens.lensu[FooNode, List[FooNode]](
    get = _.children,
    set = (node, value) => node.copy(children = value)
  )

  def childNode = (index: Int) => Lens.lensu[FooNode, FooNode](
    get = _.children(index),
    set = (node, child) => node.copy(children = node.children.patch(index, List(child), 1))
  )

//  val nodeMajorVersion = childNode(1) andThen nodeValue andThen fooVersion andThen majorVersion

  // so, to get root's version
  val rootVersion = nodeValue andThen fooVersion get tree
  rootVersion.println
}

object SetLensTreeExample extends App {
  // great, we can view objects, but I really need to update it!

  import LensTreeExample._

  val newTree = nodeValue andThen fooVersion andThen majorVersion set(tree, 12)

  newTree.println
  // wooooooo... I got a new tree that updated major version... but thats not too cool, since this is at root!



  val nodeMajorVersion = childNode(1) andThen nodeValue andThen fooVersion andThen majorVersion

  val newNewTree = nodeMajorVersion.set(tree, 12)

  newNewTree.println

  val newNewNewTree = childrenValue.partial andThen
    PLens.listNthPLens(1) andThen
    nodeValue.partial andThen
    fooVersion.partial andThen
    majorVersion.partial set(tree, 14)

    newNewNewTree.get.println
  // 14.1 at root -> right-h1!
}
