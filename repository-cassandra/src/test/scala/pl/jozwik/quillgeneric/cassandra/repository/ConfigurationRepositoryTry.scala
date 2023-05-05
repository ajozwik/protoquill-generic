package pl.jozwik.quillgeneric.cassandra.repository

import cats.implicits.*

import scala.util.Try
import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.cassandra.{ CassandraContextWithDateQuotes, CassandraRepositoryTry }
import pl.jozwik.quillgeneric.model.{ Configuration, ConfigurationId }
import pl.jozwik.quillgeneric.monad.*

final class ConfigurationRepositoryTry[+Naming <: NamingStrategy, C <: CassandraContextWithDateQuotes[Naming]](
    protected val context: C
)(implicit
    meta: SchemaMeta[Configuration]
) extends CassandraRepositoryTry[ConfigurationId, Configuration, C, Naming] {

  import context.*

  protected def quoteQuery: Quoted[EntityQuery[Configuration]] = quote {
    query[Configuration]
  }

  protected inline def find(id: ConfigurationId): Quoted[EntityQuery[Configuration]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: Try[Seq[Configuration]] =
    Try(run(quoteQuery))

  override def create(entity: Configuration): Try[ConfigurationId] =
    for {
      _ <- Try(run(quoteQuery.insertValue(lift(entity))))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Configuration): Try[ConfigurationId] =
    for {
      el <- read(entity.id)
      id <- el match
        case None =>
          create(entity)
        case _ =>
          pure(entity.id)
    } yield {
      id
    }

  override def read(id: ConfigurationId): Try[Option[Configuration]] =
    for {
      seq <- Try(run(find(id)))
    } yield {
      seq.headOption
    }

  override def update(entity: Configuration): Try[Unit] =
    Try(run(find(entity.id).updateValue(lift(entity))))

  override def delete(id: ConfigurationId): Try[Unit] =
    Try(run(find(id).delete))

  override def deleteAll(): Try[Unit] =
    Try(run(quoteQuery.delete))

}
