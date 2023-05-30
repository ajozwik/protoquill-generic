#!/bin/bash

. $HOME/.sbt/.protoquill-generic-coverage
sbt8 -Dquill.macro.log=false clean coverage test coverageAggregate && sbt8 +coveralls || exit 1
PATH=$HOME/bin:$PATH sbt8 -Dquill.macro.log=false clean test publishLocalSigned publishSigned sonatypeRelease
