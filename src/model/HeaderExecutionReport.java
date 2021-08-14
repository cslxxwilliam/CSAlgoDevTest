package model;

public class HeaderExecutionReport implements ExecutionReportable {
    public static final int HEADER_REPORT_PRIORITY = 0;
    private final String report;
    private final boolean isFatal;

    public HeaderExecutionReport(boolean isFatal, String report) {
        this.isFatal = isFatal;
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
    public boolean isFatal() {
        return this.isFatal;
    }

    @Override
    public int compareTo(ExecutionReportable o) {
        return 0;
    }
}
