#!/bin/sh
# wrapper script to run all experiments
# comment out the ones not to be run

# HMM 10 runs on atis
for i in `seq 1 10`;
do
   ./hmm-atis.sh $i
done

# CRF 10 runs on atis
for i in `seq 1 10`;
do
  ./crf-atis.sh $i
done

# CRF 10 runs on atis-ortho
for i in `seq 1 10`;
do
  ./crf-atis-ortho.sh $i
done

# HMM 1 run on wsj
./hmm-wsj.sh

# CRF 1 run on wsj
./crf-wsj.sh

# CRF 1 run on wsj-ortho
./crf-wsj-ortho.sh