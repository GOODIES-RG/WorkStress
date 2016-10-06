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

package uk.ac.mdx.cs.ie.workstress.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.mdx.cs.ie.workstress.R;
import uk.ac.mdx.cs.ie.workstress.service.IStressService;
import uk.ac.mdx.cs.ie.workstress.utility.ExplicitIntentGenerator;
import uk.ac.mdx.cs.ie.workstress.utility.WorkstressUser;

/**
 * User Selection Activity
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class UserSelectionActivity extends AppCompatActivity {

    private UserSelectionFragment mFragment;
    private static final String LOG_TAG = "UserSelectionActivity";
    private FragmentManager mFragManager;
    private IStressService mStressService;
    private boolean mBound = false;
    private SharedPreferences mSettings;
    private Context mContext;
    private Toolbar mToolbar;
    private int mUser = 0;
    private static final String STRESS_PREFS = "StressPrefs";
    private static final String USER_PREF = "userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mSettings = mContext.getSharedPreferences(STRESS_PREFS, 0);
        mUser = mSettings.getInt(USER_PREF, 0);
        mFragManager = getSupportFragmentManager();
        setContentView(R.layout.activity_user);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFragment = (UserSelectionFragment) mFragManager.findFragmentById(R.id.userfragment);
        bindToService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                getUsers();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUser(int user, String username) {
        mUser = user;

        if (mStressService != null) {
            try {
                mStressService.setUser(user, username);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, e.getMessage().toString());
            }
        }

    }

    public void getUsers() {

        List users = new ArrayList();

        try {
            users = mStressService.getAllUsers();
            Collections.sort(users);

            if (mUser > 0) {
                WorkstressUser user = (WorkstressUser) users.get(mUser - 1);
                user.checked = true;
            }

        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage().toString());
        }

        mFragment.setUsers(users);
    }

    private synchronized void bindToService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(IStressService.class.getName());

                serviceIntent = ExplicitIntentGenerator
                        .createExplicitFromImplicitIntent(mContext, serviceIntent);

                if (serviceIntent == null) {
                    Snackbar.make(mToolbar, getText(R.string.servicecantbind), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
                }
            }

        }).start();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStressService = IStressService.Stub.asInterface(service);
            mBound = !mBound;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mStressService = null;
            mBound = !mBound;
        }
    };

    public void unBindFromService() {
        if (mBound) {
            unbindService(mConnection);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBindFromService();
    }

}
