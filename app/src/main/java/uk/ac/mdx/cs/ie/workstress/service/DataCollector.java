/*Copyright 2016 WorkStress Experiment
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package uk.ac.mdx.cs.ie.workstress.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import uk.ac.mdx.cs.ie.acontextlib.ContextReceiver;
import uk.ac.mdx.cs.ie.acontextlib.HeartRateMonitor;
import uk.ac.mdx.cs.ie.workstress.utility.StressReport;

/**
 * Handles collection of data including heartrate data
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class DataCollector {

    private static final String WORK_PREFS = "StressPrefs";
    private DataUploader mUploader;
    private ArrayList<Integer> mHeartrates = new ArrayList<>(40);
    private ArrayList<Long> mTimestamps = new ArrayList<>(40);
    private ArrayList<Integer> mUploadHeartrates = new ArrayList<>(40);
    private ArrayList<Long> mUploadTimestamps = new ArrayList<>(40);
    private SharedPreferences mSettings;
    private Context mContext;
    private Timer mTimer;
    private boolean mCollecting = false;
    private static final int INTERVAL = 20000;
    private static final int REPORT_INTERVAL = 600000;
    private int mUserID;
    private HeartRateMonitor mHeartrateMonitor;
    private StressService mService;
    private Timer mReportTimer;
    private int mReportTimerCounter = 0;
    private boolean mAwaitingReport = false;
    private int mReportID = 0;

    public DataCollector(Context context, StressService service) {
        mContext = context;
        mService = service;
        mUploader = new DataUploader(this);
        mSettings = mContext.getSharedPreferences(WORK_PREFS, 0);
        mUserID = mSettings.getInt("userid", 0);
        mHeartrateMonitor = new HeartRateMonitor(mContext, new ContextReceiver() {
            @Override
            public void newContextValue(String name, long value) {
                log((int) value);
            }

            @Override
            public void newContextValue(String name, double value) {

            }

            @Override
            public void newContextValue(String name, boolean value) {

            }

            @Override
            public void newContextValue(String name, String value) {

            }

            @Override
            public void newContextValue(String name, Object value) {

            }

            @Override
            public void newContextValues(Map<String, String> values) {

            }
        });
    }

    public void setUser(int user) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("userid", user);
        editor.commit();
        mUserID = user;
    }

    public void needReport(int reportID) {
        if (!mAwaitingReport) {
            mAwaitingReport = true;
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt("reportid", reportID);
            editor.commit();
            mReportID = reportID;

            mReportTimer = new Timer();
            mReportTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    if (mReportTimerCounter < 5) {
                        mService.dismissReportNotification();
                        mService.showReportNotification();
                        mReportTimerCounter++;
                    } else {
                        outOfTime();
                    }
                }
            }, REPORT_INTERVAL, REPORT_INTERVAL);

            mService.showReportNotification();
            mService.reportNeededBroadcast(true);
        }
    }

    private void outOfTime() {
        if (mAwaitingReport) {
            mAwaitingReport = false;
            mUploader.ranOutOfTime(mUserID);
            mReportTimerCounter = 0;
            mReportTimer.cancel();
            mService.dismissReportNotification();

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt("reportid", 0);
            editor.commit();
            mReportID = 0;
            mService.reportNeededBroadcast(false);
        }
    }

    public boolean submitReport(StressReport report) {
        mUploader.uploadReport(mUserID, mReportID, report);
        return true;
    }

    private synchronized void log(int heartrate) {
        mHeartrates.add(heartrate);
        mTimestamps.add(System.currentTimeMillis() / 1000L);
    }

    public void startCollecting() {

        String device = mSettings.getString("macaddress", "");

        mHeartrateMonitor.setDeviceID(device);
        mHeartrateMonitor.start();

        mCollecting = true;

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                uploadLog();
            }
        }, INTERVAL, INTERVAL);

    }

    public void stopCollection() {
        if (mCollecting) {
            mHeartrateMonitor.stop();
            mCollecting = false;
            mTimer.cancel();
            uploadLog();
        }
    }

    private void uploadLog() {

        copyLog();

        mUploader.uploadHeartBeats(mUserID, mUploadHeartrates, mUploadTimestamps);
    }

    private synchronized void copyLog() {
        mUploadHeartrates.addAll(mHeartrates);
        mUploadTimestamps.addAll(mTimestamps);

        mHeartrates.clear();
        mTimestamps.clear();
    }

    public synchronized void uploadComplete() {
        mUploadHeartrates.clear();
        mUploadTimestamps.clear();
    }

    public void onDestroy() {
        outOfTime();
    }

    public void submittedReport() {
        mAwaitingReport = false;
        mReportTimerCounter = 0;

        if (mReportTimer != null) {
            mReportTimer.cancel();
        }

        mService.dismissReportNotification();

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("reportid", 0);
        editor.commit();
        mReportID = 0;
        mService.reportNeededBroadcast(false);
    }

    public List getAllUsers() {
        return mUploader.getAllUsers();
    }
}
