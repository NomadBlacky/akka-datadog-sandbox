package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import kamon.trace.Span.Kind

import java.util.concurrent.Executors
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

object Main extends StrictLogging {
  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutor {
    Executors.newFixedThreadPool(
      3,
      (r: Runnable) => {
        val t = Executors.defaultThreadFactory().newThread(r)
        t.setDaemon(true)
        t
      }
    )
  }
  private implicit val system: ActorSystem = ActorSystem("akka-datadog-sandbox")

  def main(args: Array[String]): Unit = try {
    Kamon.init()

    val span = Kamon.spanBuilder("main").kind(Kind.Internal).tag("foo", "bar").mark("hoge").start()

    logger.info("Start")
    Kamon.runWithSpan(span, finishSpan = true) {
      val f1 = exampleSlowProcess()
      val f2 = exampleHTTPProcess()
      val awaitable = for {
        _ <- f1
        _ <- f2
      } yield ()
      Await.ready(awaitable, Duration.Inf)
    }
    logger.info("Done the process")
  } finally {
    Await.ready(Kamon.stop(), Duration.Inf)
    Await.ready(system.terminate(), Duration.Inf)
  }

  // @Trace
  private def exampleSlowProcess()(implicit ec: ExecutionContext): Future[Unit] = {
    Kamon.span("exampleSlowProcess") {
      val metric = Kamon.gauge("example.gauge").withoutTags()
      Future
        .traverse((1 to 20).toList) { _ =>
          Future {
            Kamon.span("random") {
              Thread.sleep(1000L)
              val d = Random.between(0d, 100d)
              logger.info(s"$d")
              metric.update(d)
            }
          }
        }
        .map(_ => ())
    }
  }

  private def exampleHTTPProcess(): Future[Unit] = {
    logger.info("Execute: GET https://httpbin.org/get")
    val request = HttpRequest(uri = "https://httpbin.org/get")
    Http().singleRequest(request).map(r => logger.info(r.headers.mkString(",")))
  }
}
