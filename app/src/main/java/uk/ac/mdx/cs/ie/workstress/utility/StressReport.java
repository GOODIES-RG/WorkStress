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
 * Class holding Stress Report Information
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class StressReport implements Parcelable {

    public String date;
    public int question1;
    public int question2;
    public int question3;
    public int question4;
    public int question5;
    public int question6;
    public int question7;
    public int question8;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeInt(question1);
        dest.writeInt(question2);
        dest.writeInt(question3);
        dest.writeInt(question4);
        dest.writeInt(question5);
        dest.writeInt(question6);
        dest.writeInt(question7);
        dest.writeInt(question8);
    }

    public static final Parcelable.Creator<StressReport> CREATOR
            = new Parcelable.Creator<StressReport>() {

        @Override
        public StressReport createFromParcel(Parcel source) {
            return new StressReport(source);
        }

        @Override
        public StressReport[] newArray(int size) {
            return new StressReport[size];
        }
    };

    public StressReport() {

    }

    private StressReport(Parcel in) {
        date = in.readString();
        question1 = in.readInt();
        question2 = in.readInt();
        question3 = in.readInt();
        question4 = in.readInt();
        question5 = in.readInt();
        question6 = in.readInt();
        question7 = in.readInt();
        question8 = in.readInt();
    }
}
