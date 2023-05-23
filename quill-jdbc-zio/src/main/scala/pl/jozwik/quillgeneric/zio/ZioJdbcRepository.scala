package pl.jozwik.quillgeneric.zio

import io.getquill.*
import io.getquill.context.ZioJdbc.QIO
import io.getquill.context.jdbc.{ ObjectGenericTimeDecoders, ObjectGenericTimeEncoders }
import io.getquill.context.qzio.ZioJdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.monad.*
import pl.jozwik.quillgeneric.repository.*
import zio.{ Task, ZIO }

import javax.sql.DataSource

type ZioJdbcContextWithDateQuotes[+Dialect <: SqlIdiom, +Naming <: NamingStrategy] = ZioJdbcContext[Dialect, Naming]
  with ObjectGenericTimeDecoders
  with ObjectGenericTimeEncoders

trait ZioJdbcRepositoryWithGeneratedId[K, T <: WithId[K], C <: ZioJdbcContext[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransactionWithGeneratedId[Task, K, T, C, D, N, Long]
  with ZioJdbcWithTransaction[K, T, C, D, N]
trait ZioJdbcRepository[K, T <: WithId[K], C <: ZioJdbcContext[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransaction[Task, K, T, C, D, N, Long]
  with ZioJdbcWithTransaction[K, T, C, D, N]

@SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
trait ZioJdbcWithTransaction[K, T <: WithId[K], C <: ZioJdbcContext[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadBaseWithTransaction[Task, K, T, C, D, N, Long] {

  import context.*

  override final def inTransaction[A](task: Task[A]): Task[A] =
    val qio  = fromTask(task)
    val qioR = context.transaction(qio)
    toTask(qioR)

  protected implicit def toTask[A](t: ZIO[DataSource, Throwable, A]): Task[A] =
    t.asInstanceOf[Task[A]]

  protected implicit def fromTask[A](t: Task[A]): QIO[A] =
    t.asInstanceOf[QIO[A]]

}
