package uk.ac.mdx.cs.ie.workstress;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest;
import uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse;
import uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest;
import uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest;
import uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse;
import uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest;
import uk.ac.mdx.cs.ie.workstress.proto.UserInformation;

/**
 * Main Service class for handling GRPC Events
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */

public class WorkstressServer {

    private static final Logger logger = Logger.getLogger(WorkstressServer.class.getName());
    private Database mDatabase;
    private static final String API_KEY = "";

    private static final int PORT = 8080;
    private Server mServer;

    private void start() throws IOException {

        mDatabase = new MySQLDatabase();

        mServer = ServerBuilder.forPort(PORT)
                .addService(new ServiceImpl()).build().start();

        logger.info("WorkstressServer started, listening on " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                WorkstressServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (mServer != null) {
            try {
                mDatabase.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }

            mServer.shutdown();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final WorkstressServer server = new WorkstressServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (mServer != null) {
            mServer.awaitTermination();
        }
    }

    private class ServiceImpl extends WorkStressServiceGrpc.WorkStressServiceImplBase {

        @Override
        public void newheartrates(HeartRatesRequest req, StreamObserver<ServiceResponse> responseObserver) {

            int[] response = {-1, 0};

            if (req.getApikey().equals(API_KEY)) {

                try {
                    int user = req.getUser();

                    if (req.getHeartratesCount() < 1) {
                        response = mDatabase.isReportNeeded(user);
                    } else {
                        response = mDatabase.addNewHeartRates(user, req.getHeartratesList());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            ServiceResponse reply = ServiceResponse.newBuilder()
                    .setResponse(response[0])
                    .setRequesttime(response[1])
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void outoftime(RanOutOfTimeRequest req, StreamObserver<ServiceResponse> responseObserver) {

            int response = -1;

            if (req.getApikey().equals(API_KEY)) {

                response = mDatabase.ranOfTime(req.getUser());
            }

            ServiceResponse reply = ServiceResponse.newBuilder().setResponse(response).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void getallusers(AllUsersRequest req, StreamObserver<AllUsersResponse> responseObserver) {

            List<UserInformation> users = new ArrayList<>();

            if (req.getApikey().equals(API_KEY)) {
                try {
                    users = mDatabase.getAllUsers(users);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            AllUsersResponse.Builder reply = AllUsersResponse.newBuilder().addAllUsers(users);
            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();
        }

        @Override
        public void newReports(StressReportsRequest req, StreamObserver<ServiceResponse> responseObserver) {

            int response = -1;

            if (req.getApikey().equals(API_KEY)) {

                try {
                    response = mDatabase.addNewReports(req.getUser(), req.getReportsList());

                    if (response > -1) {

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            ServiceResponse reply = ServiceResponse.newBuilder().setResponse(response).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
