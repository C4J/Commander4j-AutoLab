echo off
cls

if exist jre\bin\java.exe goto jre_found

goto end

:jre_found

rem Use Bundled JRE
SET JAVA_HOME=.\jre\bin
SET PATH=%PATH%;.\jre\bin

:end

	java -classpath autolab4j.jar:./lib/devonly/* com.commander4j.autolab.StartStop

exit