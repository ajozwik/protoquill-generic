package pl.jozwik.quillgeneric.doobie.repository

import doobie.ConnectionIO
import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.doobie.{ DoobieJdbcContextWithDateQuotes, DoobieRepositoryWithTransactionWithGeneratedId }
import pl.jozwik.quillgeneric.model.{ Person, PersonId }

final class PersonRepository[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: DoobieJdbcContextWithDateQuotes[Dialect, Naming]](
    protected val context: C
)(implicit
    meta: SchemaMeta[Person]
) extends DoobieRepositoryWithTransactionWithGeneratedId[PersonId, Person, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery = quote {
    query[Person]
  }

  protected inline def find(id: PersonId): Quoted[EntityQuery[Person]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: ConnectionIO[Seq[Person]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Person, generateId: Boolean = true): ConnectionIO[PersonId] =
    if (generateId) {
      run(quoteQuery.insertValue(lift(entity)).returningGenerated(_.id))
    } else {
      run(quoteQuery.insertValue(lift(entity)).returning(_.id))
    }

  override def createOrUpdate(entity: Person, generateId: Boolean = true): ConnectionIO[PersonId] = {
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

  override def read(id: PersonId): ConnectionIO[Option[Person]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Person): ConnectionIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: PersonId): ConnectionIO[Long] =
    run(find(id).delete)

  override def deleteAll(): ConnectionIO[Long] =
    run(quoteQuery.delete)

}
