package pl.jozwik.quillgeneric.doobie

import doobie.ConnectionIO
import io.getquill.*
import io.getquill.context.jdbc.{JdbcContextTypes, ObjectGenericTimeDecoders, ObjectGenericTimeEncoders}
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.doobie.DoobieContextBase
import pl.jozwik.quillgeneric.monad.{JdbcRepositoryMonad, JdbcRepositoryMonadWithGeneratedId, RepositoryMonadWithTransaction}
import pl.jozwik.quillgeneric.repository.*


type DoobieJdbcContextWithDateQuotes[+Dialect <: SqlIdiom, +Naming <: NamingStrategy] = DoobieContextBase[Dialect, Naming]
  with ObjectGenericTimeDecoders
  with ObjectGenericTimeEncoders

trait DoobieJdbcRepositoryWithTransactionWithGeneratedId[K, T <: WithId[K], C <: DoobieJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends JdbcRepositoryMonadWithGeneratedId[ConnectionIO, K, T, C, D, N, Long]
  with DoobieJdbcRepositoryBase[K, T, C, D, N]
trait DoobieJdbcRepository[K, T <: WithId[K], C <: DoobieJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends JdbcRepositoryMonad[ConnectionIO, K, T, C, D, N, Long]
  with DoobieJdbcRepositoryBase[K, T, C, D, N]

trait DoobieJdbcRepositoryBase[K, T <: WithId[K], C <: DoobieJdbcContextWithDateQuotes[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransaction[ConnectionIO, K, T, C, D, N, Long] {

  override final def inTransaction[A](task: ConnectionIO[A]): ConnectionIO[A] =
    task

}
