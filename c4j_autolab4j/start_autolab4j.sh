#!/bin/sh

echo off
clear
BASEDIR=$(dirname $0)
cd "$BASEDIR"

file="./.install4j/jre.bundle/Contents/Home/bin/java"
if [ -f "$file" ]
then
	./.install4j/jre.bundle/Contents/Home/bin/java -classpath autolab4j.jar:./lib/devonly/* com.commander4j.autolab.StartStop
else
	java -classpath autolab4j.jar:./lib/devonly/* com.commander4j.autolab.StartStop
fi

exit