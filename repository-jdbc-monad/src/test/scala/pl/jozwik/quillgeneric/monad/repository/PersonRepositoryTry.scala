package pl.jozwik.quillgeneric.monad.repository

import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.model.{ Person, PersonId }
import pl.jozwik.quillgeneric.monad.*
import scala.util.Try
import cats.implicits.*
final class PersonRepositoryTry[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: TryJdbcContextWithDateQuotes[Dialect, Naming]](
    protected val context: C
)(implicit
    meta: SchemaMeta[Person]
) extends TryJdbcRepositoryJdbcWithGeneratedId[PersonId, Person, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery = quote {
    query[Person]
  }

  protected inline def find(id: PersonId): Quoted[EntityQuery[Person]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: Try[Seq[Person]] =
    Try { run(quoteQuery) }

  override def create(entity: Person, generateId: Boolean = true): Try[PersonId] =
    Try {
      if (generateId) {
        run(quoteQuery.insertValue(lift(entity)).returningGenerated(_.id))
      } else {
        run(quoteQuery.insertValue(lift(entity)).returning(_.id))
      }
    }

  override def createOrUpdate(entity: Person, generateId: Boolean = true): Try[PersonId] = {
    inTransaction {
      for {
        el <- Try(run(find(entity.id).updateValue(lift(entity))))
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

  override def read(id: PersonId): Try[Option[Person]] =
    for {
      seq <- Try {
        run(find(id))
      }
    } yield {
      seq.headOption
    }

  override def update(entity: Person): Try[Long] =
    Try {
      run(find(entity.id).updateValue(lift(entity)))
    }

  override def delete(id: PersonId): Try[Long] =
    Try { run(find(id).delete) }

  override def deleteAll(): Try[Long] =
    Try { run(quoteQuery.delete) }

}
