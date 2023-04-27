package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.qzio.ZioJdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.model.{ Configuration, ConfigurationId }
import pl.jozwik.quillgeneric.zio.ZioJdbcRepository
import pl.jozwik.quillgeneric.zio.ZioJdbcRepository.*
import zio.interop.catz.*
final class ConfigurationRepositoryJdbc[+D <: SqlIdiom, +N <: NamingStrategy, C <: ZioJdbcContextWithDataQuotes[D, N]](context: C)(implicit
    meta: SchemaMeta[Configuration]
) extends ConfigurationRepositoryQuill[D, N, C](context)
trait ConfigurationRepositoryQuill[+D <: SqlIdiom, +N <: NamingStrategy, C <: ZioJdbcContextWithDataQuotes[D, N]](protected val context: C)(implicit
    meta: SchemaMeta[Configuration]
) extends ZioJdbcRepository[ConfigurationId, Configuration, C, D, N] {

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
