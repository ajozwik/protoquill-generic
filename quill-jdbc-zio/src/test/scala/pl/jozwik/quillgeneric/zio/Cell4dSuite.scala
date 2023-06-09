package pl.jozwik.quillgeneric.zio

import io.getquill.*
import pl.jozwik.quillgeneric.model.{ Cell4d, Cell4dId }
import pl.jozwik.quillgeneric.zio.repository.Cell4DJdbcRepository


trait Cell4dSuite extends AbstractZioJdbcSpec {

  private implicit val meta: SchemaMeta[Cell4d] = schemaMeta[Cell4d]("CELL4D", _.id.fk1 -> "X", _.id.fk2 -> "Y", _.id.fk3 -> "Z", _.id.fk4 -> "T")
  private val repository                        = new Cell4DJdbcRepository(ctx)

  "Cell4dSuite " should {
    "Call crud operations " in {
      repository.all.runUnsafe() shouldBe empty
      val entity = Cell4d(Cell4dId(4, 1, 0, Integer.MAX_VALUE + 1L), false)
      repository.create(entity).runUnsafe() shouldBe entity.id
      repository.createOrUpdate(entity).runUnsafe() shouldBe entity.id
      repository.createOrUpdateAndRead(entity).runUnsafe() shouldBe entity
      repository.deleteAll().runUnsafe() shouldBe 1
    }
  }
}
