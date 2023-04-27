package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.qzio.ZioJdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.model.{ Cell4d, Cell4dId }
import pl.jozwik.quillgeneric.zio.ZioJdbcRepository
import pl.jozwik.quillgeneric.zio.ZioJdbcRepository.*
import zio.interop.catz.*
final class Cell4dRepositoryJdbc[+D <: SqlIdiom, +N <: NamingStrategy, C <: ZioJdbcContextWithDataQuotes[D, N]](context: C)(implicit
    meta: SchemaMeta[Cell4d]
) extends Cell4dRepositoryQuill[D, N, C](context)

trait Cell4dRepositoryQuill[+D <: SqlIdiom, +N <: NamingStrategy, C <: ZioJdbcContextWithDataQuotes[D, N]](protected val context: C)(implicit
    meta: SchemaMeta[Cell4d]
) extends ZioJdbcRepository[Cell4dId, Cell4d, C, D, N] {

  import context.*
  protected def quoteQuery = quote {
    query[Cell4d]
  }

  protected inline def find(id: Cell4dId) = quote {
    quoteQuery.filter(_.id.fk1 == lift(id.x)).filter(_.id.fk2 == lift(id.y)).filter(_.id.fk3 == lift(id.z)).filter(_.id.fk4 == lift(id.t))
  }

  override def all: QIO[Seq[Cell4d]] =
    run(quoteQuery)

  override def create(entity: Cell4d): QIO[Cell4dId] =
    for {
      _ <- run(quoteQuery.insertValue(lift(entity)))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Cell4d): QIO[Cell4dId] =
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

  override def read(id: Cell4dId): QIO[Option[Cell4d]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Cell4d): QIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: Cell4dId): QIO[Long] =
    run(find(id).delete)

  override def deleteAll(): QIO[Long] =
    run(quoteQuery.delete)

}
