set java_home=%JAVA_HOME_1_5%
set path=%JAVA_HOME_1_5%\bin;%path%
rem maven

rem call mvn clean
call mvn -U clean -DskipTests dependency:copy-dependencies source:jar install


 