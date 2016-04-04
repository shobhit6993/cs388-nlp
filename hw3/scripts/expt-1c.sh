#!/bin/sh

# wsj seed, no self train, wsj test
wsj="/projects/nlp/penn-treebank3/parsed/mrg/wsj"
brown="/projects/nlp/penn-treebank3/parsed/mrg/brown"
frac=0.9
null="X"
log="../logs/1c"
for seedsize in 1000 2000 3000 4000 5000 7000 10000 13000 16000 20000 25000 30000 35000; do
    ./run.sh $wsj $null $wsj $frac $seedsize $null $log/$seedsize
done