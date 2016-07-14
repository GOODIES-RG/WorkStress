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

package uk.ac.mdx.cs.ie.workstress.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The SQLiteOpenHelper needed for the db
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class OpenDBHelper extends SQLiteOpenHelper {

    public static final String REPORTTABLE = "report";
    public static final String RATETABLE = "heartrates";
    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "workstressDB";

    private static final String RATETABLE_CREATE = "create table " + RATETABLE
            + " (_id integer primary key autoincrement, "
            + "datetime int,"
            + "rate int);";

    private static final String REPORTTABLE_CREATE = "create table " + REPORTTABLE
            + " (_id integer primary key autoincrement, "
            + " reportid int,"
            + " submit_date int,"
            + " q1 int,"
            + " q2 int,"
            + " q3 int,"
            + " q4 int,"
            + " q5 int,"
            + " q6 int,"
            + " q7 int,"
            + " q8 int);";

    public OpenDBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RATETABLE_CREATE);
        db.execSQL(REPORTTABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RATETABLE);
        db.execSQL(RATETABLE_CREATE);

        db.execSQL("DROP TABLE IF EXISTS " + REPORTTABLE);
        db.execSQL(REPORTTABLE_CREATE);
    }
}
