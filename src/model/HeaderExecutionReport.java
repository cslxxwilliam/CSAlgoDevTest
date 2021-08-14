package model;

public class HeaderExecutionReport implements ExecutionReport {
    public static final int HEADER_REPORT_PRIORITY = 0;
    private String report;

    public HeaderExecutionReport(String report) {
        this.report = report;
    }

    @Override
    public ReportType getType() {
        return ReportType.Header;
    }

    @Override
    public String getReport() {
        return report;
    }

    @Override
    public int getPriority() {
        return HEADER_REPORT_PRIORITY;
    }

    @Override
    public int compareTo(ExecutionReport r2) {
        return 0;
    }
}
