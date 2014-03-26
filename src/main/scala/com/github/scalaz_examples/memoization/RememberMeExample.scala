package com.github.scalaz_examples.memoization

import com.google.common.cache.{CacheBuilder, Cache}
import java.util.concurrent.{TimeUnit, Callable}
import scalaz._
import Scalaz._
import com.google.common.base.Stopwatch

object RememberMeExample extends App {
  // we have all used a cache before, its a very simple idea: something takes a while to do, so save it in a faster
  // medium for quicker lookup.  If you use Java, you probably know Guava.  If you use Guava often, you probably
  // know Guava's Cache.
  def longRunning(input: Int): Int = {
    Thread.sleep(1000) // takes a long time!
    input * input
  }
  import scala.language.implicitConversions
  implicit def toCallable[U](unit: => U): Callable[U] = new Callable[U] {
    override def call(): U = unit
  }
  def timed[U](work: => U): U = {
    val sw = Stopwatch.createStarted()
    val data = work
    println(s"Timed(${sw.elapsed(TimeUnit.MILLISECONDS)}}): ${data}")
    data
  }
  // ignore the fact that this takes longer in output, most of the wait is class loading
  val cache: Cache[Int, Int] = CacheBuilder.
    newBuilder().
    build().
    asInstanceOf[Cache[Int, Int]] // required cause of issues with scalac
  timed(cache.get(1, longRunning(1)))
  timed(cache.get(1, longRunning(1)))

  // thats great and all, and we were able to make the cache less code with implicits, but I rather not have to deal
  // with it unless I need to
  // memo returns K => V, so need to set it as a val
  val longRunningMemo = Memo.immutableHashMapMemo(longRunning)

  // with immutableMapMemo, cacheing is now built into the function
  timed(longRunningMemo(1))
  timed(longRunningMemo(1))
  timed(longRunningMemo(2))
  timed(longRunningMemo(2))

  // can even combine the both if you want a more powerful cache
  def guavaMemo[K, V]: Memo[K, V] = Memo.memo[K, V] {method: (K => V) =>
    val cache: Cache[K, V] = CacheBuilder.
      newBuilder().
      build().
      asInstanceOf[Cache[K, V]]

    def ret(key: K): V = cache.get(key, method(key))
    ret
  }

  val longRunningMemoGuava = guavaMemo(longRunning)

  timed(longRunningMemoGuava(1))
  timed(longRunningMemoGuava(1))
}
