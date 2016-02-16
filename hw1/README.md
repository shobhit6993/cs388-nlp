Bigram Language Models
========
Problem statement can be found [here](https://www.cs.utexas.edu/~mooney/cs388/hw1.html).

#### How to compile?
Execute ``compile.sh`` script to compile all the java files.

### How to run?
Executing ``run.sh`` script would run the three models on the three datasets, producing output similar to the trace file (with a slightly more readable formatting). 

The script uses the path ``/projects/nlp/penn-treebank3/tagged/pos/atis/`` (and corresponding paths for brown and wsj dataset) for the datasets. As long as the code is being tested on UTCS machines, this should be work. Should the code be required to be tested on a different machine, the ``DATASET`` variable on Line. 5 in the script should be appropriately set.

To run a model (say, the BackwardBigram model)  manually, use the following command in the project directory:
``java -cp ./bin hw1.lm.BackwardBigramModel /path/to/dataset/ 0.1``

### Directory structure
- scr/lm directory in the project directory contains the source code.
- bin directory (created after executing ``compile.sh``) contains the java binaries.
- report directory contains the report in pdf format.
- trace directory contains three trace file (one for each model).

### Description of files
- `BigramModel.java` - Implements the forward bigram model.
- `BackwardBigramModel.java` - Implements the backward bigram model.
- `BidirectionalBigramModel.java` - Implements the bidirectional bigram model.
- `DoubleValue.java` - A wrapper class for storing and manipulating doubles.
- `POSTaggedFile.java` - Code to strip POS tags from the LDC Penn Treebank dataset.
- `compile.sh` - Script to compile the code.
- `run.sh` - Script to run all the three models on the three datasets.
