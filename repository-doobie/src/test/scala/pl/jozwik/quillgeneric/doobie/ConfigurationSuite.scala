package pl.jozwik.quillgeneric.doobie

import io.getquill.*
import pl.jozwik.quillgeneric.model.{Configuration, ConfigurationId}
import pl.jozwik.quillgeneric.doobie.repository.ConfigurationRepository

trait ConfigurationSuite extends AbstractDoobieJdbcSpec {

  private val meta            = schemaMeta[Configuration]("CONFIGURATION", _.id -> "`CONFIGURATION_KEY`" , _.value -> "`CONFIGURATION_VALUE`")
  private lazy val repository = new ConfigurationRepository(ctx)(meta)

  "ConfigurationRepository " should {
    "All is empty" in {
      repository.all.runUnsafe() shouldBe empty
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
      task.runUnsafe()

      repository.createOrUpdateAndRead(configuration).runUnsafe() shouldBe configuration
    }
  }
}
