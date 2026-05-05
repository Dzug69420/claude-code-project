#!/bin/sh
# Copyright 2015 the original author or authors. Apache 2.0 License.

app_path=$0
while [ -h "$app_path" ]; do
    ls=$(ls -ld "$app_path")
    link=${ls#*' -> '}
    case $link in
        /*) app_path=$link ;;
        *)  app_path=$(dirname "$app_path")/$link ;;
    esac
done
APP_HOME=$(cd "$(dirname "$app_path")" && pwd -P) || exit
APP_NAME="Gradle"
APP_BASE_NAME=${0##*/}
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ -n "$JAVA_HOME" ]; then
    JAVACMD=$JAVA_HOME/bin/java
else
    JAVACMD=java
fi

eval set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    "\"-Dorg.gradle.appname=$APP_BASE_NAME\"" \
    -classpath "\"$CLASSPATH\"" \
    org.gradle.wrapper.GradleWrapperMain "$@"

exec "$JAVACMD" "$@"
