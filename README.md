
Improving IR-Based Bug Localization with Context-Aware Query Reformulation
=========================================================================================

Accepted Papers at ESEC/FSE 2018 and ICSE 2018 (Poster)
-----------------------------------------------------------
```
Improving IR-Based Bug Localization with Context-Aware Query Reformulation

Mohammad Masudur Rahman and Chanchal K. Roy
```
**Download this paper:**  [<img src="https://web.cs.dal.ca/~masud/img/pdf.png"
     alt="PDF" heigh="16px" width="16px" />](https://web.cs.dal.ca/~masud/papers/masud-ESECFSE2018.pdf)
     
```
Poster: Improving Bug Localization with Report Quality Dynamics and Query Reformulation
Mohammad Masudur Rahman and Chanchal K. Roy
```
**Download this paper** [<img src="https://web.cs.dal.ca/~masud/img/pdf.png"
     alt="PDF" heigh="16px" width="16px" />](https://web.cs.dal.ca/~masud/papers/masud-ICSE2018.pdf)  


**Abstract:** Recent findings suggest that Information Retrieval (IR)-based bug localization techniques do not perform well if the bug report lacks rich structured information (e.g., relevant program entity names). Conversely, excessive structured information (e.g., stack traces) in the bug report might not always help the automated localization either. In this paper, we propose a novel technique--BLIZZARD-- that automatically localizes buggy entities from project source using appropriate query reformulation and effective information retrieval. In particular, our technique determines whether there are excessive program entities or not in a bug report (query), and then applies appropriate reformulations to the query for bug localization. Experiments using 5,139 bug reports show that our technique can localize the buggy source documents with 7%--56% higher Hit@10, 6%--62% higher MAP@10 and 6%--62% higher MRR@10 than the baseline technique. Comparison with the state-of-the-art techniques and their variants report that our technique can improve 19% in MAP@10 and 20% in MRR@10 over the state-of-the-art, and can impro 59% of the noisy queries and 39% of the poor queries.


[![DOI](https://zenodo.org/badge/138428994.svg)](https://zenodo.org/badge/latestdoi/138428994)

![Functional](https://www.acm.org/binaries/content/gallery/acm/publications/replication-badges/artifacts_evaluated_functional_dl.jpg) ![Reusable](https://www.acm.org/binaries/content/gallery/acm/publications/replication-badges/artifacts_evaluated_reusable_dl.jpg) ![Available](https://www.acm.org/binaries/content/gallery/acm/publications/replication-badges/artifacts_available_dl.jpg)


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
- **```SOURCE-CODE```** [**BLIZZARD-1.0.0**](https://github.com/masud-technope/BLIZZARD) and [**BLIZZARD-2.0.0**](https://github.com/masud-technope/BLIZZARD-Tool-New). Go ahead and extend.
- ```data:``` Data folder required for the tool
- ```models:``` Models required for the tool
- ```tbdata:``` Data folder required for the tool

**Installing, Building and Execution**
- ```README:``` Tool overview, artifact details and required commands for the tool's execution.
- ```INSTALL:``` System requirements and installation details
- ```sample-input:``` Sample input directory
- ```sample-output:``` Sample output directory

**Licensing & Others**
- ```LICENSE:``` Our artifacts are under MIT license
- ```STATUS:```  It shows our requested badge and the rationale.
- ```CITATION:```It shows how this work should be cited.
- ```Screenshots:``` It contains the screenshots of the available operations. 


Available Operations
-----------------------
- Create reformulated query, i.e., ```reformulateQuery```
- Collect/evaluate localization results, i.e., ```getResult```
- Show replicated bug localization performances from raw data, i.e., ```getReportedBLPerformance```
- Show replicated query effectiveness performances from raw data, i.e., ```getReportedQEPerformance```


Required parameters for the operations
------------------------------------------
- **-task** : expects a task to be performed
- **-repo** : expects the name of a system.
- **-queryFile**: file to store the reformulated queries  
- **-bugIDFile**: file containing the bug IDs
- **-topk** : expects the number of results to be analyzed.
- **-resultFile**: file to store the localization results
- **-reportKey**: expects one of the three bug report classes (e.g., ST, PE, NL)


Q.1: How to install the BLIZZARD tool?
----------------------------------------------------
- Download all items from GitHub using ```git clone``` command, and keep in ```/home``` folder.
- Unzip all zip files, and make sure that they are in the home directory. For example, ecf in Goldset.zip should be ```/home/Goldset/ecf```
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


Q.3: How to collect Top-K bug localization results?
------------------------------------------------------------

```
java -jar blizzard-runner.jar -task getResult -repo ecf  -queryFile ./sample-input/sample-query.txt -topk 10 -resultFile ./sample-output/sample-results.txt
```

The above command collects Top-10 results and reports Hit@10, MRR@10, MAP@10  for the queries.

If you want to extract all the results rather than Top-K only, you can set -topk to a big number, **100000** to get all the results. 
This provides the ranking of all source code files for each given query. 

**DISCLAIMER**: Currently, the tool provides system specific results. 
Hence, the performances reported at Table 5 (of the paper) can be found by averaging the results from all 6 subject systems.


Q.4: How to determine Query Effectiveness (QE) performances?
---------------------------------------------------------------

You can set **-topk** to a big number, **100000** to get all the results. This provides the ranking of all source code files for each query
which can be then used to determine the Query Effectiveness (QE).


Q.5: How to replicate the bug localization performances reported in the paper?
---------------------------------------------------------------------------------

```
java -jar blizzard-runner.jar -task getReportedBLPerformance  -topk 10 -reportKey ST
```

This command shows Hit@10, MRR@10, MAP@10 for all 6 subject systems, and their mean measures (as shown in **Table 5**)


Q.6: How to replicate the Query Effectiveness performances reported in the paper?
---------------------------------------------------------------------------------------

```
java -jar blizzard-runner.jar -task getReportedQEPerformance  -reportKey ST
```

This commands shows query improvement, query worsening and query preserving statistics across all 6 subject systems (as shown in **Table 9**).


Please cite our work as
------------------------------------------
```
@INPROCEEDINGS{fse2018masud, 
author={Rahman, M. M. and Roy, C. K.}, 
booktitle={Proc. ESEC/FSE}, 
title={Improving IR-Based Bug Localization with Context-Aware Query Reformulation}, 
year={2018}, 
pages={621-632} 
}
```
**Download this paper** [<img src="https://web.cs.dal.ca/~masud/img/pdf.png"
     alt="PDF" heigh="16px" width="16px" />](https://web.cs.dal.ca/~masud/papers/masud-ESECFSE2018.pdf)

```
@INPROCEEDINGS{icse2018masud, 
author={Rahman, M. M. and Roy, C. K.}, 
booktitle={Proc. ICSE-C}, 
title={Improving Bug Localization with Report Quality Dynamics and Query Reformulation}, 
year={2018}, 
pages={348-349} 
}
```
**Download this paper** [<img src="https://web.cs.dal.ca/~masud/img/pdf.png"
     alt="PDF" heigh="16px" width="16px" />](https://web.cs.dal.ca/~masud/papers/masud-ICSE2018.pdf)


# Related Projects: [ACER](https://github.com/masud-technope/ACER-Replication-Package-ASE2017), [STRICT](https://github.com/masud-technope/STRICT-Replication-Package), and [QUICKAR](https://github.com/masud-technope/QUICKAR-Replication-Package-ASE2016)


Something not working as expected?
------------------------------------------------------------------------
Contact: **Masud Rahman** (masud.rahman@usask.ca)

OR

Create an issue from [here](https://github.com/masud-technope/BLIZZARD-Replication-Package-ESEC-FSE2018/issues/new)

























