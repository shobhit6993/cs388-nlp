#!/bin/sh

# create the required directory structure
echo "Will delete 'data' folder in the project directory (if it exists) and create a new folder with pre-processed form of atis and wsj dataset"
read -p "Continue[y/n]? " reply
case "$reply" 
in y)
    bash -c '[ -d ../data ] && rm -r ../data'
    mkdir -p ../data
    mkdir -p ../data/atis
    mkdir -p ../data/atis_ortho
    mkdir -p ../data/wsj
    mkdir -p ../data/wsj_ortho

    for i in $(seq -w 00 24);
    do
        mkdir -p ../data/wsj/$i
    done

    for i in $(seq -w 00 24);
    do
        mkdir -p ../data/wsj_ortho/$i
    done

    # The first argument must either [ortho] or [no-ortho], depending upon whether or not the orthographic features (such as suffix, caps, etc.) of tokens should be included. The second argument is the path of the pos file, or the directory containing a set of pos files.
    java -cp "../class" cc.mallet.fst.Parser no-ortho /home/shobhit/code/dataset/pos-LDC-penn-treeback/atis/atis3.pos
    java -cp "../class" cc.mallet.fst.Parser no-ortho /home/shobhit/code/dataset/pos-LDC-penn-treeback/wsj/

    java -cp "../class" cc.mallet.fst.Parser ortho /home/shobhit/code/dataset/pos-LDC-penn-treeback/atis/atis3.pos
    java -cp "../class" cc.mallet.fst.Parser ortho /home/shobhit/code/dataset/pos-LDC-penn-treeback/wsj/

    # merge wsj sections
    for f in "../data/wsj"/*; do
        [ -d "${f}" ] || continue # if not a directory, skip
        # echo $f
        x="$(echo $f | cut -d'/' -f 4)"
        new_f=wsj_section_$x
        cat $f/* > ../data/wsj/$new_f.pos
        echo Written ../data/wsj/$new_f.pos
    done

    # merge wsj_ortho sections
    for f in "../data/wsj_ortho"/*; do
        [ -d "${f}" ] || continue # if not a directory, skip
        # echo $f
        x="$(echo $f | cut -d'/' -f 4)"
        new_f=wsj_section_$x
        cat $f/* > ../data/wsj_ortho/$new_f.pos
        echo Written ../data/wsj_ortho/$new_f.pos
    done
    ;;
*) 
    echo "Exiting..." 
    exit;;
esac