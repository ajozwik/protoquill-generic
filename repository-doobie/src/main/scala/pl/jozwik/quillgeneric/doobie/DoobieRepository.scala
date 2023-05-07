package pl.jozwik.quillgeneric.doobie

import doobie.ConnectionIO
import io.getquill.*
import io.getquill.context.Context
import io.getquill.context.jdbc.{ ObjectGenericTimeDecoders, ObjectGenericTimeEncoders }
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.doobie.DoobieContextBase
import pl.jozwik.quillgeneric.monad.*
import pl.jozwik.quillgeneric.repository.*

type DoobieJdbcContextWithDateQuotes[+Dialect <: SqlIdiom, +Naming <: NamingStrategy] = DoobieContextBase[Dialect, Naming]
  with ObjectGenericTimeDecoders
  with ObjectGenericTimeEncoders

trait DoobieRepositoryWithTransactionWithGeneratedId[K, T <: WithId[K], C <: Context[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransactionWithGeneratedId[ConnectionIO, K, T, C, D, N, Long]
  with DoobieJdbcTransaction[K, T, C, D, N]
trait DoobieRepository[K, T <: WithId[K], C <: Context[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadWithTransaction[ConnectionIO, K, T, C, D, N, Long]
  with DoobieJdbcTransaction[K, T, C, D, N]

trait DoobieJdbcTransaction[K, T <: WithId[K], C <: Context[D, N], +D <: SqlIdiom, +N <: NamingStrategy]
  extends RepositoryMonadBaseWithTransaction[ConnectionIO, K, T, C, D, N, Long] {

  override final def inTransaction[A](task: ConnectionIO[A]): ConnectionIO[A] =
    task

}
