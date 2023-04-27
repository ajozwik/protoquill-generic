package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.qzio.ZioJdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.model.{ Person, PersonId }
import pl.jozwik.quillgeneric.repository.RepositoryWithTransactionWithGeneratedId
import pl.jozwik.quillgeneric.zio.ZioJdbcRepository.*
import pl.jozwik.quillgeneric.zio.ZioJdbcRepositoryWithTransactionWithGeneratedId
import zio.interop.catz.*
final class PersonCustomRepositoryJdbc[+D <: SqlIdiom, +N <: NamingStrategy, C <: ZioJdbcContextWithDataQuotes[D, N]](context: C)(implicit
    meta: SchemaMeta[Person]
) extends PersonCustomRepositoryQuill[D, N, C](context)

trait PersonCustomRepositoryQuill[+D <: SqlIdiom, +N <: NamingStrategy, C <: ZioJdbcContextWithDataQuotes[D, N]](protected val context: C)(implicit
    meta: SchemaMeta[Person]
) extends ZioJdbcRepositoryWithTransactionWithGeneratedId[PersonId, Person, C, D, N] {

  import context.*

  protected def quoteQuery = quote {
    query[Person]
  }

  protected inline def find(id: PersonId) = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: QIO[Seq[Person]] = {
    run(quoteQuery)
  }

  override def create(entity: Person, generateId: Boolean = true): QIO[PersonId] =
    if (generateId) {
      run(quoteQuery.insertValue(lift(entity)).returningGenerated(_.id))
    } else {
      run(quoteQuery.insertValue(lift(entity)).returning(_.id))
    }

  override final def createOrUpdate(entity: Person, generateId: Boolean = true): QIO[PersonId] = {
    inTransaction {
      for {
        el <- run(find(entity.id).updateValue(lift(entity)))
        id <- el match
          case 0 =>
            create(entity, generateId)
          case _ =>
            pure(entity.id)
      } yield {
        id
      }
    }
  }

  override def read(id: PersonId): QIO[Option[Person]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Person): QIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: PersonId): QIO[Long] =
    run(find(id).delete)

  override def deleteAll(): QIO[Long] =
    run(quoteQuery.delete)

}
