package uk.ac.mdx.cs.ie.workstress;

import java.sql.SQLException;
import java.util.List;

import uk.ac.mdx.cs.ie.workstress.proto.StressReport;
import uk.ac.mdx.cs.ie.workstress.proto.UserInformation;

/**
 * Database interface
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */

public interface Database {


    void close() throws SQLException;

    List<UserInformation> getAllUsers(List<UserInformation> users) throws SQLException;

    int addNewReports(String user, List<StressReport> reportsList) throws SQLException;

    int[] isReportNeeded(String user) throws SQLException;

    int ranOfTime(String user);

    int[] addNewHeartRates(String user, List<Integer> heartRates, List<Long> timestamps) throws SQLException;
}
