package latency;

import controller.MatchingEngineApp;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ExecutionPlanComplex {
    public MatchingEngineApp app;
    public String input;

    @Param({"1"})
    public int iterations;

    public ExecutionPlanComplex() {

        this.app = new MatchingEngineApp();
        this.input = "#OrderID,Symbol,Price,Side,OrderQuantity\n";

        for (int i = 0; i < 10000; i++) {
            input = input + "Order" + i + ",0700.HK,610,Sell,1\n";
        }

        input = input + "Order1,0700.HK,610,Sell,10000\n";
    }

}
