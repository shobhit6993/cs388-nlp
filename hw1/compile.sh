#!/bin/sh

SRC=./src
BIN=./bin
# CLASSPATH=./src
PROGRAM_NAME=$(find ${SRC}/* | grep .java)

# cd src
# for i in `ls ../lib/*.jar`
#   do
#   THE_CLASSPATH=${THE_CLASSPATH}:${i}
# done

if [ -d ${BIN} ]; then
    rm -rf ${BIN}/*; 
else
    mkdir ${BIN}
fi

javac -d ${BIN} $PROGRAM_NAME

if [ $? -eq 0 ]; then
    echo "compile worked!"
fi
