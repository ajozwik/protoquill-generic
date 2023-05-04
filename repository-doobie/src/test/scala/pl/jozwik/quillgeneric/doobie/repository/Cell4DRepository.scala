package pl.jozwik.quillgeneric.doobie.repository

import doobie.ConnectionIO
import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.doobie.{ DoobieJdbcContextWithDateQuotes, DoobieRepository }
import pl.jozwik.quillgeneric.model.{ Cell4d, Cell4dId }

final class Cell4DRepository[+D <: SqlIdiom, +N <: NamingStrategy, C <: DoobieJdbcContextWithDateQuotes[D, N]](protected val context: C)(implicit
                                                                                                                                         meta: SchemaMeta[Cell4d]
) extends DoobieRepository[Cell4dId, Cell4d, C, D, N] {

  import context.*
  protected def quoteQuery = quote {
    query[Cell4d]
  }

  protected inline def find(id: Cell4dId) = quote {
    quoteQuery.filter(_.id.fk1 == lift(id.x)).filter(_.id.fk2 == lift(id.y)).filter(_.id.fk3 == lift(id.z)).filter(_.id.fk4 == lift(id.t))
  }

  override def all: ConnectionIO[Seq[Cell4d]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Cell4d): ConnectionIO[Cell4dId] =
    for {
      _ <- run(quoteQuery.insertValue(lift(entity)))
    } yield {
      entity.id
    }

  override def createOrUpdate(entity: Cell4d): ConnectionIO[Cell4dId] =
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

  override def read(id: Cell4dId): ConnectionIO[Option[Cell4d]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Cell4d): ConnectionIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: Cell4dId): ConnectionIO[Long] =
    run(find(id).delete)

  override def deleteAll(): ConnectionIO[Long] =
    run(quoteQuery.delete)

}
