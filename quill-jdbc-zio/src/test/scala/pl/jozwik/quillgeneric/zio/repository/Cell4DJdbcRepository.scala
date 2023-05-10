package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.qzio.ZioJdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.model.{ Cell4d, Cell4dId }
import pl.jozwik.quillgeneric.zio.*
import zio.interop.catz.*
import zio.Task
final class Cell4DJdbcRepository[+D <: SqlIdiom, +N <: NamingStrategy, C <: ZioJdbcContextWithDateQuotes[D, N]](protected val context: C)(implicit
    meta: SchemaMeta[Cell4d]
) extends ZioJdbcRepository[Cell4dId, Cell4d, C, D, N] {

  import context.*
  protected def quoteQuery = quote {
    query[Cell4d]
  }

  protected inline def find(id: Cell4dId) = quote {
    quoteQuery.filter(_.id.fk1 == lift(id.x)).filter(_.id.fk2 == lift(id.y)).filter(_.id.fk3 == lift(id.z)).filter(_.id.fk4 == lift(id.t))
  }

  override def all: Task[Seq[Cell4d]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Cell4d): Task[Cell4dId] =
    for {
      _ <- run(quoteQuery.insertValue(lift(entity)))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Cell4d): Task[Cell4dId] =
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

  override def read(id: Cell4dId): Task[Option[Cell4d]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Cell4d): Task[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: Cell4dId): Task[Long] =
    run(find(id).delete)

  override def deleteAll(): Task[Long] =
    run(quoteQuery.delete)

}
