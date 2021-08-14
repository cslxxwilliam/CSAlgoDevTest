package model;

import java.util.Comparator;

public class ExecutionReport  {
    private ReportType type;
    private Order order;
    private String report;
    private Fill fill;
    public ExecutionReport(ReportType type, Order order, Fill fill, String report) {
        this.fill = fill;
        this.type = type;
        this.order = order;
        this.report = report;
    }

    public ExecutionReport(ReportType type, String report) {
        this.type = type;
        this.report = report;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Fill getFill() {
        return this.fill;
    }

    //refactor for a better comparator
    public static Comparator<ExecutionReport> reportComparator = (r1, r2) -> {
        //price larger, the less
        if (r1.getType().compareTo(r2.getType()) == 0) {
            if (r1.getFill() == null || r2.getFill() == null) {
                return r1.getOrder().getSeq() - r2.getOrder().getSeq();
            }

            if (r1.getFill().getSeq() == r2.getFill().getSeq()) {
                return r1.getOrder().getSeq() - r2.getOrder().getSeq();
            }
            return r1.getFill().getSeq() - r2.getFill().getSeq();
        }
        //market order less than limit
        else {
            return r1.getType().compareTo(r2.getType());
        }
    };
}
