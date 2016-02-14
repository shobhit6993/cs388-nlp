#!/bin/sh

BIN=./bin
CLASSPATH=${BIN}
DATASET=/home/shobhit/code/dataset/pos-LDC-penn-treeback/

java -classpath ${BIN} hw1.lm.BigramModel ${DATASET}/atis/ 0.1