// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2017 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.exploration.actions

import org.droidmate.android_sdk.IApk
import org.droidmate.device.datatypes.AndroidDeviceAction.Companion.newTurnWifiOnDeviceAction
import org.droidmate.exploration.device.DeviceLogsHandler
import org.droidmate.exploration.device.IRobustDevice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RunnableResetAppExplorationAction(action: ResetAppExplorationAction, timestamp: LocalDateTime, takeScreenshot: Boolean)
    : RunnableExplorationAction(action, timestamp, takeScreenshot) {
    companion object {
        private const val serialVersionUID: Long = 1
    }

    private val isFirst: Boolean = (action.isFirst)

    override fun performDeviceActions(app: IApk, device: IRobustDevice) {
        log.debug("1. Clear package ${app.packageName}.")

        device.clearPackage(app.packageName)

        log.debug("2. Clear logcat.")
        // This is made to clean up the logcat if previous app exploration failed. If the clean would not be made, it might be
        // possible some API logs will be read from it, wreaking all kinds of havoc, e.g. having timestamp < than the current
        // exploration start time.
        device.clearLogcat()

        log.debug("3. Ensure home screen is displayed.")
        device.ensureHomeScreenIsDisplayed()

        log.debug("4. Turn wifi on.")
        device.perform(newTurnWifiOnDeviceAction())

        log.debug("5. Get GUI snapshot to ensure device displays valid screen that is not \"app has stopped\" dialog box.")
        device.getGuiSnapshot()

        log.debug("6. Ensure app is not running.")
        if (device.appIsRunning(app.packageName)) {
            log.trace("App is still running. Clearing package again.")
            device.clearPackage(app.packageName)
        }

        log.debug("7. Launch app $app.packageName.")
        device.launchApp(app)

        if (this.isFirst || this.takeScreenshot) {
            log.debug("7.firstReset: Take a screenshot of first reset action.")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss_SSS")
            this.screenshot = device.takeScreenshot(app, timestamp.format(formatter)).toUri()
        }

        log.debug("8. Get GUI snapshot.")
        this.snapshot = device.getGuiSnapshot()

        log.debug("9. Try to read API logs.")
        val logsHandler = DeviceLogsHandler(device)
        logsHandler.readAndClearApiLogs()
        this.logs = logsHandler.getLogs()

    }
}

