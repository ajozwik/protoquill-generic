package pl.jozwik.quillgeneric.monad

import io.getquill.*
import io.getquill.context.jdbc.{ JdbcContext, ObjectGenericTimeDecoders, ObjectGenericTimeEncoders }
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.monad.{ RepositoryMonad, RepositoryMonadWithTransaction }
import pl.jozwik.quillgeneric.repository.{ WithId, WithTransaction }

import scala.util.Try

type TryJdbcContextWithDateQuotes[+Dialect <: SqlIdiom, +Naming <: NamingStrategy] = JdbcContext[Dialect, Naming]
  with ObjectGenericTimeDecoders
  with ObjectGenericTimeEncoders

trait TryRepositoryWithGeneratedId[K, T <: WithId[K], C <: TryJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransactionWithGeneratedId[Try, K, T, C, D, N, Long]
  with TryWithTransaction[C, D, N]

trait TryRepository[K, T <: WithId[K], C <: TryJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransaction[Try, K, T, C, D, N, Long]
  with TryWithTransaction[C, D, N]

trait TryWithTransaction[C <: TryJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy] extends WithTransaction[Try] {

  protected val context: C

  override final def inTransaction[A](task: Try[A]): Try[A] =
    context.transaction(task)
}
