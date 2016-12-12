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
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import uk.ac.mdx.cs.ie.acontextlib.IContextReceiver;
import uk.ac.mdx.cs.ie.acontextlib.personal.HeartRateMonitor;
import uk.ac.mdx.cs.ie.workstress.db.WorkstressDB;
import uk.ac.mdx.cs.ie.workstress.utility.StressReport;

/**
 * Handles collection of data including heartrate data
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class DataCollector {

    private static final String WORK_PREFS = "StressPrefs";
    private DataUploader mUploader;
    private ArrayList<Integer> mHeartrates = new ArrayList<>(200);
    private ArrayList<Long> mTimestamps = new ArrayList<>(200);
    private ArrayList<Integer> mToSendHeartrates = new ArrayList<>(200);
    private ArrayList<Long> mToSendTimestamps = new ArrayList<>(200);
    private SharedPreferences mSettings;
    private Context mContext;
    private Timer mTimer;
    private boolean mCollecting = false;
    private static final int HR_INTERVAL = 20000;
    private static final int INTERVAL = 60000;
    private static final int REPORT_INTERVAL = 600000;
    private static final int RESENT_INTERVAL = 300000;
    private String mUserID;
    private HeartRateMonitor mHeartrateMonitor;
    private StressService mService;
    private Timer mReportTimer;
    private Timer mReportSendTimer;
    private Timer mHeartrateSendTimer;
    private int mReportDueTime;
    private boolean mAwaitingReport = false;
    private int mReportID = 0;
    private static final String LOG_TAG = "DataCollector";
    private long mLastHeartTime = 0;
    private WorkstressDB mDatabase;
    private Object mLogLock = new Object();
    private long mOutstandingRates = 0;
    private boolean mHeartMonitorBonded = false;


    public DataCollector(Context context, StressService service) {
        mContext = context;
        mService = service;
        mUploader = new ProtobufUploader(context, this);
        mDatabase = new WorkstressDB(mContext);
        mSettings = mContext.getSharedPreferences(WORK_PREFS, 0);
        mUserID = mSettings.getString("userid", "");
        mReportID = mSettings.getInt("reportid", 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            mHeartrateMonitor = new HeartRateMonitor(mContext);
            mHeartrateMonitor.addContextReceiver(new IContextReceiver() {
                @Override
                public void newContextValue(String name, long value) {
                    log((int) value);
                }

                @Override
                public void newContextValue(String name, double value) {

                }

                @Override
                public void newContextValue(String name, boolean value) {
                    if (value == false) {
                        mService.showConnectionTroubleNotification();
                    } else {
                        mService.dismissConnectionNotification();
                    }
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

        mOutstandingRates = mDatabase.numOfRates();

        if (mOutstandingRates > 0) {
            sendOutstandingRates();
        }

        sendOutstandingReports();

    }

    public void setUser(String user, String username) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("userid", user);
        editor.putString("username", username);
        editor.commit();
        mUserID = user;
    }

    public void needReport(int reportID, int date) {
        if (!mAwaitingReport) {
            mAwaitingReport = true;

            mReportDueTime = date + 3600;
            long current = System.currentTimeMillis() / 1000L;

            if (mReportDueTime > current) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt("reportid", reportID);
                editor.putInt("reportstime", date);
                editor.commit();
                mReportID = reportID;

                mReportTimer = new Timer();
                mReportTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        long current = System.currentTimeMillis() / 1000L;

                        if (current < mReportDueTime) {
                            mService.dismissReportNotification();
                            mService.showReportNotification();
                        } else {
                            outOfTime();
                        }
                    }
                }, REPORT_INTERVAL, REPORT_INTERVAL);

                mService.showReportNotification();
                mService.reportNeededBroadcast(true);
            } else {
                outOfTime();
            }
        }
    }

    private void outOfTime() {
        if (mAwaitingReport) {
            mAwaitingReport = false;
            mUploader.ranOutOfTime(mUserID);
            mReportDueTime = 0;
            if (mReportTimer != null) {
                mReportTimer.cancel();
            }
            mService.dismissReportNotification();

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt("reportid", 0);
            editor.putInt("reportstime", 0);
            editor.commit();
            mReportID = 0;
            mService.reportNeededBroadcast(false);
        }
    }

    public boolean submitReport(StressReport report) {
        ArrayList reports = new ArrayList(1);
        report.reportid = mReportID;
        reports.add(report);
        mDatabase.addReports(reports);
        submittedReport();

        sendOutstandingReports();
        return true;
    }

    private void log(int heartrate) {
        synchronized (mLogLock) {

            long time = System.currentTimeMillis();

            if (time > mLastHeartTime) {
                mHeartrates.add(heartrate);
                mTimestamps.add(time);
                mLastHeartTime = time;
            }
        }
    }

    public void startCollecting() {

        int interval = INTERVAL;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            String device = mSettings.getString("macaddress", "");

            if (!device.equals("")) {
                mHeartMonitorBonded = true;
                mHeartrateMonitor.setDeviceID(device);
                mHeartrateMonitor.setConnectRetry(true);
                mHeartrateMonitor.start();
                interval = HR_INTERVAL;
            }

        }

        mCollecting = true;

        mTimer = new Timer();

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                uploadLog();
            }
        }, interval, interval);

    }

    public void stopCollection() {
        if (mCollecting) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (mHeartMonitorBonded) {
                    mHeartrateMonitor.stop();
                }
            }

            mCollecting = false;
            mTimer.cancel();
            uploadLog();
        }
    }

    private void uploadLog() {

        if (mToSendTimestamps.size() > 200) {
            persistLog(mToSendHeartrates, mToSendTimestamps);
        }

        synchronized (mLogLock) {
            mToSendHeartrates.addAll(mHeartrates);
            mToSendTimestamps.addAll(mTimestamps);
            mHeartrates.clear();
            mTimestamps.clear();
        }

        mUploader.uploadHeartBeats(false, mUserID, mToSendHeartrates, mToSendTimestamps);

        if (mOutstandingRates > 0) {
            sendOutstandingRates();
        }
    }

    public void persistLog(List<Integer> rates, List<Long> timestamps) {
        mDatabase.addHeartrates(rates, timestamps);
        mOutstandingRates += rates.size();
        rates.clear();
        timestamps.clear();

    }

    public void onDestroy() {
        outOfTime();

        if (mReportSendTimer != null) {
            mReportSendTimer.cancel();
        }

        if (mHeartrateSendTimer != null) {
            mHeartrateSendTimer.cancel();
        }

        mDatabase.closeDB();
        mUploader.closeConnection();
    }

    public void submittedReport() {
        mAwaitingReport = false;
        mReportDueTime = 0;

        if (mReportTimer != null) {
            mReportTimer.cancel();
        }

        mService.dismissReportNotification();

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("reportid", 0);
        editor.putInt("reportstime", 0);
        editor.commit();
        mReportID = 0;
        mService.reportNeededBroadcast(false);
    }

    public void sendOutstandingReports() {

        List<StressReport> reports = mDatabase.getAllReports();

        if (reports.size() > 0) {
            if (mReportSendTimer != null) {
                mReportSendTimer = new Timer();

                mReportSendTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        sendOutstandingReports();
                    }
                }, RESENT_INTERVAL, RESENT_INTERVAL);
            }

            mUploader.uploadReports(mUserID, reports);
        }
    }

    public void completeOutstandingReports() {

        if (mReportSendTimer != null) {
            mReportSendTimer.cancel();
            mReportSendTimer = null;
        }

        mDatabase.emptyReports();
    }

    public void sendOutstandingRates() {
        List heartrates = mDatabase.getAllHeartrates();

        ArrayList<Integer> rates = (ArrayList<Integer>) heartrates.get(0);
        ArrayList<Long> timestamps = (ArrayList<Long>) heartrates.get(1);

        if (rates.size() == timestamps.size()) {

            if (mHeartrateSendTimer != null) {
                mHeartrateSendTimer = new Timer();

                mHeartrateSendTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        sendOutstandingRates();
                    }
                }, RESENT_INTERVAL, RESENT_INTERVAL);
            }

            mUploader.uploadHeartBeats(true, mUserID, rates, timestamps);
        }
    }

    public List getAllUsers() {
        return mUploader.getAllUsers();
    }

    public void completeOutstandingRates() {
        if (mHeartrateSendTimer != null) {
            mHeartrateSendTimer.cancel();
            mHeartrateSendTimer = null;
        }

        mDatabase.emptyHeartrates();
    }

    public void completeRates() {
        mToSendTimestamps.clear();
        mToSendHeartrates.clear();
    }
}
