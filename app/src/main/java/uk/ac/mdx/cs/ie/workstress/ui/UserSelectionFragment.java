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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mdx.cs.ie.workstress.R;
import uk.ac.mdx.cs.ie.workstress.utility.WorkstressUser;

/**
 * User Selection Fragment
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class UserSelectionFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UserSelectionAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<WorkstressUser> mUsers;
    private UserSelectionActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceData) {

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mUsers = populateUsers();
        mAdapter = new UserSelectionAdapter(mUsers, true);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = (UserSelectionActivity) getActivity();
        mAdapter.setOnItemClickListener(new UserSelectionAdapter.UserClickListener() {

            @Override
            public void onItemClick(int position, View v) {
                WorkstressUser user = mUsers.get(position);
                int size = mUsers.size();

                for (int i = 0; i < size; i++) {
                    if (i != position) {
                        WorkstressUser u = mUsers.get(i);
                        u.checked = false;
                    }
                }

                if (user.checked) {
                    user.checked = false;
                    mActivity.setUser("", "");
                } else {
                    user.checked = true;
                    mActivity.setUser(user.userid, user.username);
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setUsers(List<WorkstressUser> users) {

        if (users.size() > 0) {
            mUsers = users;
            mAdapter.setUsers(users);
        }

    }

    public List<WorkstressUser> populateUsers() {

        List<WorkstressUser> users = new ArrayList<>();

        if (users.size() == 0) {
            WorkstressUser user = new WorkstressUser();
            user.userid = "";
            user.username = "No Users found...";
            users.add(user);
        }

        return users;
    }
}
