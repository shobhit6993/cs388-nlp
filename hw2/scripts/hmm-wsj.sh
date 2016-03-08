#!/bin/sh
# run HMM on wsj
time java -cp "../class:../lib/mallet-deps.jar" \
    cc.mallet.fst.HMMSimpleTagger \
    --train true --model-file ../trained/HMM_wsj \
    --test lab ../data/wsj/wsj_section_00.pos ../data/wsj/wsj_section_01.pos