#!/bin/sh
# run CRF on wsj with orthographic features
time java -cp "../class:../lib/mallet-deps.jar" \
    cc.mallet.fst.SimpleTagger \
    --train true --model-file ../trained/CRF_wsj_ortho \
    --test lab ../data/wsj_ortho/wsj_section_00.pos ../data/wsj_ortho/wsj_section_01.pos