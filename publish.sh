#!/bin/bash

PATH=$HOME/bin:$PATH sbt -Dquill.macro.log=false clean test publishLocalSigned publishSigned sonatypeRelease
