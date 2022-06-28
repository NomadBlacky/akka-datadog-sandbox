package example

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import kamon.Kamon
import kamon.trace.Identifier

class KamonDatadogTraceIdToLogback extends ClassicConverter {

  override def convert(event: ILoggingEvent): String = {
    val traceId = Kamon.currentSpan.trace.id

    if (traceId == Identifier.Empty)
      ""
    else
      BigInt(traceId.string.takeRight(16), 16).toString
  }
}
