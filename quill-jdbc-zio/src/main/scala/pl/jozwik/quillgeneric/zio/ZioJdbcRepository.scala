package pl.jozwik.quillgeneric.zio

import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.*
import io.getquill.context.jdbc.{ JdbcContextTypes, ObjectGenericTimeDecoders, ObjectGenericTimeEncoders }
import io.getquill.context.qzio.ZioJdbcContext
import pl.jozwik.quillgeneric.repository.*
import pl.jozwik.quillgeneric.zio.ZioJdbcRepository.{ QIO, ZioJdbcContextWithDateQuotes }
import zio.ZIO

import javax.sql.DataSource
import pl.ds.quillgeneric.monad.{ BaseRepositoryMonadWithTransaction, JdbcRepositoryMonad, JdbcRepositoryMonadWithGeneratedId }
object ZioJdbcRepository {
  type QIO[T] = ZIO[DataSource, Throwable, T]
  type ZioJdbcContextWithDateQuotes[+Dialect <: SqlIdiom, +Naming <: NamingStrategy] = ZioJdbcContext[Dialect, Naming]
    with ObjectGenericTimeDecoders
    with ObjectGenericTimeEncoders
}

trait ZioJdbcRepositoryWithTransactionWithGeneratedId[K, T <: WithId[K], C <: ZioJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends JdbcRepositoryMonadWithGeneratedId[QIO, K, T, C, D, N, Long]
  with ZioJdbcRepositoryBase[K, T, C, D, N]
trait ZioJdbcRepository[K, T <: WithId[K], C <: ZioJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends JdbcRepositoryMonad[QIO, K, T, C, D, N, Long]
  with ZioJdbcRepositoryBase[K, T, C, D, N]

trait ZioJdbcRepositoryBase[K, T <: WithId[K], C <: ZioJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends BaseRepositoryMonadWithTransaction[QIO, K, T, C, D, N, Long] {

  import context.*

  override final def inTransaction[A](task: QIO[A]): QIO[A] =
    context.transaction(task)

}
