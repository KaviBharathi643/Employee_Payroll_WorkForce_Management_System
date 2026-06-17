package com.company.payroll.dto.report;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ReportPdfData {

    private String reportType;
    private String generatedBy;
    private String dateRange;
    private List<String> headers;
    private List<List<String>> rows;
    @Builder.Default
    private Map<String, String> summary = new LinkedHashMap<>();
}
