package uk.ac.mdx.cs.ie.workstress;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.0.0)",
        comments = "Source: service.proto")
public class WorkStressServiceGrpc {

    private WorkStressServiceGrpc() {
    }

    public static final String SERVICE_NAME = "workstress.WorkStressService";

    // Static method descriptors that strictly reflect the proto.
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    public static final io.grpc.MethodDescriptor<uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest,
            uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> METHOD_NEWHEARTRATES =
            io.grpc.MethodDescriptor.create(
                    io.grpc.MethodDescriptor.MethodType.UNARY,
                    generateFullMethodName(
                            "workstress.WorkStressService", "newheartrates"),
                    io.grpc.protobuf.ProtoUtils.marshaller(uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest.getDefaultInstance()),
                    io.grpc.protobuf.ProtoUtils.marshaller(uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse.getDefaultInstance()));
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    public static final io.grpc.MethodDescriptor<uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest,
            uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> METHOD_OUTOFTIME =
            io.grpc.MethodDescriptor.create(
                    io.grpc.MethodDescriptor.MethodType.UNARY,
                    generateFullMethodName(
                            "workstress.WorkStressService", "outoftime"),
                    io.grpc.protobuf.ProtoUtils.marshaller(uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest.getDefaultInstance()),
                    io.grpc.protobuf.ProtoUtils.marshaller(uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse.getDefaultInstance()));
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    public static final io.grpc.MethodDescriptor<uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest,
            uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse> METHOD_GETALLUSERS =
            io.grpc.MethodDescriptor.create(
                    io.grpc.MethodDescriptor.MethodType.UNARY,
                    generateFullMethodName(
                            "workstress.WorkStressService", "getallusers"),
                    io.grpc.protobuf.ProtoUtils.marshaller(uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest.getDefaultInstance()),
                    io.grpc.protobuf.ProtoUtils.marshaller(uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse.getDefaultInstance()));
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    public static final io.grpc.MethodDescriptor<uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest,
            uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> METHOD_NEW_REPORTS =
            io.grpc.MethodDescriptor.create(
                    io.grpc.MethodDescriptor.MethodType.UNARY,
                    generateFullMethodName(
                            "workstress.WorkStressService", "newReports"),
                    io.grpc.protobuf.ProtoUtils.marshaller(uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest.getDefaultInstance()),
                    io.grpc.protobuf.ProtoUtils.marshaller(uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse.getDefaultInstance()));

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static WorkStressServiceStub newStub(io.grpc.Channel channel) {
        return new WorkStressServiceStub(channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static WorkStressServiceBlockingStub newBlockingStub(
            io.grpc.Channel channel) {
        return new WorkStressServiceBlockingStub(channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
     */
    public static WorkStressServiceFutureStub newFutureStub(
            io.grpc.Channel channel) {
        return new WorkStressServiceFutureStub(channel);
    }

    /**
     */
    public static abstract class WorkStressServiceImplBase implements io.grpc.BindableService {

        /**
         */
        public void newheartrates(uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest request,
                                  io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> responseObserver) {
            asyncUnimplementedUnaryCall(METHOD_NEWHEARTRATES, responseObserver);
        }

        /**
         */
        public void outoftime(uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest request,
                              io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> responseObserver) {
            asyncUnimplementedUnaryCall(METHOD_OUTOFTIME, responseObserver);
        }

        /**
         */
        public void getallusers(uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest request,
                                io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse> responseObserver) {
            asyncUnimplementedUnaryCall(METHOD_GETALLUSERS, responseObserver);
        }

        /**
         */
        public void newReports(uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest request,
                               io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> responseObserver) {
            asyncUnimplementedUnaryCall(METHOD_NEW_REPORTS, responseObserver);
        }

        @Override
        public io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            METHOD_NEWHEARTRATES,
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest,
                                            uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse>(
                                            this, METHODID_NEWHEARTRATES)))
                    .addMethod(
                            METHOD_OUTOFTIME,
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest,
                                            uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse>(
                                            this, METHODID_OUTOFTIME)))
                    .addMethod(
                            METHOD_GETALLUSERS,
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest,
                                            uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse>(
                                            this, METHODID_GETALLUSERS)))
                    .addMethod(
                            METHOD_NEW_REPORTS,
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest,
                                            uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse>(
                                            this, METHODID_NEW_REPORTS)))
                    .build();
        }
    }

    /**
     */
    public static final class WorkStressServiceStub extends io.grpc.stub.AbstractStub<WorkStressServiceStub> {
        private WorkStressServiceStub(io.grpc.Channel channel) {
            super(channel);
        }

        private WorkStressServiceStub(io.grpc.Channel channel,
                                      io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected WorkStressServiceStub build(io.grpc.Channel channel,
                                              io.grpc.CallOptions callOptions) {
            return new WorkStressServiceStub(channel, callOptions);
        }

        /**
         */
        public void newheartrates(uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest request,
                                  io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(METHOD_NEWHEARTRATES, getCallOptions()), request, responseObserver);
        }

        /**
         */
        public void outoftime(uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest request,
                              io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(METHOD_OUTOFTIME, getCallOptions()), request, responseObserver);
        }

        /**
         */
        public void getallusers(uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest request,
                                io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(METHOD_GETALLUSERS, getCallOptions()), request, responseObserver);
        }

        /**
         */
        public void newReports(uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest request,
                               io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(METHOD_NEW_REPORTS, getCallOptions()), request, responseObserver);
        }
    }

    /**
     */
    public static final class WorkStressServiceBlockingStub extends io.grpc.stub.AbstractStub<WorkStressServiceBlockingStub> {
        private WorkStressServiceBlockingStub(io.grpc.Channel channel) {
            super(channel);
        }

        private WorkStressServiceBlockingStub(io.grpc.Channel channel,
                                              io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected WorkStressServiceBlockingStub build(io.grpc.Channel channel,
                                                      io.grpc.CallOptions callOptions) {
            return new WorkStressServiceBlockingStub(channel, callOptions);
        }

        /**
         */
        public uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse newheartrates(uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest request) {
            return blockingUnaryCall(
                    getChannel(), METHOD_NEWHEARTRATES, getCallOptions(), request);
        }

        /**
         */
        public uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse outoftime(uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest request) {
            return blockingUnaryCall(
                    getChannel(), METHOD_OUTOFTIME, getCallOptions(), request);
        }

        /**
         */
        public uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse getallusers(uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest request) {
            return blockingUnaryCall(
                    getChannel(), METHOD_GETALLUSERS, getCallOptions(), request);
        }

        /**
         */
        public uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse newReports(uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest request) {
            return blockingUnaryCall(
                    getChannel(), METHOD_NEW_REPORTS, getCallOptions(), request);
        }
    }

    /**
     */
    public static final class WorkStressServiceFutureStub extends io.grpc.stub.AbstractStub<WorkStressServiceFutureStub> {
        private WorkStressServiceFutureStub(io.grpc.Channel channel) {
            super(channel);
        }

        private WorkStressServiceFutureStub(io.grpc.Channel channel,
                                            io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected WorkStressServiceFutureStub build(io.grpc.Channel channel,
                                                    io.grpc.CallOptions callOptions) {
            return new WorkStressServiceFutureStub(channel, callOptions);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> newheartrates(
                uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest request) {
            return futureUnaryCall(
                    getChannel().newCall(METHOD_NEWHEARTRATES, getCallOptions()), request);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> outoftime(
                uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest request) {
            return futureUnaryCall(
                    getChannel().newCall(METHOD_OUTOFTIME, getCallOptions()), request);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse> getallusers(
                uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest request) {
            return futureUnaryCall(
                    getChannel().newCall(METHOD_GETALLUSERS, getCallOptions()), request);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse> newReports(
                uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest request) {
            return futureUnaryCall(
                    getChannel().newCall(METHOD_NEW_REPORTS, getCallOptions()), request);
        }
    }

    private static final int METHODID_NEWHEARTRATES = 0;
    private static final int METHODID_OUTOFTIME = 1;
    private static final int METHODID_GETALLUSERS = 2;
    private static final int METHODID_NEW_REPORTS = 3;

    private static class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final WorkStressServiceImplBase serviceImpl;
        private final int methodId;

        public MethodHandlers(WorkStressServiceImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_NEWHEARTRATES:
                    serviceImpl.newheartrates((uk.ac.mdx.cs.ie.workstress.proto.HeartRatesRequest) request,
                            (io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse>) responseObserver);
                    break;
                case METHODID_OUTOFTIME:
                    serviceImpl.outoftime((uk.ac.mdx.cs.ie.workstress.proto.RanOutOfTimeRequest) request,
                            (io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse>) responseObserver);
                    break;
                case METHODID_GETALLUSERS:
                    serviceImpl.getallusers((uk.ac.mdx.cs.ie.workstress.proto.AllUsersRequest) request,
                            (io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.AllUsersResponse>) responseObserver);
                    break;
                case METHODID_NEW_REPORTS:
                    serviceImpl.newReports((uk.ac.mdx.cs.ie.workstress.proto.StressReportsRequest) request,
                            (io.grpc.stub.StreamObserver<uk.ac.mdx.cs.ie.workstress.proto.ServiceResponse>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(
                io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        return new io.grpc.ServiceDescriptor(SERVICE_NAME,
                METHOD_NEWHEARTRATES,
                METHOD_OUTOFTIME,
                METHOD_GETALLUSERS,
                METHOD_NEW_REPORTS);
    }

}
