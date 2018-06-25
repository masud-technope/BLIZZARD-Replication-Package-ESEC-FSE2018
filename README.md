
Improving IR-Based Bug Localization with Context-Aware Query Reformulation
=========================================================================================

Accepted Paper at ESEC/FSE 2018
---------------------------------------
```
Improving IR-Based Bug Localization with Context-Aware Query Reformulation

Mohammad Masudur Rahman and Chanchal K. Roy
```

```
BLIZZARD automatically localizes buggy entities from project source using appropriate 
query reformulation and effective information retrieval. In particular, it determines whether 
there are excessive program entities or not in a bug report (query), 
and then applies appropriate reformulations to the query for bug localization.
```

[![DOI](https://zenodo.org/badge/138428994.svg)](https://zenodo.org/badge/latestdoi/138428994)

Subject Systems (6)
--------------------
 * ecf (553)
 * eclipse.jdt.core (989)
 * eclipse.jdt.debug (557)
 * eclipse.jdt.ui (1,115)
 * eclipse.pde.ui (872)
 * tomcat70 (1,053)

**Total Bug reports: 5,139**


Materials Included
----------------------
**Baseline Method**   
- ```Query:``` Baseline queries
- ```Results:``` Top-10 returned results by the baseline queries
- ```QE:``` Query Effectiveness of baseline queries.

**BLIZZARD-Proposed Method**
- ```Query:``` Reformulated queries by our technique.
- ```Results:``` Top-10 returned results by our queries.
- ```Result-Matched-Indices:``` Result indices matched with goldset
- ```QE:``` Query Effectiveness of our queries.

**Bug Report & Goldsets**
- ```BR-Exceptions:``` Exceptions extracted from BR-ST group of each subject system
- ```BR-ST-StackTraces:``` Stack traces extracted from BR-ST group of each subject system
- ```BR-Classes:``` Bug report classes of each subject system-- BR-ST, BR-PE, BR-NL, BR-ALL.
- ```BR-Raw:``` Raw content from 5,139 bug reports from six systems
- ```Goldset:``` Change set for 5,139 reported bugs

**System Corpora & Lucene Indices**
- ```Corpus:``` Source code corpus for six subject systems where original file names are replaced with indices.
- ```Lucene-Index2File-Mapping:``` Corpus file index to original file name mapping
- ```Lucene-Index:``` Lucene index for code search

**BLIZZARD Prototype & External Dependencies**
- ```blizzard-runner.jar:``` Our proposed tool
- ```data:``` Data folder required for the tool
- ```models:``` Models required for the tool
- ```tbdata:``` Data folder required for the tool

**Installing, Building and Execution**
- ```README```
- ```INSTALL```
- ```sample-input:``` Sample input directory
- ```sample-output:``` Sample output directory


**Licensing & Others**
- ```LICENSE```
- ```STATUS```


Available Operations
-----------------------
- Create reformulated query, i.e., ```reformulateQuery```
- Collect/evaluate localization results, i.e., ```getResult```


Required parameters for the operations
------------------------------------------
- **-task** : expects a task to be performed
- **-repo** : expects the name of a system.
- **-queryFile**: file to store the reformulated queries  
- **-bugIDFile**: file containing the bug IDs
- **-topk** : expects the number of results to be analyzed.
- **-resultFile**: file to store the localization results


Q.1: How to install the BLIZZARD tool?
----------------------------------------------------
- Download all items from GitHub using ```git clone``` command, and keep in **/home** folder.
- Unzip all zip files, and make sure that they are in the home directory. For example, ecf in Goldset.zip should be **/home/Goldset/ecf**
- Run the tool from within the home directory.


Q.2: How to get reformulated queries for a system?
----------------------------------------------------

```
java -jar blizzard-runner.jar -task reformulateQuery -repo ecf -bugIDFile ./sample-input/sample-bugs.txt -queryFile ./sample-input/sample-query.txt
```
Currently, the tool extracts raw bug reports from "BR-Raw" folder using the sample input Bug-IDs and then reformulates the reports.

Query File format:
--------------------------
BugID1	Reformulated-query

BugID2	Reformulated-query

BugID3	Reformulated-query

..........................................................


Q.3: How to collect bug localization results?
----------------------------------------------
- Ennter the path to query file.
- Execute this command

```
java -jar blizzard-runner.jar -task getResult -repo ecf  -queryFile ./sample-input/sample-query.txt -topk 10 -resultFile ./sample-output/sample-results.txt
```

The above command collects Top-10 results, and also reports Hit@10, MRR@10, MAP@10  for the queries.


DISCLAIMER: Currently, the tool provides system specific results. 
Hence, the performances reported at Table 5 (of the paper) can be found by averaging the results from all 6 subject systems.
The items provided in "BLIZZARD" folder can be directly used to verify the results reported in the paper. You have to choose appropriate bug IDs for that.
For example, if you want to verify results for BR_ST of ecf, (1) please use the bug-IDs only from "./BLIZZARD/Query/ecf/proposed-ST.txt" to geneate queries, and then (2) execute the queries.


Q.4: How to get Query Effectiveness?
---------------------------------------------

You can set **-topk** to a big number like **100000** to get all the results, and then simply calculate the QE.


Please cite our work as
------------------------------------------
```
@INPROCEEDINGS{fse2018masud, 
author={Rahman, M. M. and Roy, C. K.}, 
booktitle={Proc. ESEC/FSE}, 
title={Improving IR-Based Bug Localization with Context-Aware Query Reformulation}, 
year={2018}, 
pages={11} 
}
```


























