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

package uk.ac.mdx.cs.ie.workstress.utility;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class holding WorkstressUser Information
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class WorkstressUser implements Parcelable, Comparable<WorkstressUser> {

    public int userid;
    public String username;
    public boolean checked = false;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userid);
        dest.writeString(username);
    }

    public WorkstressUser() {

    }

    public static final Parcelable.Creator<WorkstressUser> CREATOR
            = new Parcelable.Creator<WorkstressUser>() {

        @Override
        public WorkstressUser createFromParcel(Parcel source) {
            return new WorkstressUser(source);
        }

        @Override
        public WorkstressUser[] newArray(int size) {
            return new WorkstressUser[size];
        }
    };

    private WorkstressUser(Parcel in) {
        userid = in.readInt();
        username = in.readString();
    }

    @Override
    public int compareTo(WorkstressUser another) {
        return userid - another.userid;
    }
}
