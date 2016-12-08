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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import uk.ac.mdx.cs.ie.workstress.R;
import uk.ac.mdx.cs.ie.workstress.ui.MainActivity;
import uk.ac.mdx.cs.ie.workstress.utility.StressReport;

/**
 * Android Service to collect all sensor data
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class StressService extends Service {

    public static final int HEARTMONITOR_STATUS = 1;
    private PowerManager.WakeLock mWakeLock;
    private Context mContext;
    private boolean mCollecting = false;
    private DataCollector mCollector;
    private NotificationManager mNotificationManager;
    public static final String BROADCAST_INTENT = "uk.ac.mdx.cs.ie.NEED_REPORT";
    public static final String BROADCAST_NEEDED = "needed";
    public static final String LOG_TAG = "StressService";
    private boolean mConTroubles = false;


    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mCollector = new DataCollector(mContext, this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mStressServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        if (IStressService.class.getName().equals(intent.getAction())) {
            BootReceiver.completeWakefulIntent(intent);
        }

        return START_STICKY;
    }

    public final IStressService.Stub mStressServiceBinder = new IStressService.Stub() {

        @Override
        public boolean sendReport(StressReport report) throws RemoteException {
            mCollector.submitReport(report);
            return false;
        }

        @Override
        public boolean setUser(String user, String username) throws RemoteException {
            mCollector.setUser(user, username);
            return true;
        }

        @Override
        public boolean startHeartMonitor() throws RemoteException {
            return StressService.this.startHeartMonitor();
        }

        @Override
        public boolean stopHeartMonitor() throws RemoteException {
            return StressService.this.stopHeartMonitor();
        }

        @Override
        public boolean isCollecting() throws RemoteException {
            return StressService.this.isCollecting();
        }

        @Override
        public void dismissNotification() throws RemoteException {
            dismissReportNotification();
        }

        @Override
        public List getAllUsers() throws RemoteException {
            return mCollector.getAllUsers();
        }

        @Override
        public void deviceChange() throws RemoteException {
            StressService.this.deviceChange();
        }

    };

    private void deviceChange() {

        if (mCollecting) {
            mCollector.stopCollection();
            mCollector.startCollecting();
        }
    }

    public boolean isCollecting() {
        return mCollecting;
    }

    public boolean startHeartMonitor() {

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        mWakeLock.acquire();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getText(R.string.app_name));
        builder.setContentText(getText(R.string.collecting));
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(mContext, MainActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, 0);
        builder.setContentIntent(resultPendingIntent);

        startForeground(HEARTMONITOR_STATUS, builder.build());
        mCollector.startCollecting();

        mCollecting = true;
        return true;
    }

    public void showReportNotification() {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle(getText(R.string.reportneeded));
        builder.setContentText(getText(R.string.reportneededtext));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(resultPendingIntent);
        builder.setSound(alarmSound);
        builder.setLights(Color.BLUE, 500, 500);
        builder.setVibrate(new long[]{500, 500, 500, 500, 500});
        builder.setAutoCancel(true);

        mNotificationManager.notify(2, builder.build());


    }

    public void showConnectionTroubleNotification() {

        if (!mConTroubles) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent resultIntent = new Intent(mContext, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder.setContentTitle(getText(R.string.controuble));
            builder.setContentText(getText(R.string.disconnected));
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentIntent(resultPendingIntent);
            builder.setSound(alarmSound);
            builder.setLights(Color.BLUE, 500, 500);
            builder.setVibrate(new long[]{500, 500, 500, 500, 500});
            builder.setAutoCancel(false);

            mNotificationManager.notify(3, builder.build());
            mConTroubles = true;
        }
    }

    public void dismissConnectionNotification() {
        mNotificationManager.cancel(3);
        mConTroubles = false;
    }

    public void reportNeededBroadcast(boolean needed) {

        Intent intent = new Intent();
        try {
            intent.setAction(BROADCAST_INTENT);
            intent.putExtra(BROADCAST_NEEDED, needed);
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public void dismissReportNotification() {
        mNotificationManager.cancel(2);
    }

    public boolean stopHeartMonitor() {
        if (mCollecting) {
            stopForeground(true);
            mCollector.stopCollection();
            mCollecting = false;
            mWakeLock.release();
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopHeartMonitor();
        mCollector.onDestroy();
    }
}
