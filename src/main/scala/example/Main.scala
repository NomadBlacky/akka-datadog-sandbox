package example

import kamon.Kamon
import kamon.trace.Span.Kind

import scala.util.Random

object Main {
  def main(args: Array[String]): Unit = {
    Kamon.init()

    val span   = Kamon.spanBuilder("example.span").kind(Kind.Internal).tag("foo", "bar").mark("hoge").start()
    val metric = Kamon.gauge("example.gauge").withoutTags()

    println("Calculating...")
    LazyList
      .continually {
        Thread.sleep(5000L)
        val d = Random.between(0d, 100d)
        println(d)
        metric.update(d)
      }
      .take(10)
      .force
    println("Done.")

    span.finish()
    Kamon.stop()
  }
}
