package pl.jozwik.quillgeneric.doobie.repository

import doobie.*
import io.getquill.*
import io.getquill.context.sql.idiom.SqlIdiom
import pl.jozwik.quillgeneric.doobie.{DoobieJdbcContextWithDateQuotes, DoobieRepositoryWithTransactionWithGeneratedId}
import pl.jozwik.quillgeneric.model.{Product, ProductId}

final class ProductRepository[+Dialect <: SqlIdiom, +Naming <: NamingStrategy, C <: DoobieJdbcContextWithDateQuotes[Dialect, Naming]](
    protected val context: C
)(implicit
    meta: SchemaMeta[Product]
) extends DoobieRepositoryWithTransactionWithGeneratedId[ProductId, Product, C, Dialect, Naming] {

  import context.*

  protected def quoteQuery = quote {
    query[Product]
  }

  protected inline def find(id: ProductId): Quoted[EntityQuery[Product]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: ConnectionIO[Seq[Product]] =
    for {
      all <- run(quoteQuery)
    } yield {
      all
    }

  override def create(entity: Product, generateId: Boolean = true): ConnectionIO[ProductId] =
    if (generateId) {
      run(quoteQuery.insertValue(lift(entity)).returningGenerated(_.id))
    } else {
      run(quoteQuery.insertValue(lift(entity)).returning(_.id))
    }

  override def createOrUpdate(entity: Product, generateId: Boolean = true): ConnectionIO[ProductId] = {
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

  override def read(id: ProductId): ConnectionIO[Option[Product]] =
    for {
      seq <- run(find(id))
    } yield {
      seq.headOption
    }

  override def update(entity: Product): ConnectionIO[Long] =
    run(find(entity.id).updateValue(lift(entity)))

  override def delete(id: ProductId): ConnectionIO[Long] =
    run(find(id).delete)

  override def deleteAll(): ConnectionIO[Long] =
    run(quoteQuery.delete)

}
