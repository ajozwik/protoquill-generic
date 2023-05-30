package pl.jozwik.quillgeneric.zio

import io.getquill.*
import org.scalatest.BeforeAndAfterAll
import pl.jozwik.quillgeneric.monad.HelperSpec
import pl.jozwik.quillgeneric.AbstractSpec
import zio.*

import javax.sql.DataSource

object ZioHelperSpec {
  def runLayerUnsafe[T: Tag](layer: ZLayer[Any, Throwable, T]): T =
    zio.Unsafe.unsafe { implicit unsafe =>
      zio.Runtime.default.unsafe.run(zio.Scope.global.extend(layer.build)).getOrThrow()
    }.get
}

trait AbstractZioJdbcSpec extends AbstractSpec with BeforeAndAfterAll {

  extension [T](task: Task[T]) def runUnsafe(): T = unsafe(task)

  protected def unsafe[T](task: Task[T]): T =
    Unsafe.unsafe { implicit unsafe =>
      val io = task.provideEnvironment(ZEnvironment(HelperSpec.pool))
      zio.Runtime.default.unsafe.run(io).getOrThrow()
    }

  lazy protected val ctx = new H2ZioJdbcContext(strategy)

  override def afterAll(): Unit = {
    ctx.close()
    super.afterAll()
  }

}
