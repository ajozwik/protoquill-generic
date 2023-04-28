package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.model.Configuration
import pl.jozwik.quillgeneric.model.ConfigurationId
import io.getquill.context.qzio.ZioJdbcContext
import pl.jozwik.quillgeneric.zio.*
import pl.jozwik.quillgeneric.zio.ZioJdbcRepository.*
import zio.interop.catz.*

final class ConfigurationRepositoryJdbc[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: ZioJdbcContextWithDateQuotes[Dialect, Naming]](
    protected val context: C
)(implicit
    meta: SchemaMeta[Configuration]
) extends ZioJdbcRepository[ConfigurationId, Configuration, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery = quote {
    query[Configuration]
  }

  protected inline def find(id: ConfigurationId): Quoted[EntityQuery[Configuration]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: QIO[Seq[Configuration]] =
    run(quoteQuery)

  override def create(entity: Configuration): QIO[ConfigurationId] =
    for {
      _ <- run(quoteQuery.insertValue(lift(entity)))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Configuration): QIO[ConfigurationId] =
    context.transaction {
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

  override def read(id: ConfigurationId): QIO[Option[Configuration]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Configuration): QIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: ConfigurationId): QIO[Long] =
    run(find(id).delete)

  override def deleteAll(): QIO[Long] =
    run(quoteQuery.delete)

}
