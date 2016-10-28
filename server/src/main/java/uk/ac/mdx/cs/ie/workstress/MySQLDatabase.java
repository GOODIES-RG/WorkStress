package uk.ac.mdx.cs.ie.workstress;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.mdx.cs.ie.workstress.proto.HeartRate;
import uk.ac.mdx.cs.ie.workstress.proto.StressReport;
import uk.ac.mdx.cs.ie.workstress.proto.UserInformation;

/**
 * MySQL Database
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */

public class MySQLDatabase implements Database {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/workstress?autoReconnect=true&useSSL=false";
    private static final String DB_USER = "";
    private static final String DB_PASS = "";
    private Connection mConnection;

    private static final String GET_ALL_USERS_STRING =
            "SELECT id, user_id from user ORDER BY user.id ASC;";

    private static final String UPDATE_REPORT_STRING =
            "UPDATE report SET submit_date = ?, q1 = ?, q2 = ?, q3 = ?, q4 = ?, q5 = ?, q6 = ?, q7 = ?, q8 = ? WHERE id = ?";

    private static final String UPDATE_REPORT_NEEDED_STRING =
            "UPDATE user SET reportneeded = 0 WHERE id = ?;";

    private static final String ADD_NEW_HEARTRATE_STRING =
            "INSERT INTO heartbeat(user_id, datetime, rate) VALUES (?, ?, ?);";

    private static final String GET_REPORT_NEEDED_STRING =
            "SELECT reportneeded FROM user WHERE id = ?";

    private static final String GET_REPORT_DATE_STRING =
            "SELECT request_date FROM report WHERE id = ?";

    public MySQLDatabase() {
        getConnection();
    }

    private Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            if (mConnection == null || !mConnection.isValid(1)) {
                mConnection = (Connection) DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            mConnection = null;
        }


        return mConnection;
    }

    public void close() throws SQLException {
        if (mConnection == null || !mConnection.isValid(1)) {
            mConnection.close();
        }
    }

    @Override
    public List<UserInformation> getAllUsers(List<UserInformation> users) throws SQLException {

        Connection con = getConnection();

        if (con == null) {
            System.err.println("Cannot get DB connection");
        }

        PreparedStatement getUsers = null;

        try {
            con.setAutoCommit(false);

            getUsers = con.prepareStatement(GET_ALL_USERS_STRING);
            ResultSet rs = getUsers.executeQuery();
            while (rs.next()) {
                UserInformation user = UserInformation.newBuilder()
                        .setUserid(rs.getInt(1))
                        .setUsername(rs.getString(2))
                        .build();

                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (getUsers != null) {
                getUsers.close();
            }

            con.setAutoCommit(true);
        }

        return users;

    }

    @Override
    public int addNewReports(int user, List<StressReport> reportsList) throws SQLException {

        Connection con = getConnection();

        int result = -2;

        if (con == null) {
            System.err.println("Cannot get DB connection");
            return result;
        }

        PreparedStatement newReports = null;

        try {
            result = -3;
            con.setAutoCommit(false);
            newReports = con.prepareStatement(UPDATE_REPORT_STRING);

            for (StressReport report : reportsList) {

                newReports.setLong(1, report.getTimestamp());
                newReports.setInt(2, report.getQ1());
                newReports.setInt(3, report.getQ2());
                newReports.setInt(4, report.getQ3());
                newReports.setInt(5, report.getQ4());
                newReports.setInt(6, report.getQ5());
                newReports.setInt(7, report.getQ6());
                newReports.setInt(8, report.getQ7());
                newReports.setInt(9, report.getQ8());
                newReports.setInt(10, user);
                newReports.addBatch();
            }

            newReports.executeBatch();
            resetReportNeeded(user, con);
            con.commit();

            result = 1;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            con.rollback();
        } finally {
            if (newReports != null) {
                newReports.close();
            }

            con.setAutoCommit(true);
        }

        return result;
    }

    @Override
    public int[] isReportNeeded(int user) throws SQLException {

        int[] result = new int[2];
        result[0] = -2;

        Connection con = getConnection();
        PreparedStatement reportNeeded = null;
        PreparedStatement reportDate = null;

        try {
            reportNeeded = con.prepareStatement(GET_REPORT_NEEDED_STRING);

            reportNeeded.setInt(1, user);
            ResultSet rs = reportNeeded.executeQuery();

            while (rs.next()) {
                result[0] = rs.getInt(1);
            }

            if (result[0] > 0) {

                reportDate = con.prepareStatement(GET_REPORT_DATE_STRING);
                reportDate.setInt(1, result[0]);

                rs = reportDate.executeQuery();

                while (rs.next()) {
                    result[1] = rs.getInt(1);
                }
            }


        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {

            if (reportNeeded != null) {
                reportNeeded.close();
            }

            if (reportDate != null) {
                reportDate.close();
            }
        }

        return result;
    }

    @Override
    public int ranOfTime(int user) {

        Connection con = getConnection();
        int result = -2;
        try {
            resetReportNeeded(user, con);
            result = 1;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }

    @Override
    public int[] addNewHeartRates(int user, List<HeartRate> heartRateList) throws SQLException {

        Connection con = getConnection();

        int result[] = new int[2];
        result[0] = -2;

        if (con == null) {
            System.err.println("Cannot get DB connection");
            return result;
        }

        PreparedStatement newHeartRates = null;

        try {
            result[0] = -3;
            con.setAutoCommit(false);

            newHeartRates = con.prepareStatement(ADD_NEW_HEARTRATE_STRING);

            for (HeartRate rate : heartRateList) {
                newHeartRates.setInt(1, user);
                newHeartRates.setLong(2, rate.getTimestamp());
                newHeartRates.setInt(3, rate.getHeartrate());
                newHeartRates.addBatch();
            }

            newHeartRates.executeBatch();
            result = isReportNeeded(user);
            con.commit();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (newHeartRates != null) {
                newHeartRates.close();
            }

            con.setAutoCommit(true);
        }

        return result;
    }

    private static void resetReportNeeded(int user, Connection con) throws SQLException {

        PreparedStatement resetReportNeeded = null;

        try {

            resetReportNeeded = con.prepareStatement(UPDATE_REPORT_NEEDED_STRING);
            resetReportNeeded.setInt(1, user);
            resetReportNeeded.execute();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (resetReportNeeded != null) {
                resetReportNeeded.close();
            }
        }
    }
}
