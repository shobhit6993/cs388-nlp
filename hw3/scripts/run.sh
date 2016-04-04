#!/bin/sh

if [ $# -ne 7 ]
  then
    echo "Wrong arguments"
    exit;
fi

java -cp "../stanford-parser/stanford-parser.jar:../stanford-parser/slf4j-api.jar:../class/" domain_adapt.Interface \
     -seed $1 \
     -self $2 \
     -test $3 \
     -frac $4 \
     -seedsize $5 \
     -selfsize $6 > $7 2>&1


#java -cp "../stanford-parser/stanford-parser.jar:../stanford-parser/slf4j-api.jar:../class/" domain_adapt.Interface \
#     -seed /projects/nlp/penn-treebank3/parsed/mrg/wsj \
#     -self /projects/nlp/penn-treebank3/parsed/mrg/wsj \
#     -test /projects/nlp/penn-treebank3/parsed/mrg/brown \
#     -frac 0.9 \
#     -seedsize 35000 \
#     -selfsize 21000
