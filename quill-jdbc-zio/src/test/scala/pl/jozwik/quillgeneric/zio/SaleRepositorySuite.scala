package pl.jozwik.quillgeneric.zio

import io.getquill.*
import pl.jozwik.quillgeneric.model.{ Person, PersonId, Product, ProductId, Sale, SaleId }
import pl.jozwik.quillgeneric.zio.repository.*

trait SaleRepositorySuite extends AbstractZioJdbcSpec {
  private implicit val saleSchema: SchemaMeta[Sale]     = schemaMeta[Sale]("SALE", _.id.fk1 -> "PRODUCT_ID", _.id.fk2 -> "PERSON_ID")
  private lazy val saleRepository                       = new SaleJdbcRepositoryGen(ctx)
  private implicit val personSchema: SchemaMeta[Person] = schemaMeta[Person]("PERSON2")
  private lazy val personRepository                     = new PersonRepository(ctx)
  private lazy val productRepository                    = new ProductGenRepository(ctx)(schemaMeta("PRODUCT"))
  "Sale Repository " should {
    "Call all operations on Sale" in {
      saleRepository.all.runUnsafe() shouldBe Seq()
      val personWithoutId  = Person(PersonId.empty, "firstName", "lastName", today)
      val person           = personRepository.createAndRead(personWithoutId).runUnsafe()
      val productWithoutId = Product(ProductId.empty, "productName")
      val product          = productRepository.createAndRead(productWithoutId).runUnsafe()
      val saleId           = SaleId(product.id, person.id)
      val sale             = Sale(saleId, now)
      saleRepository.createAndRead(sale).runUnsafe() shouldBe sale

      saleRepository.createOrUpdateAndRead(sale).runUnsafe() shouldBe sale

      saleRepository.read(saleId).runUnsafe() shouldBe Option(sale)
      saleRepository.readUnsafe(saleId).runUnsafe() shouldBe sale
      saleRepository.delete(saleId).runUnsafe() shouldBe 1
      productRepository.delete(product.id).runUnsafe() shouldBe 1
      personRepository.delete(person.id).runUnsafe() shouldBe 1
    }
  }

}
