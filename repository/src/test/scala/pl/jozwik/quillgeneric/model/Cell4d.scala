package pl.jozwik.quillgeneric.model

import io.getquill.Embedded
import pl.jozwik.quillgeneric.repository.{ CompositeKey4, WithId }

final case class Cell4dId(fk1: Int, fk2: Int, fk3: Int, fk4: Long) extends CompositeKey4[Int, Int, Int, Long] {
  def x: Int = fk1

  def y: Int = fk2

  def z: Int = fk3

  def t: Long = fk4

}

final case class Cell4d(id: Cell4dId, occupied: Boolean) extends WithId[Cell4dId]
