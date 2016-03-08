#!/bin/sh
# run CRF on wsj
time java -cp "../class:../lib/mallet-deps.jar" \
    cc.mallet.fst.SimpleTagger \
    --train true --model-file ../trained/CRF_wsj \
    --test lab ../data/wsj/wsj_section_00.pos ../data/wsj/wsj_section_01.pos