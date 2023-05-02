package pl.jozwik.quillgeneric.doobie.repository

import doobie.ConnectionIO
import io.getquill.{ doobie, * }
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.doobie.{ DoobieJdbcContextWithDateQuotes, DoobieJdbcRepository }
import pl.jozwik.quillgeneric.model.{ Sale, SaleId }

final class SaleRepositoryGen[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: DoobieJdbcContextWithDateQuotes[Dialect, Naming]](protected val context: C)(
    implicit meta: SchemaMeta[Sale]
) extends DoobieJdbcRepository[SaleId, Sale, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery = quote {
    query[Sale]
  }

  protected def find(id: SaleId): Quoted[EntityQuery[Sale]] = quote {
    quoteQuery.filter(_.id.fk1 == lift(id.fk1)).filter(_.id.fk2 == lift(id.fk2))
  }

  override def all: ConnectionIO[Seq[Sale]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Sale): ConnectionIO[SaleId] =
    for {
      _ <- run(quoteQuery.insertValue(lift(entity)))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Sale): ConnectionIO[SaleId] =
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

  override def read(id: SaleId): ConnectionIO[Option[Sale]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Sale): ConnectionIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: SaleId): ConnectionIO[Long] =
    run(find(id).delete)

  override def deleteAll(): ConnectionIO[Long] =
    run(quoteQuery.delete)

}
