#!/bin/sh

BIN=./bin
CLASSPATH=${BIN}
#DATASET=../../dataset/pos-LDC-penn-treeback/
DATASET=/projects/nlp/penn-treebank3/tagged/pos/
echo "atis dataset\n"
echo "FORWARD"
java -classpath ${BIN} hw1.lm.BigramModel ${DATASET}/atis/ 0.1
echo "-------\n"
echo "BACKWARD"
echo "-------"
java -classpath ${BIN} hw1.lm.BackwardBigramModel ${DATASET}/atis/ 0.1
echo "-------\n"
echo "BIDIRECTIONAL"
echo "-------"
java -classpath ${BIN} hw1.lm.BidirectionalBigramModel ${DATASET}/atis/ 0.1
echo "\n\n##################################\n\n"

echo "wsj dataset\n"
echo "FORWARD"
java -classpath ${BIN} hw1.lm.BigramModel ${DATASET}/wsj/ 0.1
echo "-------\n"
echo "BACKWARD"
echo "-------"
java -classpath ${BIN} hw1.lm.BackwardBigramModel ${DATASET}/wsj/ 0.1
echo "-------\n"
echo "BIDIRECTIONAL"
echo "-------"
java -classpath ${BIN} hw1.lm.BidirectionalBigramModel ${DATASET}/wsj/ 0.1
echo "\n\n##################################\n\n"

echo "brown dataset\n"
echo "FORWARD"
java -classpath ${BIN} hw1.lm.BigramModel ${DATASET}/brown/ 0.1
echo "-------\n"
echo "BACKWARD"
echo "-------"
java -classpath ${BIN} hw1.lm.BackwardBigramModel ${DATASET}/brown/ 0.1
echo "-------\n"
echo "BIDIRECTIONAL"
echo "-------"
java -classpath ${BIN} hw1.lm.BidirectionalBigramModel ${DATASET}/brown/ 0.1
echo "\n\n##################################\n\n"


