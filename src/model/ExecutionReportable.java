package model;

public interface ExecutionReportable extends Comparable<ExecutionReportable> {

    String getReport();

    int getPriority();

    boolean isFatal();

    int compareTo(ExecutionReportable o);
}
