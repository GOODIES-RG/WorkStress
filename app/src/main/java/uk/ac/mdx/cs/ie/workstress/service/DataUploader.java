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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import uk.ac.mdx.cs.ie.workstress.utility.StressReport;
import uk.ac.mdx.cs.ie.workstress.utility.WorkstressUser;

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
    private static final String RPC_OUTOFTIME_FUNCTION = "workstress.outoftime";
    private static final String RPC_GETALLUSERS_FUNCTION = "workstress.getallusers";

    private XMLRPCClient mRPCClient;
    private static final String LOG_TAG = "DataUploader";
    private DataCollector mCollector;

    public DataUploader(DataCollector collector) {

        mCollector = collector;

        try {
            mRPCClient = new XMLRPCClient(new URL(SERVER_URL));
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public boolean ranOutOfTime(final Integer user) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList params = new ArrayList();
                    params.add(API_KEY);
                    params.add(user);

                    Object i = mRPCClient.call(RPC_OUTOFTIME_FUNCTION, params);

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

    public List getAllUsers() {

        final ArrayList users = new ArrayList();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList params = new ArrayList();
                    params.add(API_KEY);

                    Object o = mRPCClient.call(RPC_GETALLUSERS_FUNCTION, params);

                    if (o instanceof HashMap) {

                        HashMap<String, String> results = (HashMap<String, String>) o;

                        for (Map.Entry<String, String> entry : results.entrySet()) {
                            WorkstressUser user = new WorkstressUser();
                            user.userid = Integer.parseInt(entry.getKey());
                            user.username = entry.getValue();
                            users.add(user);
                        }

                    } else {
                        Log.e(LOG_TAG, "Couldn't get Users: " + (Integer) o);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage().toString());
                }
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return users;
    }

    public boolean uploadReport(final Integer user, final Integer reportNumber, final StressReport report) {

        final boolean[] success = {false};

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList params = new ArrayList();
                    params.add(API_KEY);
                    params.add(user);
                    params.add(reportNumber);
                    params.add(report.date);
                    params.add(report.question1);
                    params.add(report.question2);
                    params.add(report.question3);
                    params.add(report.question4);
                    params.add(report.question5);
                    params.add(report.question6);
                    params.add(report.question7);
                    params.add(report.question8);

                    Object i = mRPCClient.call(RPC_REPORT_FUNCTION, params);

                    mCollector.submittedReport();

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage().toString());
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

                        Integer status = -1;
                        Integer date = 0;

                        if (result instanceof Object[]) {
                            Object[] results = (Object[]) result;
                            status = (Integer) results[0];
                            date = (Integer) results[1];
                        } else {
                            Log.e(LOG_TAG, "Error: " + result);
                        }

                        if (status > -1) {
                            mCollector.uploadComplete();
                        }

                        if (status > 0) {
                            mCollector.needReport(status, date);
                        }

                    }

                    @Override
                    public void onError(long id, XMLRPCException error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }

                    @Override
                    public void onServerError(long id, XMLRPCServerException error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }
                }, RPC_HEARTBEAT_FUNCTION, params);
            }
        }).start();
    }
}
