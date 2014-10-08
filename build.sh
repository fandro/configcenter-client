#!/bin/bash
JAVA_HOME=$JAVA_HOME_1_5
export JAVA_HOME
PATH=$JAVA_HOME/bin:$PATH
export PATH
#ant

mvn -U -DskipTests clean  dependency:copy-dependencies source:jar deploy 

