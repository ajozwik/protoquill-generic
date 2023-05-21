package pl.jozwik.quillgeneric

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}
import com.typesafe.scalalogging.StrictLogging
import io.getquill.NamingStrategy
import org.scalatest.concurrent.{AsyncTimeLimitedTests, TimeLimitedTests}
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.{AnyWordSpecLike, AsyncWordSpecLike}
import org.scalatest.{BeforeAndAfterAll, TryValues}
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers
import pl.jozwik.quillgeneric.model.AddressId

import scala.util.*

trait AbstractSpecScalaCheck extends AbstractSpec with Checkers

trait Spec extends StrictLogging {
  val TIMEOUT_SECONDS = 600
  val timeLimit       = Span(TIMEOUT_SECONDS, Seconds)
}

object AbstractSpec {
  sys.props.put("quill.macro.log", false.toString)
  sys.props.put("quill.binds.log", true.toString)
  val defaultNamingStrategy: NamingStrategy = Strategy.namingStrategy
}

trait AbstractSpec extends AnyWordSpecLike with TryValues with TimeLimitedTests with Spec with Matchers with BeforeAndAfterAll {

  protected val now: Instant             = Instant.now().truncatedTo(ChronoUnit.SECONDS)
  protected val today: LocalDate         = LocalDate.now
  protected val dateTime: LocalDateTime  = LocalDateTime.ofInstant(now, ZoneOffset.UTC)
  protected val strategy: NamingStrategy = AbstractSpec.defaultNamingStrategy

  protected val (offset, limit) = (0, 100)
  protected val generateId      = true
  protected val addressId: AddressId = AddressId(1)

  extension[T] (task: Try[T])
    def runUnsafe() = task match {
      case Success(s) =>
        s
      case Failure(th) =>
        throw th
    }

}

trait AbstractAsyncSpec extends AsyncWordSpecLike with AsyncTimeLimitedTests with Spec with Matchers
