#!/bin/sh
# requires an integer argument which is the random-seed
# run HMM on atis
if [ $# -eq 0 ]
  then
    echo "No arguments supplied. Need an integer argument for random-seed"
else
    time java -cp "../class:../lib/mallet-deps.jar" \
        cc.mallet.fst.HMMSimpleTagger \
        --train true --model-file ../trained/HMM_atis \
        --training-proportion 0.8 \
        --random-seed $1 \
        --test lab ../data/atis/atis3.pos
fi