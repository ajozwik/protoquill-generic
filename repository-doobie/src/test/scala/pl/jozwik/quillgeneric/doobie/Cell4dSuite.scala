package pl.jozwik.quillgeneric.doobie

import io.getquill.*
import pl.jozwik.quillgeneric.model.{ Cell4d, Cell4dId }
import pl.jozwik.quillgeneric.doobie.repository.Cell4DRepository


trait Cell4dSuite extends AbstractDoobieJdbcSpec {

  private val meta       = schemaMeta[Cell4d]("CELL4D", _.id.fk1 -> "X", _.id.fk2 -> "Y", _.id.fk3 -> "Z", _.id.fk4 -> "T")
  private val repository = new Cell4DRepository(ctx)(meta)

  "Cell4dSuite " should {
    "Call crud operations " in {

      repository.all.runUnsafe() shouldBe empty
      val entity = Cell4d(Cell4dId(4, 1, 0, Integer.MAX_VALUE + 1L), false)
      repository.create(entity).runUnsafe() shouldBe entity.id
      repository.createOrUpdate(entity).runUnsafe() shouldBe entity.id
      repository.createOrUpdateAndRead(entity).runUnsafe() shouldBe entity
      repository.all.runUnsafe() should not be empty
      repository.deleteAll().runUnsafe() shouldBe 1
    }
  }
}
