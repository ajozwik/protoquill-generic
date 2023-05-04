package pl.jozwik.quillgeneric.zio

import pl.jozwik.quillgeneric.model.{ Person, PersonId }
import pl.jozwik.quillgeneric.zio.repository.PersonRepository
import zio.Unsafe
import io.getquill.*
trait PersonRepositorySuite extends AbstractZioJdbcSpec {
  "PersonCustomRepository " should {
    "Call all operations on Person with auto generated id and custom field" in {
      implicit val meta: SchemaMeta[Person] = schemaMeta[Person]("Person3", columns => columns.birthDate -> "dob")
      val repository                        = new PersonRepository(ctx)
      logger.debug("generated id with custom field")
      val person = Person(PersonId.empty, "firstName", "lastName", today)
      unsafe {
        repository.all
      } shouldBe empty
      val personId      = repository.create(person).runUnsafe()
      val createdPatron = repository.read(personId).runUnsafe().getOrElse(fail())
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
      repository.createOrUpdate(modified).runUnsafe() shouldBe modified.id
      repository.createOrUpdateAndRead(modified).runUnsafe() shouldBe modified
      repository.read(createdPatron.id).runUnsafe().map(_.birthDate) shouldBe Option(newBirthDate)

      repository.delete(createdPatron.id).runUnsafe() shouldBe 1
      repository.read(createdPatron.id).runUnsafe() shouldBe empty
      repository.all.runUnsafe() shouldBe empty

      repository.deleteAll().runUnsafe() shouldBe 0

    }
  }
}
