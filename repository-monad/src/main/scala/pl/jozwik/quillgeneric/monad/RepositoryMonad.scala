package pl.jozwik.quillgeneric.monad

import cats.Monad
import cats.implicits.*
import io.getquill.{EntityQuery, NamingStrategy, Quoted}
import io.getquill.context.Context
import io.getquill.idiom.Idiom
import pl.jozwik.quillgeneric.repository.{BaseRepository, Repository, RepositoryWithGeneratedId, WithId}


trait RepositoryMonadWithGeneratedId[F[_]: Monad, K, T <: WithId[K], C <: Context[D, N], +D <: Idiom, +N <: NamingStrategy, UP]
  extends RepositoryWithGeneratedId[F, K, T, UP]
    with RepositoryMonadBase[F, K, T, C, D, N, UP]
trait RepositoryMonad[F[_]: Monad, K, T <: WithId[K], C <: Context[D, N], +D <: Idiom, +N <: NamingStrategy, UP]
  extends Repository[F, K, T, UP]
    with RepositoryMonadBase[F, K, T, C, D, N, UP]
trait RepositoryMonadBase[F[_]: Monad, K, T <: WithId[K], C <: Context[D, N], +D <: Idiom, +N <: NamingStrategy, UP] extends BaseRepository[F, K, T, UP] {
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

  protected final def pure[E](el: E): F[E] = MonadHelper.pure[F, E](el)
}

