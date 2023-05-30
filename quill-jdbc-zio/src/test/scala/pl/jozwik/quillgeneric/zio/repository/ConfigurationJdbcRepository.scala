package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.model.Configuration
import pl.jozwik.quillgeneric.model.ConfigurationId
import pl.jozwik.quillgeneric.zio.*
import zio.interop.catz.*
import zio.Task
final class ConfigurationJdbcRepository[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: ZioJdbcContextWithDateQuotes[Dialect, Naming]](
    protected val context: C
)(implicit
    meta: SchemaMeta[Configuration]
) extends ZioJdbcRepository[ConfigurationId, Configuration, C, Dialect, Naming] {

  import context.*

  protected inline def quoteQuery: Quoted[EntityQuery[Configuration]] = quote {
    query[Configuration]
  }

  protected inline def find(id: ConfigurationId): Quoted[EntityQuery[Configuration]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: Task[Seq[Configuration]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Configuration): Task[ConfigurationId] =
    for {
      _ <- run(quoteQuery.insertValue(lift(entity)))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Configuration): Task[ConfigurationId] =
    inTransaction {
      toTask {
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
    }

  override def read(id: ConfigurationId): Task[Option[Configuration]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Configuration): Task[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: ConfigurationId): Task[Long] =
    run(find(id).delete)

  override def deleteAll(): Task[Long] =
    run(quoteQuery.delete)

}
