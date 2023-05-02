package pl.jozwik.quillgeneric.monad

import cats.Monad
import cats.implicits.*
import io.getquill.context.Context
import io.getquill.{ EntityQuery, NamingStrategy, Quoted }
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.repository.{ BaseRepository, RepositoryWithTransaction, RepositoryWithTransactionWithGeneratedId, WithId, WithTransaction }

trait JdbcRepositoryMonadWithGeneratedId[F[_]: Monad, K, T <: WithId[K], C <: Context[D, N], +D <: SqlIdiom, +N <: NamingStrategy, UP]
  extends RepositoryWithTransactionWithGeneratedId[F, K, T, UP]
  with RepositoryMonadWithTransaction[F, K, T, C, D, N, UP] {

  override def createAndRead(entity: T, generateId: Boolean = true): F[T] =
    inTransaction {
      for {
        id <- create(entity, generateId)
        el <- readUnsafe(id)
      } yield {
        el
      }
    }

  override final def createOrUpdateAndRead(entity: T, generateId: Boolean = true): F[T] =
    inTransaction {
      for {
        id <- createOrUpdate(entity, generateId)
        el <- readUnsafe(id)
      } yield {
        el
      }
    }

}
trait JdbcRepositoryMonad[F[_]: Monad, K, T <: WithId[K], C <: Context[D, N], +D <: SqlIdiom, +N <: NamingStrategy, UP]
  extends RepositoryWithTransaction[F, K, T, UP]
  with RepositoryMonadWithTransaction[F, K, T, C, D, N, UP] {

  override final def createAndRead(entity: T): F[T] =
    inTransaction {
      for {
        id <- create(entity)
        el <- readUnsafe(id)
      } yield {
        el
      }
    }

  override final def createOrUpdateAndRead(entity: T): F[T] =
    inTransaction {
      for {
        id <- createOrUpdate(entity)
        el <- readUnsafe(id)
      } yield {
        el
      }
    }

}

trait RepositoryMonadWithTransaction[F[_]: Monad, K, T <: WithId[K], C <: Context[D, N], +D <: SqlIdiom, +N <: NamingStrategy, UP]
  extends BaseRepository[F, K, T, UP]
  with WithTransaction[F] {
  protected val context: C

  protected def quoteQuery: Quoted[EntityQuery[T]]

  protected def find(id: K): Quoted[EntityQuery[T]]

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  override final def readUnsafe(id: K): F[T] =
    for {
      opt <- read(id)
    } yield {
      opt.getOrElse(throw new NoSuchElementException(s"$id"))
    }

  override final def updateAndRead(entity: T): F[T] =
    inTransaction {
      for {
        _  <- update(entity)
        el <- readUnsafe(entity.id)
      } yield {
        el
      }
    }

  protected final def pure[E](el: E): F[E] = MonadHelper.pure[F, E](el)
}
