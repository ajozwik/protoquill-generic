package pl.jozwik.quillgeneric.cassandra

import cats.Monad
import cats.implicits.*
import io.getquill.context.cassandra.{ CassandraStandardContext, CqlIdiom }
import io.getquill.{ CassandraSyncContext, NamingStrategy }
import io.getquill.context.cassandra.encoding.{ Decoders, Encoders }
import pl.jozwik.quillgeneric.monad.RepositoryMonad
import pl.jozwik.quillgeneric.repository.WithId

import scala.util.Try

type CassandraContextWithDateQuotes[+Naming <: NamingStrategy] = CassandraSyncContext[Naming] with Decoders with Encoders
trait CassandraRepositoryTry[K, T <: WithId[K], C <: CassandraStandardContext[N], +N <: NamingStrategy] extends CassandraRepositoryMonad[Try, K, T, C, N]

trait CassandraRepositoryMonad[F[_]: Monad, K, T <: WithId[K], C <: CassandraStandardContext[N], +N <: NamingStrategy]
  extends RepositoryMonad[F, K, T, C, CqlIdiom, N, Unit] {

  override final def createAndRead(entity: T): F[T] =
    for {
      id <- create(entity)
      el <- readUnsafe(id)
    } yield {
      el
    }

  override final def createOrUpdateAndRead(entity: T): F[T] =
    for {
      id <- createOrUpdate(entity)
      el <- readUnsafe(id)
    } yield {
      el
    }

  override final def updateAndRead(entity: T): F[T] =
    for {
      _  <- update(entity)
      el <- readUnsafe(entity.id)
    } yield {
      el
    }

  override final def update(t: T): F[Unit] =
    for {
      _ <- readUnsafe(t.id)
      _ <- create(t)
    } yield {}

}
