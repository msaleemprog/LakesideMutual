package com.lakesidemutual.riskmanagement.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: riskmanagement.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RiskManagementGrpc {

  private RiskManagementGrpc() {}

  public static final java.lang.String SERVICE_NAME = "riskmanagement.RiskManagement";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.lakesidemutual.riskmanagement.grpc.TriggerRequest,
      com.lakesidemutual.riskmanagement.grpc.TriggerReply> getTriggerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Trigger",
      requestType = com.lakesidemutual.riskmanagement.grpc.TriggerRequest.class,
      responseType = com.lakesidemutual.riskmanagement.grpc.TriggerReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.lakesidemutual.riskmanagement.grpc.TriggerRequest,
      com.lakesidemutual.riskmanagement.grpc.TriggerReply> getTriggerMethod() {
    io.grpc.MethodDescriptor<com.lakesidemutual.riskmanagement.grpc.TriggerRequest, com.lakesidemutual.riskmanagement.grpc.TriggerReply> getTriggerMethod;
    if ((getTriggerMethod = RiskManagementGrpc.getTriggerMethod) == null) {
      synchronized (RiskManagementGrpc.class) {
        if ((getTriggerMethod = RiskManagementGrpc.getTriggerMethod) == null) {
          RiskManagementGrpc.getTriggerMethod = getTriggerMethod =
              io.grpc.MethodDescriptor.<com.lakesidemutual.riskmanagement.grpc.TriggerRequest, com.lakesidemutual.riskmanagement.grpc.TriggerReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Trigger"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lakesidemutual.riskmanagement.grpc.TriggerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.lakesidemutual.riskmanagement.grpc.TriggerReply.getDefaultInstance()))
              .setSchemaDescriptor(new RiskManagementMethodDescriptorSupplier("Trigger"))
              .build();
        }
      }
    }
    return getTriggerMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RiskManagementStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RiskManagementStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RiskManagementStub>() {
        @java.lang.Override
        public RiskManagementStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RiskManagementStub(channel, callOptions);
        }
      };
    return RiskManagementStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RiskManagementBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RiskManagementBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RiskManagementBlockingStub>() {
        @java.lang.Override
        public RiskManagementBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RiskManagementBlockingStub(channel, callOptions);
        }
      };
    return RiskManagementBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RiskManagementFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RiskManagementFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RiskManagementFutureStub>() {
        @java.lang.Override
        public RiskManagementFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RiskManagementFutureStub(channel, callOptions);
        }
      };
    return RiskManagementFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void trigger(com.lakesidemutual.riskmanagement.grpc.TriggerRequest request,
        io.grpc.stub.StreamObserver<com.lakesidemutual.riskmanagement.grpc.TriggerReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTriggerMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service RiskManagement.
   */
  public static abstract class RiskManagementImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return RiskManagementGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service RiskManagement.
   */
  public static final class RiskManagementStub
      extends io.grpc.stub.AbstractAsyncStub<RiskManagementStub> {
    private RiskManagementStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RiskManagementStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RiskManagementStub(channel, callOptions);
    }

    /**
     */
    public void trigger(com.lakesidemutual.riskmanagement.grpc.TriggerRequest request,
        io.grpc.stub.StreamObserver<com.lakesidemutual.riskmanagement.grpc.TriggerReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getTriggerMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service RiskManagement.
   */
  public static final class RiskManagementBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<RiskManagementBlockingStub> {
    private RiskManagementBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RiskManagementBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RiskManagementBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<com.lakesidemutual.riskmanagement.grpc.TriggerReply> trigger(
        com.lakesidemutual.riskmanagement.grpc.TriggerRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getTriggerMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service RiskManagement.
   */
  public static final class RiskManagementFutureStub
      extends io.grpc.stub.AbstractFutureStub<RiskManagementFutureStub> {
    private RiskManagementFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RiskManagementFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RiskManagementFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_TRIGGER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TRIGGER:
          serviceImpl.trigger((com.lakesidemutual.riskmanagement.grpc.TriggerRequest) request,
              (io.grpc.stub.StreamObserver<com.lakesidemutual.riskmanagement.grpc.TriggerReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getTriggerMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.lakesidemutual.riskmanagement.grpc.TriggerRequest,
              com.lakesidemutual.riskmanagement.grpc.TriggerReply>(
                service, METHODID_TRIGGER)))
        .build();
  }

  private static abstract class RiskManagementBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RiskManagementBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.lakesidemutual.riskmanagement.grpc.RiskManagementProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RiskManagement");
    }
  }

  private static final class RiskManagementFileDescriptorSupplier
      extends RiskManagementBaseDescriptorSupplier {
    RiskManagementFileDescriptorSupplier() {}
  }

  private static final class RiskManagementMethodDescriptorSupplier
      extends RiskManagementBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    RiskManagementMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RiskManagementGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RiskManagementFileDescriptorSupplier())
              .addMethod(getTriggerMethod())
              .build();
        }
      }
    }
    return result;
  }
}
