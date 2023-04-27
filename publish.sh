#!/bin/bash
# +publishLocalSigned
PATH=$HOME/bin:$PATH sbt8 -Dquill.macro.log=false clean +test +publishSigned +sonatypeRelease
