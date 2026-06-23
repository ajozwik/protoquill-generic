#!/bin/bash

sbt -Dquill.macro.log=false publishLocalSigned publishSigned sonaUpload sonaRelease
