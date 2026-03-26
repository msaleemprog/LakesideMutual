package com.lakesidemutual.riskmanagement.interfaces.grpc;

import com.lakesidemutual.riskmanagement.application.RiskProjectionStore;
import com.lakesidemutual.riskmanagement.application.RiskProjectionSyncService;
import com.lakesidemutual.riskmanagement.application.RiskReportCsvService;
import com.lakesidemutual.riskmanagement.grpc.Report;
import com.lakesidemutual.riskmanagement.grpc.RiskManagementGrpc;
import com.lakesidemutual.riskmanagement.grpc.TriggerReply;
import com.lakesidemutual.riskmanagement.grpc.TriggerRequest;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class RiskManagementGrpcService extends RiskManagementGrpc.RiskManagementImplBase {

  private final RiskProjectionStore store;
  private final RiskReportCsvService csvService;
  private final RiskProjectionSyncService syncService;

  public RiskManagementGrpcService(
      RiskProjectionStore store,
      RiskReportCsvService csvService,
      RiskProjectionSyncService syncService) {
    this.store = store;
    this.csvService = csvService;
    this.syncService = syncService;
  }

  @Override
  public void trigger(TriggerRequest request, StreamObserver<TriggerReply> responseObserver) {

    // 1) smooth progress like upstream (client shows bar)
    for (int p = 1; p <= 10; p++) {
      responseObserver.onNext(TriggerReply.newBuilder().setProgress(p).build());
    }

    // 2) rebuild projection from DB so report is never empty after restart
    syncService.rebuildFromDatabase();

    for (int p = 11; p <= 80; p++) {
      responseObserver.onNext(TriggerReply.newBuilder().setProgress(p).build());
    }

    // 3) generate CSV from store
    String csv = csvService.generateCsv(store);

    // send report
    responseObserver.onNext(
        TriggerReply.newBuilder()
            .setReport(Report.newBuilder().setCsv(csv).build())
            .build()
    );

    // finish progress
    responseObserver.onNext(TriggerReply.newBuilder().setProgress(100).build());
    responseObserver.onCompleted();
  }
}
