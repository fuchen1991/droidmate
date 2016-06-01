// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.configuration.Configuration
import org.droidmate.test_base.FilesystemTestFixtures
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Path

class ExplorationOutput2ReportTest {
 
  @Test
  fun reports() {

    val mockFs: FileSystem = mockFs()
    val cfg = Configuration.getDefault()
    val serExplOutput: Path = FilesystemTestFixtures.build().f_monitoredSer2
    val mockFsDirWithOutput: Path = mockFs.dir(cfg.droidmateOutputDir).withFiles(serExplOutput)
    
    val report = ExplorationOutput2Report(
      data = OutputDir(mockFsDirWithOutput).notEmptyExplorationOutput2,
      dir = mockFs.dir(cfg.reportOutputDir)
    )

    // Act
    report.writeOut()
    
    /* KJA now we have "views seen". We also need:
    - click distribution: amount of click per view. X axis: no of clicks. Y axis: no of views.
    - automatic generation of .pdf with chart.
       */

    // Asserts on the data structure
    report.guiCoverageReports.forEach {
      assertThat(it.guiCoverage.table.rowKeySet().size, greaterThan(0))
      assertThat(it.guiCoverage.table.columnKeySet(),
        hasItems(
          GUICoverage.headerTime,
          GUICoverage.headerViewsSeen,
          GUICoverage.headerViewsClicked
        )
      )
    }
    
    // Asserts on the reports written to (here - mocked) file system.
    assertThat(report.dir.fileNames, hasItem(containsString(GUICoverageReport.fileNameSuffix)))

    val manualInspection = true
    if (manualInspection)
    {
      report.reportFiles.forEach {
        println(it.toAbsolutePath().toString())
        println(it.text())
      }
    }
  }
}

/* Temp fixture for refactoring
Time_seconds	Actionable_unique_views_seen	Actionable_unique_views_clicked
0	0	0
1	3	0
2	3	0
3	3	0
4	3	0
5	3	0
6	3	0
7	3	0
8	3	0
9	3	0
10	3	0
11	3	0
12	3	0
13	3	0
14	3	0
15	3	0
16	3	0
17	3	0
18	3	0
19	3	0
20	3	0
21	3	1
22	3	1
23	14	2
24	14	2
25	14	2
26	14	2
27	14	2
28	14	2
29	14	2
30	14	2
31	14	2
32	14	2
33	14	2
34	14	2
35	14	2
36	14	2
37	14	2
38	14	2
39	14	2
40	14	2
41	17	3
42	17	3
43	29	4
44	29	4
45	29	4
46	29	4
47	29	4
48	29	4
49	29	4
50	29	4
51	29	4
52	29	4
53	29	4
54	29	4
55	29	4
56	29	4
57	29	4
58	29	4
59	29	4
60	29	4
61	29	4
62	29	4
63	29	4
64	29	4
65	29	4
66	29	4
67	29	4
68	29	4
69	29	4
70	34	5
71	34	5
72	35	6
73	35	6
74	35	6
75	35	6
76	35	6
77	35	6
78	35	6
79	35	6
80	35	6
81	35	6
82	35	6
83	35	6
84	35	6
85	35	6
86	35	6
87	35	6
88	35	6
89	35	6
90	35	6
91	35	6
92	-1	-1
93	-1	-1
94	-1	-1
95	-1	-1
 */
