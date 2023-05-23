package pl.jozwik.quillgeneric.monad

import io.getquill.*
import pl.jozwik.quillgeneric.model.{ Configuration, ConfigurationId }
import pl.jozwik.quillgeneric.monad.repository.ConfigurationRepositoryTry

trait ConfigurationSuite extends AbstractTryJdbcSpec {

  private implicit val meta: SchemaMeta[Configuration] =
    schemaMeta[Configuration]("CONFIGURATION", _.id -> "`CONFIGURATION_KEY`", _.value -> "`CONFIGURATION_VALUE`")
  private lazy val repository = new ConfigurationRepositoryTry(ctx)

  "ConfigurationRepository " should {
    "All is empty" in {
      repository.all.runUnsafe() shouldBe empty
    }

    "Call all operations on Configuration" in {

      val configuration = Configuration(ConfigurationId("key"), "value")
      val task = repository
        .inTransaction {
          for {
            id     <- repository.create(configuration)
            actual <- repository.readUnsafe(id)
          } yield {
            actual
          }

        }
      task.runUnsafe() shouldBe configuration

      repository.createOrUpdateAndRead(configuration).runUnsafe() shouldBe configuration
    }
  }
}
