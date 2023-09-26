package pl.jozwik.quillgeneric.model

import pl.jozwik.quillgeneric.DateTimeHelper

import java.time.{Instant, LocalDate, LocalDateTime}
import pl.jozwik.quillgeneric.repository.WithId

final case class SaleId(fk1: ProductId, fk2: PersonId) {
  def productId: ProductId = fk1
  def personId: PersonId   = fk2
}

final case class Sale(id: SaleId, saleDate: Instant, saleDateTime: LocalDateTime = DateTimeHelper.localDateTimeNow, createDate: LocalDate = LocalDate.now())
  extends WithId[SaleId]
