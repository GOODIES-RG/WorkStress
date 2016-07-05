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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import uk.ac.mdx.cs.ie.workstress.R;
import uk.ac.mdx.cs.ie.workstress.utility.WorkstressUser;

/**
 * Adapter for the cardview of different users
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class UserSelectionAdapter extends RecyclerView.Adapter<UserSelectionAdapter.UserViewHolder> {

    private List<WorkstressUser> mUsers;
    private boolean mIsEmpty;
    private static UserClickListener mClickListener;

    public UserSelectionAdapter(List<WorkstressUser> users, boolean empty) {
        mUsers = users;
        mIsEmpty = empty;
    }

    @Override
    public UserSelectionAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_card_layout, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        WorkstressUser user = mUsers.get(position);
        holder.mUser.setText(user.username);

        if (!mIsEmpty) {
            if (user.checked) {
                holder.mChecked.setVisibility(View.VISIBLE);
            } else {
                holder.mChecked.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setUsers(List<WorkstressUser> users) {
        mUsers = users;
        mIsEmpty = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        protected TextView mUser;
        protected ImageButton mChecked;

        public UserViewHolder(View v) {
            super(v);
            mUser = (TextView) v.findViewById(R.id.txtUser);
            mChecked = (ImageButton) v.findViewById(R.id.checked);
            mChecked.setVisibility(View.INVISIBLE);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(UserClickListener listener) {
        mClickListener = listener;
    }

    public interface UserClickListener {
        void onItemClick(int position, View v);
    }

}
