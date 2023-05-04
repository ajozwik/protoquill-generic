package pl.jozwik.quillgeneric.cassandra

import io.getquill.context.cassandra.{CassandraContext, CassandraStandardContext}
import io.getquill.NamingStrategy
import pl.jozwik.quillgeneric.repository.WithId

type CassandraContextWithDateQuotes[+Naming <: NamingStrategy] = CassandraStandardContext[Naming]
