package example

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import kamon.Kamon
import kamon.trace.Identifier

class KamonDatadogSpanIdToLogback extends ClassicConverter {

  override def convert(event: ILoggingEvent): String = {
    val spanId = Kamon.currentSpan.id

    if (spanId == Identifier.Empty)
      ""
    else
      BigInt(spanId.string.takeRight(16), 16).toString
  }
}
