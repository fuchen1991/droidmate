// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class GUICoverage(val data: IApkExplorationOutput2) {

  val table: Table<Int, String, Int> by lazy {

    val uniqueWidgets = UniqueWidgets(data)

    // KJA extract ms step
    val timeRange = 0.rangeTo(data.explorationTimeInMs).step(1000)

    val rows: List<Triple<Int, Int, Int>> = timeRange.mapIndexed { tickIndex, timePassed ->
      // KJA the "widgetsSeen.byTime" is the only variable, all the rest can be extracted. The variable is "some value that got increment by given other value"
      Triple(tickIndex, timePassed, uniqueWidgets.byTime(timePassed))
    }

    // KJA extract "make table from (column headers, rows represented by triplets)
    tableBuilder().apply {
      rows.forEach { row ->
        // KJA dry up column headers
        put(row.first, "Time", row.second)
        put(row.first, "Views seen", row.third)
      }
    }.build()
  }

  private fun tableBuilder(): ImmutableTable.Builder<Int, String, Int> {

    return ImmutableTable
      .Builder<Int, String, Int>()
      .orderColumnsBy(compareBy {
        when (it) {
          "Time" -> 0
          "Views seen" -> 1
          else -> throw UnexpectedIfElseFallthroughError()
        }
      })
      .orderRowsBy(naturalOrder<Int>())
  }
}

