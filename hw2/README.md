Comparison of HMM and CRF for POS-tagging.
=============

The goal of this assignment is to compare the perfomance of HMM and CRF on a POS-tagging task. The problem statement can be found [here](https://www.cs.utexas.edu/~mooney/cs388/hw2.html). Mallet's implementation of HMM and CRF are used. The two datasets used are atis and wsj (from Penn Treebank). The results of various experiments can be found in ``report/assignment-2.pdf``

### Dependencies
Requires mallet-2.0.7, or mallet-2.0.8RC3, or above. Download from [here](http://mallet.cs.umass.edu/download.php).

For pedagogical reasons, the following instructions assume that the project (mallet) directory is /u/user/project/ (this directory should already have folders like bin, src).

### Description of files
``Parser.java``:
Contains code to perform pre-processing of atis and wsj. Copy it in /u/user/project/src/cc/mallet/fst

``TokenAccuracyEvaluator.java``:
A modified version of ``TokenAccuracyEvaluator.java`` that comes with mallet (in /u/user/project/src/cc/mallet/fst).
The file has been modified to account for OOV words, recording accuracy of HMM and CRF specifically on them.

``scripts folder``:
This directory contains scripts (including condor.sh) needed to run experiments.
Copy this folder in the project directory /u/user/project/

### How to compile?
After extracing mallet and replacing/adding the files mentioned above, cd to the project (mallet) directory and run
``ant``

### How to perform pre-processing?
For pre-processing data to convert it into the format accepted by mallet, cd to scripts folder (not doing this causes path issues in bash) and run ``parser.sh``. 
Note that you may have to appropriately change the path of source directory in the four java commands in ``parser.sh`` to the location where the original dataset resides.

The pre-processed files are written in ``data`` folder in the project directory. Additionally, the script merges the wsj data so that all files in a particular section (such as all files in sub-directory 05) are concatenated into a single file named ``wsj_section_05.pos``.

### How to run experiments?
Script for running each (standard) experiment is supplied in the ``scripts`` folder (which should be copied to the project directory). To run a script cd to ``scripts`` folder (not doing this causes path issues in bash) and run the script.

``wrapper.sh`` is a single script that can be run to perform all the (standard) experiments.

condor.sh is the condor script that runs wrapper.sh on condor. The output of **all** the experiments in the wrapper script are saved in the Output and/or Error files mentioned in the condor script.
