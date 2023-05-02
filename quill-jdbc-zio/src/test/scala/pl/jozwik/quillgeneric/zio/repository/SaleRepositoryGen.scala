package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.context.qzio.ZioJdbcContext
import pl.jozwik.quillgeneric.model.{ Sale, SaleId }
import pl.jozwik.quillgeneric.zio.*
import zio.interop.catz.*

final class SaleRepositoryGen[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: ZioJdbcContextWithDateQuotes[Dialect, Naming]](protected val context: C)(
    implicit meta: SchemaMeta[Sale]
) extends ZioJdbcRepository[SaleId, Sale, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery = quote {
    query[Sale]
  }

  protected def find(id: SaleId): Quoted[EntityQuery[Sale]] = quote {
    quoteQuery.filter(_.id.fk1 == lift(id.fk1)).filter(_.id.fk2 == lift(id.fk2))
  }

  override def all: QIO[Seq[Sale]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Sale): QIO[SaleId] =
    for {
      _ <- run(quoteQuery.insertValue(lift(entity)))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Sale): QIO[SaleId] =
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

  override def read(id: SaleId): QIO[Option[Sale]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Sale): QIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: SaleId): QIO[Long] =
    run(find(id).delete)

  override def deleteAll(): QIO[Long] =
    run(quoteQuery.delete)

}
