package pl.jozwik.quillgeneric.model

import pl.jozwik.quillgeneric.DateTimeHelper

import java.time.{LocalDate, LocalDateTime}
import pl.jozwik.quillgeneric.repository.WithId

object PersonId {
  val empty: PersonId = PersonId(0)
}

final case class PersonId(value: Int) extends AnyVal

final case class Person(
    id: PersonId,
    firstName: String,
    lastName: String,
    birthDate: LocalDate,
    addressId: Option[AddressId] = Option(AddressId.empty),
    createDateTime: Option[LocalDateTime] = Option(DateTimeHelper.localDateTimeNow)
) extends WithId[PersonId]
