package pl.jozwik.quillgeneric.cassandra.sync.repository

import pl.jozwik.quillgeneric.cassandra.model.{ Address, AddressId }
import pl.jozwik.quillgeneric.cassandra.*
import io.getquill.*
import scala.util.Try

final class AddressRepository[+Naming <: NamingStrategy, C <: CassandraContextWithDateQuotes[Naming]](protected val context: C)(implicit
    meta: SchemaMeta[Address]
) extends CassandraRepositoryTry[AddressId, Address, C, Naming] {

  import context.*

  protected def quoteQuery: Quoted[EntityQuery[Address]] = quote {
    query[Address]
  }

  protected inline def find(id: AddressId): Quoted[EntityQuery[Address]] = quote {
    quoteQuery.filter(_.id == lift(id))
  }

  override def all: Try[Seq[Address]] =
    Try(run(quoteQuery))

  override def create(entity: Address): Try[AddressId] =
    for {
      _ <- Try(run(quoteQuery.insertValue(lift(entity))))
    } yield {
      entity.id
    }

  override def read(id: AddressId): Try[Option[Address]] = {
    for {
      seq <- Try(run(find(id)))
    } yield {
      seq.headOption
    }
  }

  override def createOrUpdate(entity: Address): Try[AddressId] =
    for {
      el <- read(entity.id)
      id <- el match {
        case None =>
          create(entity)
        case _ =>
          pure(entity.id)
      }
    } yield {
      id
    }

  override def delete(id: AddressId): Try[Unit] = Try {
    run(find(id).delete)
  }

  override def deleteAll(): Try[Unit] =
    Try(run(quoteQuery.delete))

}
