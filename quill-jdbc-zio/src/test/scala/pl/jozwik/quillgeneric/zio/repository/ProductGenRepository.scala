package pl.jozwik.quillgeneric.zio.repository

import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.context.qzio.ZioJdbcContext
import pl.jozwik.quillgeneric.model.{ Product, ProductId }
import pl.jozwik.quillgeneric.zio.*
import zio.interop.catz.*
import zio.Task
final class ProductGenRepository[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: ZioJdbcContextWithDateQuotes[Dialect, Naming]](protected val context: C)(
    implicit meta: SchemaMeta[Product]
) extends ZioJdbcRepositoryWithGeneratedId[ProductId, Product, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery = quote {
    query[Product]
  }

  protected inline def find(id: ProductId): Quoted[EntityQuery[Product]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: Task[Seq[Product]] =
    run(quoteQuery)

  override def create(entity: Product, generateId: Boolean = true): Task[ProductId] =
    if (generateId) {
      run(quoteQuery.insertValue(lift(entity)).returningGenerated(_.id))
    } else {
      run(quoteQuery.insertValue(lift(entity)).returning(_.id))
    }

  override def createOrUpdate(entity: Product, generateId: Boolean = true): Task[ProductId] = {
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
  }

  override def read(id: ProductId): Task[Option[Product]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Product): Task[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: ProductId): Task[Long] =
    run(find(id).delete)

  override def deleteAll(): Task[Long] =
    run(quoteQuery.delete)

}
