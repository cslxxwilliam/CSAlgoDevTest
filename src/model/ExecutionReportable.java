package model;

public interface ExecutionReportable extends Comparable<ExecutionReportable> {

    String getReport();

    int getPriority();

    int compareTo(ExecutionReportable o);
}
