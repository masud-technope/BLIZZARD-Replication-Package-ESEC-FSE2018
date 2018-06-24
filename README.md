
Improving IR-Based Bug Localization with Context-Aware Query Reformulation
=========================================================================================

Subject Systems (6):
--------------------
ecf (553)
eclipse.jdt.core (989)
eclipse.jdt.debug (557)
eclipse.jdt.ui (1,115)
eclipse.pde.ui (872)
tomcat70 (1,053)
------------------------
Total Bug reports: 5,139


Materials Included:
----------------------
**Baseline: (Best)**   
- Query: Baseline queries
- Results: Top-10 returned results by the baseline queries
- QE: Query Effectiveness of baseline queries.

**BLIZZARD: (Proposed)**
- Query: Reformulated queries by our technique.
- Results: Top-10 returned results by our queries.
- Result-Matched-Indices: Result indices matched with goldset
- QE: Query Effectiveness of our queries.

- BR-Exceptions: Exceptions extracted from BR-ST group of each subject system
- BR-ST-StackTraces: Stack traces extracted from BR-ST group of each subject system
- BR-Classes:	Bug report classes of each subject system-- BR-ST, BR-PE, BR-NL, BR-ALL.
- BR-Raw: Raw content from 5,139 bug reports from six systems
- Goldset: Change set for 5,139 reported bugs

- Corpus: Source code corpus for six subject systems where original file names are replaced with indices.
- Lucene-Index2File-Mapping: Corpus file index to original file name mapping
- Lucene-Index: Lucene index for code search

- blizzard.jar : Our proposed tool
- data: Data folder required for the tool
- models: Models required for the tool
- tbdata: Data folder required for the tool

- sample-input: Sample input directory
- sample-output: Sample output directory

- README
- LICENSE


Available Operations:
-----------------------
- Create reformulated query, i.e., reformulateQuery
- Collect/evaluate localization results, i.e., getResult


Required parameters for the operations:
======================================
- -task : expects a task to be performed
- -repo : expects the name of a system.
- -queryFile: file to store the reformulated queries  
- -bugIDFile: file containing the bug IDs
- -topk : expects the number of results to be analyzed.
- -resultFile: file to store the localization results


Q.1: How to install the BLIZZARD tool?
----------------------------------------------------
- Download all items from the Google drive, and keep in /home folder.
- Unzip all zip files, and make sure that they are in the home directory. For example, ecf in Goldset.zip should be /home/Goldset/ecf
- Run the tool from within the home directory.


Q.2: How to get reformulated queries for a system?
----------------------------------------------------

```java -jar blizzard-runner.jar -task reformulateQuery -repo ecf -bugIDFile ./sample-input/sample-bugs.txt -queryFile ./sample-input/sample-query.txt```


Query File format:
--------------------------
BugID1	Reformulated-query
BugID2	Reformulated-query
BugID3	Reformulated-query


Q.3: How to collect bug localization results?
----------------------------------------------
- Ennter the path to query file.
- Execute this command

```java -jar blizzard-runner.jar -task getResult -repo ecf  -queryFile ./sample-input/sample-query.txt -topk 10 -resultFile ./sample-output/sample-results.txt```

The above command collects Top-10 results, and also reports Hit@10, MRR@10, MAP@10  for the queries.


Q.4: How to get Query Effectiveness?
---------------------------------------------

You can set -topk to a big number like 100000 to get all the results, and then simply calculate the QE.


























