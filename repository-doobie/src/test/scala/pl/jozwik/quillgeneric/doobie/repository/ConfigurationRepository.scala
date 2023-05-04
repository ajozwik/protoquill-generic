package pl.jozwik.quillgeneric.doobie.repository

import doobie.ConnectionIO
import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.doobie.{ DoobieJdbcContextWithDateQuotes, DoobieRepository }
import pl.jozwik.quillgeneric.model.{ Configuration, ConfigurationId }

final class ConfigurationRepository[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: DoobieJdbcContextWithDateQuotes[Dialect, Naming]](
    protected val context: C
)(implicit
    meta: SchemaMeta[Configuration]
) extends DoobieRepository[ConfigurationId, Configuration, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery: Quoted[EntityQuery[Configuration]] = quote {
    query[Configuration]
  }

  protected inline def find(id: ConfigurationId): Quoted[EntityQuery[Configuration]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: ConnectionIO[Seq[Configuration]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Configuration): ConnectionIO[ConfigurationId] =
    for {
      _ <- run(quoteQuery.insertValue(lift(entity)))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Configuration): ConnectionIO[ConfigurationId] =
    inTransaction {
      for {
        el <- run(find(entity.id).updateValue(lift(entity)))
        id <- el match
          case 0 =>
            create(entity)
          case _ =>
            pure(entity.id)
      } yield {
        id
      }
    }

  override def read(id: ConfigurationId): ConnectionIO[Option[Configuration]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Configuration): ConnectionIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: ConfigurationId): ConnectionIO[Long] =
    run(find(id).delete)

  override def deleteAll(): ConnectionIO[Long] =
    run(quoteQuery.delete)

}
