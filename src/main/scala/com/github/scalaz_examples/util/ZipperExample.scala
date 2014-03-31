package com.github.scalaz_examples.util

import scalaz._, Scalaz._

// one thing to note on zippers, scalaz doesn't provide zippers for lists, only streams.  So if you have a list
// you need to convert it to a stream first
object ZipperExample extends App {
  val res0 = Stream(1, 2, 3, 4).toZipper
  // Option[scalaz.Zipper[Int]] = Some(Zipper(<lefts>, 1, <rights>))

  val res1 = res0 >>= {_.next} // flatmap{_.next}
  // Option[scalaz.Zipper[Int]] = Some(Zipper(<lefts>, 2, <rights>))

  val res2 = res1 >>= {_.next}
  // Option[scalaz.Zipper[Int]] = Some(Zipper(<lefts>, 3, <rights>))

  val res3 = res2 >>= {_.previous}
  // Option[scalaz.Zipper[Int]] = Some(Zipper(<lefts>, 2, <rights>))

  // can also use it with for comprehension
  val res4 = for {
    a <- res0
    b <- a.next
    c <- b.next
    d <- c.previous
  } yield d.modify {_ => 7}
  // Option[scalaz.Zipper[Int]] = Some(Zipper(<lefts>, 7, <rights>))

  // get the current value
  res4.get.focus
  // 7

  // or get back new stream with changes
  val res5 = res4.get.toStream
  // Stream[Int] = Stream(1, ?)
  res5.toList
  // List(1, 7, 3, 4)

  // zipper seems like an iterator that lets you move forward/backwards/and modify in a safe way
  // because you mostly deal with option, if there is no left or right hand, the flatMaps become no-ops

  for {
    a <- Stream(1).toZipper
    b <- a.next
    c <- b.next
    d <- c.previous
  } yield d.modify {_ => 7}
  // Option[scalaz.Zipper[Int]] = None
}
