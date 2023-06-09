package pl.jozwik.quillgeneric.monad

import io.getquill.*
import pl.jozwik.quillgeneric.model.{ Person, PersonId }
import pl.jozwik.quillgeneric.monad.repository.PersonRepositoryTry

import java.sql.SQLException

trait PersonRepositorySuite extends AbstractTryJdbcSpec {
  "PersonCustomRepository " should {
    "Call all operations on Person with auto generated id and custom field" in {
      implicit val meta: SchemaMeta[Person] = schemaMeta[Person]("Person3", columns => columns.birthDate -> "dob")
      val repository                        = new PersonRepositoryTry(ctx)
      logger.debug("generated id with custom field")
      val person = Person(PersonId.empty, "firstName", "lastName", today)
      repository.all.runUnsafe() shouldBe empty
      val personId      = repository.create(person).runUnsafe()
      val createdPatron = repository.read(personId).runUnsafe().getOrElse(fail())
      intercept[SQLException] {
        repository.createAndRead(createdPatron, false).runUnsafe()
      }
      val task = repository.inTransaction {
        for {
          u   <- repository.update(createdPatron)
          all <- repository.all
        } yield {
          (u, all)
        }
      }
      task.runUnsafe() shouldBe ((1, Seq(createdPatron)))
      val newBirthDate = createdPatron.birthDate.minusYears(1)
      val modified     = createdPatron.copy(birthDate = newBirthDate)
      repository.update(modified).runUnsafe() shouldBe 1
      repository.updateAndRead(modified).runUnsafe() shouldBe modified
      repository.createOrUpdate(modified).runUnsafe() shouldBe modified.id
      repository.createOrUpdateAndRead(modified).runUnsafe() shouldBe modified
      repository.read(createdPatron.id).runUnsafe().map(_.birthDate) shouldBe Option(newBirthDate)

      repository.delete(createdPatron.id).runUnsafe() shouldBe 1
      repository.read(createdPatron.id).runUnsafe() shouldBe empty
      intercept[NoSuchElementException](repository.readUnsafe(createdPatron.id).runUnsafe())
      repository.all.runUnsafe() shouldBe empty

      repository.deleteAll().runUnsafe() shouldBe 0

    }
  }
}
