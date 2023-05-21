# protoquill-generic support
Library of generic CRUD operation for [protoquill](https://github.com/zio/zio-protoquill) library. Only dynamic queries are supported.

It is used by:
https://github.com/ajozwik/sbt-protoquill-crud-generic/

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ajozwik/repository_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ajozwik/repository_3)
[![Scala CI](https://github.com/ajozwik/protoquill-generic/actions/workflows/scala.yml/badge.svg)](https://github.com/ajozwik/protoquill-generic/actions/workflows/scala.yml)


protoquill-generic is implementation for scala 3 project: [quill-generic](https://github.com/ajozwik/quill-generic). protoquill-generic does not use macro yet. 
It used basic implementation for CRUD operations.

Purpose of CRUD operations - [Repository](/repository/src/main/scala/pl/jozwik/quillgeneric/repository/Repository.scala) - where F is monad

```scala
trait WithTransaction[F[_]] {
  def inTransaction[A](task: F[A]): F[A]
}

trait RepositoryWithGeneratedId[F[_], K, T <: WithId[K], UP] extends BaseRepository[F, K, T, UP] {
  def create(entity: T, generatedId: Boolean = true): F[K]

  def createAndRead(entity: T, generatedId: Boolean = true): F[T]

  def createOrUpdate(entity: T, generatedId: Boolean = true): F[K]

  def createOrUpdateAndRead(entity: T, generatedId: Boolean = true): F[T]
}

trait Repository[F[_], K, T <: WithId[K], UP] extends BaseRepository[F, K, T, UP] {
  def create(entity: T): F[K]

  def createAndRead(entity: T): F[T]

  def createOrUpdate(entity: T): F[K]

  def createOrUpdateAndRead(entity: T): F[T]
}

trait BaseRepository[F[_], K, T <: WithId[K], UP] {

  def all: F[Seq[T]]

  def read(id: K): F[Option[T]]

  def readUnsafe(id: K): F[T]

  def update(t: T): F[UP]

  def updateAndRead(t: T): F[T]
  def delete(id: K): F[UP]

  def deleteAll(): F[UP]

}

trait RepositoryWithTransaction[F[_], K, T <: WithId[K], UP] extends Repository[F, K, T, UP] with WithTransaction[F]

trait RepositoryWithTransactionWithGeneratedId[F[_], K, T <: WithId[K], UP] extends RepositoryWithGeneratedId[F, K, T, UP] with WithTransaction[F]

```

Because protoquill-macro's are created in compile time - we need to know primary key. Case class for database entity has to have field id - the primary key [WithId](/repository/src/main/scala/pl/jozwik/quillgeneric/repository/WithId.scala)
If you have composite key you need to create case class like [Cell4dId](/repository/src/main/scala/pl/jozwik/quillgeneric/model/Cell4dId.scala):

For table
```sql
CREATE TABLE IF NOT EXISTS CELL4D (
    `X`  INT NOT NULL,
    `Y`  INT NOT NULL,
    `Z`  INT NOT NULL,
    `T`  INT NOT NULL,
    `OCCUPIED` BOOLEAN,
    PRIMARY KEY (`X`, `Y`, `Z`, `T`)
)
```
Compose key can look like:

```scala
final case class Cell4dId(fk1: Int, fk2: Int, fk3: Int, fk4: Long) {
  def x: Int = fk1

  def y: Int = fk2

  def z: Int = fk3

  def t: Long = fk4

}
```
N-column is represented by fkN - and add schema-mapping:
```scala
implicit val meta: SchemaMeta[Cell4d] = schemaMeta[Cell4d]("CELL4D", _.id.fk1 -> "X", _.id.fk2 -> "Y", _.id.fk3 -> "Z", _.id.fk4 -> "T")
```

For more details look at [sbt-protoquill-crud-generic](https://github.com/ajozwik/sbt-protoquill-crud-generic)
