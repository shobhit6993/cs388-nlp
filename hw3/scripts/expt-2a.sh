#!/bin/sh

# wsj seed, brown self train, brown test, seedsize=10000
wsj="/projects/nlp/penn-treebank3/parsed/mrg/wsj"
brown="/projects/nlp/penn-treebank3/parsed/mrg/brown"
frac=0.9
null="X"
log="../logs/2a"
for selfsize in 1000 2000 3000 4000 5000 7000 10000 13000 17000 21000; do
    ./run.sh $wsj $brown $brown $frac 10000 $selfsize $log/$selfsize
done