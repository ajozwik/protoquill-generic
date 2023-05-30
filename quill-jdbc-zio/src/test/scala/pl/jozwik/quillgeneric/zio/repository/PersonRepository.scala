package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.model.Person
import pl.jozwik.quillgeneric.model.PersonId
import pl.jozwik.quillgeneric.zio.*
import zio.interop.catz.*
import zio.Task
final class PersonRepository[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: ZioJdbcContextWithDateQuotes[Dialect, Naming]](protected val context: C)(
    implicit meta: SchemaMeta[Person]
) extends ZioJdbcRepositoryWithGeneratedId[PersonId, Person, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery = quote {
    query[Person]
  }

  protected inline def find(id: PersonId): Quoted[EntityQuery[Person]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: Task[Seq[Person]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Person, generateId: Boolean = true): Task[PersonId] =
    if (generateId) {
      run(quoteQuery.insertValue(lift(entity)).returningGenerated(_.id))
    } else {
      run(quoteQuery.insertValue(lift(entity)).returning(_.id))
    }

  override def createOrUpdate(entity: Person, generateId: Boolean = true): Task[PersonId] =
    inTransaction {
      toTask {
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

  override def read(id: PersonId): Task[Option[Person]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Person): Task[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: PersonId): Task[Long] =
    run(find(id).delete)

  override def deleteAll(): Task[Long] =
    run(quoteQuery.delete)

}
