package latency;

import controller.MatchingEngineApp;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ExecutionPlan {
    public MatchingEngineApp app;

    @Param({ "1000000" })
    public int iterations;

    public ExecutionPlan() {
        this.app = new MatchingEngineApp();
    }

}
