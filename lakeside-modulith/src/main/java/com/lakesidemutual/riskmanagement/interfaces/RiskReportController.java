package com.lakesidemutual.riskmanagement.interfaces;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lakesidemutual.riskmanagement.application.RiskProjectionStore;
import com.lakesidemutual.riskmanagement.application.RiskReportCsvService;

@RestController
public class RiskReportController {

  private final RiskProjectionStore store;
  private final RiskReportCsvService csvService;

  public RiskReportController(RiskProjectionStore store, RiskReportCsvService csvService) {
    this.store = store;
    this.csvService = csvService;
  }

  @GetMapping(value = "/risk/report", produces = "text/csv")
  public ResponseEntity<byte[]> getReportCsv() {

    String csv = csvService.generateCsv(store);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv")
        .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
        .body(csv.getBytes(StandardCharsets.UTF_8));
  }
}
