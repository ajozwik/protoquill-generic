package pl.jozwik.quillgeneric.monad

import io.getquill.JdbcContextConfig
import io.getquill.util.LoadConfig
import pl.jozwik.quillgeneric.PoolHelper

import javax.sql.DataSource

object TryHelperSpec {
  private val cfg      = JdbcContextConfig(LoadConfig(PoolHelper.PoolName))
  val pool: DataSource = cfg.dataSource
}
