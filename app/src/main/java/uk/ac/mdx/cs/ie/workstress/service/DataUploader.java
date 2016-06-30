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


import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import uk.ac.mdx.cs.ie.workstress.utility.StressReport;

/**
 * Class to handle communication with webservice for data collection
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class DataUploader {

    //The RPC Server address
    private static final String SERVER_URL = "";
    private static final String API_KEY = "";
    private static final String RPC_REPORT_FUNCTION = "workstress.newreport";
    private static final String RPC_HEARTBEAT_FUNCTION = "workstress.newheartbeats";
    private static final String RPC_NEWUSER = "workstress.getuser";

    private XMLRPCClient mRPCClient;
    private static final String LOG_TAG = "DataUploader";
    private DataCollector mCollector;
    private StressService mService;

    public DataUploader(DataCollector collector, StressService service) {

        mCollector = collector;
        mService = service;

        try {
            mRPCClient = new XMLRPCClient(new URL(SERVER_URL));
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }


    public boolean uploadReport(final Integer user, final StressReport report) {

        final boolean[] success = {false};

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList params = new ArrayList();
                    params.add(API_KEY);
                    params.add(user);

                    Object i = mRPCClient.call(RPC_REPORT_FUNCTION, params);

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return true;
    }

    public void uploadHeartBeats(final Integer user, final ArrayList<Integer> heartbeats, final ArrayList<Long> timestamps) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList params = new ArrayList();
                params.add(API_KEY);
                params.add(user);
                params.add(heartbeats);
                params.add(timestamps);

                mRPCClient.callAsync(new XMLRPCCallback() {
                    @Override
                    public void onResponse(long id, Object result) {

                        Integer status = (Integer) result;

                        if (status > 1) {
                            mCollector.uploadComplete();
                        }

                        if (status == 2) {
                            mService.showReportNotification();
                        }

                    }

                    @Override
                    public void onError(long id, XMLRPCException error) {

                    }

                    @Override
                    public void onServerError(long id, XMLRPCServerException error) {

                    }
                }, RPC_HEARTBEAT_FUNCTION, params);
            }
        }).start();
    }

    public void getUserId(final String username) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList params = new ArrayList();
                params.add(API_KEY);
                params.add(username);

                try {
                    Object i = mRPCClient.call(RPC_NEWUSER, params);

                    if (i instanceof Integer) {
                        mCollector.newUserId((Integer) i);
                    }

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Cannot call getUserId");
                }
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }


}
