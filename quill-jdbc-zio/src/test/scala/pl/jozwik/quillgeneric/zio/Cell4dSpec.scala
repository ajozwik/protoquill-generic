package pl.jozwik.quillgeneric.zio

import io.getquill.*
import pl.jozwik.quillgeneric.model.{ Cell4d, Cell4dId }
import pl.jozwik.quillgeneric.zio.repository.Cell4dRepositoryJdbc

import scala.util.Using

class Cell4dSpec extends AbstractZioJdbcSpec {

  private val meta       = schemaMeta[Cell4d]("CELL4D", _.id.fk1 -> "X", _.id.fk2 -> "Y", _.id.fk3 -> "Z", _.id.fk4 -> "T")
  private val repository = new Cell4dRepositoryJdbc(ctx)(meta)

  "Cell4dSuite " should {
    "Call crud operations " in {
      Using(ZioHelperSpec.pool.getConnection) { con =>
        val meta      = con.getSchema
        val tableType = Array("TABLE");
        logger.debug(s"$meta")
        val metaData = con.getMetaData
        val result   = metaData.getTables(null, meta, null, tableType)
        val builder  = new StringBuilder
        while (result.next) {
          val tableName = result.getString(3)
          builder.append(tableName).append("( ")
          val columns = metaData.getColumns(null, null, tableName, null)
          while (columns.next) {
            val columnName = columns.getString(4)
            builder.append(columnName)
            builder.append(",")
          }
          builder.deleteCharAt(builder.lastIndexOf(","))
          builder.append(" )")
          builder.append("\n")
          builder.append("----------------")
          builder.append("\n")
        }

        logger.debug(builder.toString)

      }.recover { th =>
        logger.debug("", th)
      }

      repository.all.runUnsafe() shouldBe empty
      val entity = Cell4d(Cell4dId(4, 1, 0, Integer.MAX_VALUE + 1L), false)
      repository.create(entity).runUnsafe() shouldBe entity.id
//      repository.createOrUpdate(entity).runUnsafe() shouldBe entity.id
//      repository.createOrUpdateAndRead(entity).runUnsafe() shouldBe entity
//      repository.deleteAll().runUnsafe() shouldBe 1
    }
  }
}
