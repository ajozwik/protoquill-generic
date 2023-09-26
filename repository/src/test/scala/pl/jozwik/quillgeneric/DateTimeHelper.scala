package pl.jozwik.quillgeneric

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object DateTimeHelper {
  def localDateTimeNow:LocalDateTime =
    LocalDateTime.now.truncatedTo(ChronoUnit.MILLIS)
}
