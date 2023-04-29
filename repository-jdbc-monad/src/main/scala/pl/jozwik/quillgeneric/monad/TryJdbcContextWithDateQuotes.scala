package pl.jozwik.quillgeneric.monad

import io.getquill.*
import io.getquill.context.jdbc.{ JdbcContext, ObjectGenericTimeDecoders, ObjectGenericTimeEncoders }
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.monad.{ JdbcRepositoryMonad, RepositoryMonadWithTransaction }
import pl.jozwik.quillgeneric.repository.WithId

import scala.util.Try

type TryJdbcContextWithDateQuotes[+Dialect <: SqlIdiom, +Naming <: NamingStrategy] = JdbcContext[Dialect, Naming]
  with ObjectGenericTimeDecoders
  with ObjectGenericTimeEncoders

trait TryJdbcRepositoryWithGeneratedId[K, T <: WithId[K], C <: TryJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends JdbcRepositoryMonadWithGeneratedId[Try, K, T, C, D, N, Long]
  with TryJdbcRepositoryBase[K, T, C, D, N]

trait TryJdbcRepository[K, T <: WithId[K], C <: TryJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends JdbcRepositoryMonad[Try, K, T, C, D, N, Long]
  with TryJdbcRepositoryBase[K, T, C, D, N]

trait TryJdbcRepositoryBase[K, T <: WithId[K], C <: TryJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransaction[Try, K, T, C, D, N, Long] {

  import context.*

  override final def inTransaction[A](task: Try[A]): Try[A] =
    context.transaction(task)

}
