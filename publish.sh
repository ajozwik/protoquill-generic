#!/bin/bash

#sbt -Dquill.macro.log=false clean test publishLocalSigned publishSigned sonaUpload sonaRelease

sbt -Dquill.macro.log=false publishLocalSigned publishSigned sonaUpload sonaRelease
