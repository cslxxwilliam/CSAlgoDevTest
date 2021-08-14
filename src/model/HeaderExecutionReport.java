package model;

public class HeaderExecutionReport implements ExecutionReportable {
    public static final int HEADER_REPORT_PRIORITY = 0;
    private String report;

    public HeaderExecutionReport(String report) {
        this.report = report;
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
    public int compareTo(ExecutionReportable o) {
        return 0;
    }
}
