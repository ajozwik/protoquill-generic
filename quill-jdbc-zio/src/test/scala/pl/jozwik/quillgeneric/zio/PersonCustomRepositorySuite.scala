package pl.jozwik.quillgeneric.zio

import pl.jozwik.quillgeneric.model.{ Person, PersonId }
import pl.jozwik.quillgeneric.zio.repository.PersonCustomRepositoryJdbc
import zio.Unsafe
import io.getquill.*
trait PersonCustomRepositorySuite extends AbstractZioJdbcSpec {
  "PersonCustomRepository " should {
    "Call all operations on Person with auto generated id and custom field" in {
      implicit val meta: SchemaMeta[Person] = schemaMeta[Person]("Person3", columns => columns.birthDate -> "dob")
      val repository                        = new PersonCustomRepositoryJdbc(ctx)
      logger.debug("generated id with custom field")
      val person = Person(PersonId.empty, "firstName", "lastName", today)
      unsafe {
        repository.all
      } shouldBe empty
      val personId      = repository.create(person).runSyncUnsafe()
      val createdPatron = repository.read(personId).runSyncUnsafe().getOrElse(fail())
      val task = repository.inTransaction {
        for {
          u   <- repository.update(createdPatron)
          all <- repository.all
        } yield {
          (u, all)
        }
      }
      task.runSyncUnsafe() shouldBe ((1, Seq(createdPatron)))
      val newBirthDate = createdPatron.birthDate.minusYears(1)
      val modified     = createdPatron.copy(birthDate = newBirthDate)
      repository.update(modified).runSyncUnsafe() shouldBe 1
      repository.createOrUpdate(modified).runSyncUnsafe() shouldBe modified.id
      repository.createOrUpdateAndRead(modified).runSyncUnsafe() shouldBe modified
      repository.read(createdPatron.id).runSyncUnsafe().map(_.birthDate) shouldBe Option(newBirthDate)

      repository.delete(createdPatron.id).runSyncUnsafe() shouldBe 1
      repository.read(createdPatron.id).runSyncUnsafe() shouldBe empty
      repository.all.runSyncUnsafe() shouldBe empty

      repository.deleteAll().runSyncUnsafe() shouldBe 0

    }
  }
}
