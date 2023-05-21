package pl.jozwik.quillgeneric.cassandra.sync

import org.scalatest.TryValues.*
import pl.jozwik.quillgeneric.cassandra.model.{ Address, AddressId }
import pl.jozwik.quillgeneric.cassandra.sync.repository.AddressRepository
import io.getquill.*
trait AddressSuite extends AbstractSyncSpec {

  private implicit val addressSchemaMeta: SchemaMeta[Address] = schemaMeta[Address]("Address")
  private lazy val repository                                 = new AddressRepository(ctx)

  "really simple transformation" should {

    "run sync" in {
      val id      = AddressId.random
      val address = createAddress(id)
      repository.create(address).runUnsafe()
      val add = repository.createAndRead(address.copy(localNumber = Option("F"))).runUnsafe()
      val upd = repository.createOrUpdateAndRead(add).runUnsafe()
      upd shouldBe add
      val v = repository.read(id).runUnsafe()
      v shouldBe Option(add)
      val vv = repository.readUnsafe(id).runUnsafe()
      vv shouldBe add
      repository.all.runUnsafe() shouldBe Seq(add)
      val updated = add.copy(buildingNumber = Option(System.currentTimeMillis().toString))
      repository.updateAndRead(updated).runUnsafe() shouldBe updated
      repository.deleteAll().runUnsafe()
      repository.all.runUnsafe() shouldBe Seq.empty
      intercept[NoSuchElementException] {
        repository.readUnsafe(id).runUnsafe()
      }

      intercept[NoSuchElementException] {
        repository.update(add).runUnsafe()
      }
      intercept[NoSuchElementException] {
        repository.updateAndRead(add).runUnsafe()
      }
    }

  }

}
