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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mdx.cs.ie.workstress.utility.StressReport;


/**
 * Implementation class to handle context database operations
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class WorkstressDB {

    private OpenDBHelper dbHelper;
    public static final String REPORTTABLE = "report";
    public static final String RATETABLE = "heartrates";

    public WorkstressDB(Context context) {
        dbHelper = new OpenDBHelper(context);
    }

    public void closeDB() {
        dbHelper.close();
    }

    public synchronized List<StressReport> getAllReports() {

        List<StressReport> reports = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        try {

            Cursor cursor = sqLiteDatabase.rawQuery(
                    "Select reportid, submit_date, q1, q2, q3, q4, q5, q6, q7, q8 from report;",
                    null);

            while (cursor.moveToNext()) {
                StressReport report = new StressReport();
                report.reportid = cursor.getInt(0);
                report.date = cursor.getInt(1);
                report.question1 = cursor.getInt(2);
                report.question2 = cursor.getInt(3);
                report.question3 = cursor.getInt(4);
                report.question4 = cursor.getInt(5);
                report.question5 = cursor.getInt(6);
                report.question6 = cursor.getInt(7);
                report.question7 = cursor.getInt(8);
                report.question8 = cursor.getInt(9);

                reports.add(report);
            }

        } catch (Exception sqlerror) {
            Log.e("Table read error", sqlerror.getMessage());
        } finally {
            sqLiteDatabase.close();
        }

        return reports;
    }

    public synchronized List getAllHeartrates() {

        List heartrates = new ArrayList();
        List rates = new ArrayList();
        List dates = new ArrayList();
        heartrates.add(rates);
        heartrates.add(dates);

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        try {

            Cursor cursor = sqLiteDatabase.rawQuery(
                    "Select rate, datetime from heartrates;", null);

            while (cursor.moveToNext()) {
                rates.add(cursor.getInt(0));
                dates.add(cursor.getLong(1));
            }

        } catch (Exception sqlerror) {
            Log.e("Table read error", sqlerror.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
        return heartrates;
    }

    public synchronized void addReports(List<StressReport> reports) {

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        try {

            sqLiteDatabase.beginTransaction();

            for (StressReport report : reports) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("reportid", report.reportid);
                contentValues.put("submit_date", report.date);
                contentValues.put("q1", report.question1);
                contentValues.put("q2", report.question2);
                contentValues.put("q3", report.question3);
                contentValues.put("q4", report.question4);
                contentValues.put("q5", report.question5);
                contentValues.put("q6", report.question6);
                contentValues.put("q7", report.question7);
                contentValues.put("q8", report.question8);
                sqLiteDatabase.insert(dbHelper.REPORTTABLE, null, contentValues);
            }

            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception sqlerror) {
            Log.e("Table insert error", sqlerror.getMessage());
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    public synchronized void addHeartrates(List<Integer> heartrates, List<Long> timestamps) {

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        try {

            sqLiteDatabase.beginTransaction();

            int size = heartrates.size();

            if (size == timestamps.size()) {
                for (int i = 0; i < size; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("datetime", timestamps.get(i));
                    contentValues.put("rate", heartrates.get(i));
                    sqLiteDatabase.insert(RATETABLE, null, contentValues);
                }

                sqLiteDatabase.setTransactionSuccessful();
            }

        } catch (Exception sqlerror) {
            Log.e("Table insert error", sqlerror.getMessage());
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    public synchronized void emptyReports() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(dbHelper.REPORTTABLE, null, null);
        sqLiteDatabase.close();
    }

    public synchronized void emptyHeartrates() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(dbHelper.RATETABLE, null, null);
        sqLiteDatabase.close();
    }
}
