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

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

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


    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mCollector = new DataCollector(mContext, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mStressServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        return START_STICKY;
    }

    public final IStressService.Stub mStressServiceBinder = new IStressService.Stub() {

        @Override
        public boolean sendReport(String user, StressReport report) throws RemoteException {
            return false;
        }

        @Override
        public boolean setUsername(String username) throws RemoteException {
            mCollector.setUsername(username);
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

    };

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

        Intent resultIntent = new Intent(mContext, MainActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, 0);
        builder.setContentIntent(resultPendingIntent);

        startForeground(HEARTMONITOR_STATUS, builder.build());
        mCollector.startCollecting();

        mCollecting = true;
        return true;
    }

    public void showReportNotification() {

    }

    public boolean stopHeartMonitor() {
        stopForeground(true);
        mCollecting = false;
        mWakeLock.release();
        return true;
    }
}
