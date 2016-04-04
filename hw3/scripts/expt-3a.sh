#!/bin/sh

# brown seed, wsj self train, wsj test
wsj="/projects/nlp/penn-treebank3/parsed/mrg/wsj"
brown="/projects/nlp/penn-treebank3/parsed/mrg/brown"
frac=0.9
null="X"
log="../logs/3a"
for seedsize in 1000 2000 3000 4000 5000 7000 10000 13000 17000 21000; do
    ./run.sh $brown $wsj $wsj $frac $seedsize $null $log/$seedsize
done