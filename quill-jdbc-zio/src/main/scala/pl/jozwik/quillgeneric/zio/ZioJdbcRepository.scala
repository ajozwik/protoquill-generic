package pl.jozwik.quillgeneric.zio

import io.getquill.*
import io.getquill.context.jdbc.{ ObjectGenericTimeDecoders, ObjectGenericTimeEncoders }
import io.getquill.context.qzio.ZioJdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.monad.*
import pl.jozwik.quillgeneric.repository.*
import pl.jozwik.quillgeneric.zio.{ QIO, ZioJdbcContextWithDateQuotes }
import zio.ZIO

import javax.sql.DataSource

type QIO[+T] = ZIO[DataSource, Throwable, T]
type ZioJdbcContextWithDateQuotes[+Dialect <: SqlIdiom, +Naming <: NamingStrategy] = ZioJdbcContext[Dialect, Naming]
  with ObjectGenericTimeDecoders
  with ObjectGenericTimeEncoders

trait ZioJdbcRepositoryWithGeneratedId[K, T <: WithId[K], C <: ZioJdbcContext[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransactionWithGeneratedId[QIO, K, T, C, D, N, Long]
  with ZioJdbcWithTransaction[K, T, C, D, N]
trait ZioJdbcRepository[K, T <: WithId[K], C <: ZioJdbcContext[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransaction[QIO, K, T, C, D, N, Long]
  with ZioJdbcWithTransaction[K, T, C, D, N]

trait ZioJdbcWithTransaction[K, T <: WithId[K], C <: ZioJdbcContext[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadBaseWithTransaction[QIO, K, T, C, D, N, Long] {

  import context.*

  override final def inTransaction[A](task: QIO[A]): QIO[A] =
    context.transaction(task)

}
