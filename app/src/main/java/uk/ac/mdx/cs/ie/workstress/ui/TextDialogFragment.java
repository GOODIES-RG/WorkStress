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

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import uk.ac.mdx.cs.ie.workstress.R;
import uk.ac.mdx.cs.ie.workstress.utility.DialogReturnInterface;

/**
 * Reusable text input dialog
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class TextDialogFragment extends DialogFragment {

    private EditText mText;
    private AlertDialog mDialog;
    private Activity mActivity;


    public static TextDialogFragment newInstance(int title) {
        TextDialogFragment fragment = new TextDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        mActivity = getActivity();

        mDialog = new AlertDialog.Builder(mActivity)
                .setTitle(title)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doPositiveClick();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doNegativeClick();
                            }
                        })
                .create();

        View dialogView = mActivity.getLayoutInflater().inflate(R.layout.textdialog, null);
        mDialog.setView(dialogView);
        setCancelable(false);

        mText = (EditText) dialogView.findViewById(R.id.txt_text);

        return mDialog;
    }

    private void doPositiveClick() {
        String text = mText.getText().toString();

        ((DialogReturnInterface) getActivity()).doPositiveButtonClick(text);
    }

    private void doNegativeClick() {
        ((DialogReturnInterface) getActivity()).doNegativeButtonClick();
    }

    public void setText(String text) {
        mText.setText(text);
    }
}
