package pl.jozwik.quillgeneric.monad

import io.getquill.H2JdbcContext
import org.scalatest.{ BeforeAndAfterAll, TryValues }
import pl.jozwik.quillgeneric.AbstractSpec

import scala.util.Try

trait AbstractTryJdbcSpec extends AbstractSpec with BeforeAndAfterAll with TryValues {

  extension [T](task: Try[T]) def runUnsafe() = task.success.value

  lazy protected val ctx = new H2JdbcContext(strategy, TryHelperSpec.pool)

  override def afterAll(): Unit = {
    ctx.close()
    super.afterAll()
  }

}
