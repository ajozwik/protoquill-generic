package pl.jozwik.quillgeneric.monad

import io.getquill.H2JdbcContext
import org.scalatest.BeforeAndAfterAll
import pl.jozwik.quillgeneric.AbstractSpec

trait AbstractTryJdbcSpec extends AbstractSpec with BeforeAndAfterAll {

  lazy protected val ctx = new H2JdbcContext(strategy, HelperSpec.pool)

  override def afterAll(): Unit = {
    ctx.close()
    super.afterAll()
  }

}
