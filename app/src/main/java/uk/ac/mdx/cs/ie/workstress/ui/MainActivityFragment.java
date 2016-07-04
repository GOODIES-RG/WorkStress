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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import uk.ac.mdx.cs.ie.workstress.R;
import uk.ac.mdx.cs.ie.workstress.utility.StressReport;

/**
 * Main application Fragment
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class MainActivityFragment extends Fragment implements DiscreteSeekBar.OnProgressChangeListener {

    private MainActivity mActivity;
    private DiscreteSeekBar mSbAnswer1;
    private DiscreteSeekBar mSbAnswer2;
    private DiscreteSeekBar mSbAnswer3;
    private DiscreteSeekBar mSbAnswer4;
    private DiscreteSeekBar mSbAnswer5;
    private DiscreteSeekBar mSbAnswer6;
    private DiscreteSeekBar mSbAnswer7;
    private DiscreteSeekBar mSbAnswer8;
    private static final SimpleDateFormat mDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final Calendar mCurrentDate = Calendar.getInstance();
    private StressReport mReport;

    public MainActivityFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = (MainActivity) getActivity();

        if (mActivity.mReportNeeded) {
            setupUI();
            mReport = new StressReport();
        }
    }


    private void setupUI() {
        mSbAnswer1 = (DiscreteSeekBar) mActivity.findViewById(R.id.sbq1);
        mSbAnswer1.setOnProgressChangeListener(this);

        mSbAnswer2 = (DiscreteSeekBar) mActivity.findViewById(R.id.sbq1);
        mSbAnswer2.setOnProgressChangeListener(this);

        mSbAnswer3 = (DiscreteSeekBar) mActivity.findViewById(R.id.sbq1);
        mSbAnswer3.setOnProgressChangeListener(this);

        mSbAnswer4 = (DiscreteSeekBar) mActivity.findViewById(R.id.sbq1);
        mSbAnswer4.setOnProgressChangeListener(this);

        mSbAnswer5 = (DiscreteSeekBar) mActivity.findViewById(R.id.sbq1);
        mSbAnswer5.setOnProgressChangeListener(this);

        mSbAnswer6 = (DiscreteSeekBar) mActivity.findViewById(R.id.sbq1);
        mSbAnswer6.setOnProgressChangeListener(this);

        mSbAnswer7 = (DiscreteSeekBar) mActivity.findViewById(R.id.sbq1);
        mSbAnswer7.setOnProgressChangeListener(this);

        mSbAnswer8 = (DiscreteSeekBar) mActivity.findViewById(R.id.sbq1);
        mSbAnswer8.setOnProgressChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        if (mActivity.mReportNeeded) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_noreport, container, false);
        }
    }

    public StressReport getStressData() {

        mReport.date = (int) (System.currentTimeMillis() / 1000L);
        return mReport;
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        int id = seekBar.getId();

        switch (id) {
            case R.id.sbq1:
                mReport.question1 = value;
                break;

            case R.id.sbq2:
                mReport.question2 = value;
                break;

            case R.id.sbq3:
                mReport.question3 = value;
                break;

            case R.id.sbq4:
                mReport.question4 = value;
                break;

            case R.id.sbq5:
                mReport.question5 = value;
                break;

            case R.id.sbq6:
                mReport.question6 = value;
                break;

            case R.id.sbq7:
                mReport.question7 = value;
                break;

            case R.id.sbq8:
                mReport.question8 = value;
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }
}
