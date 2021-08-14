package model;

public interface ExecutionReport extends Comparable<ExecutionReport> {

    ReportType getType();

    String getReport();

    int getPriority();

    int compareTo(ExecutionReport r2);
}
