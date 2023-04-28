package pl.jozwik.quillgeneric.zio

import io.getquill.*
import pl.jozwik.quillgeneric.model.{ Configuration, ConfigurationId }
import pl.jozwik.quillgeneric.zio.repository.ConfigurationRepositoryJdbc

trait ConfigurationSuite extends AbstractZioJdbcSpec {

  private val meta            = schemaMeta[Configuration]("CONFIGURATION", _.id -> "`CONFIGURATION_KEY`" , _.value -> "`CONFIGURATION_VALUE`")
  private lazy val repository = new ConfigurationRepositoryJdbc(ctx)(meta)

  "ConfigurationRepository " should {
    "All is empty" in {
      repository.all.runSyncUnsafe() shouldBe empty
    }

    "Call all operations on Configuration" in {

      val configuration = Configuration(ConfigurationId("key"), "value")
      val task = repository
        .inTransaction {
          for {
            _      <- repository.create(configuration)
            actual <- repository.readUnsafe(configuration.id)
          } yield {
            actual shouldBe configuration
          }

        }
      task.runSyncUnsafe()

      repository.createOrUpdateAndRead(configuration).runSyncUnsafe() shouldBe configuration
    }
  }
}
