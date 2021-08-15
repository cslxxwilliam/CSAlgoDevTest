package latency;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class EngineRunner {
    @Benchmark
    @Fork(value =1)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @BenchmarkMode(Mode.AverageTime)
    public void measure(ExecutionPlan plan){
        for(int i=0;i<plan.iterations;i++) {
            plan.app.addInput("#OrderID,Symbol,Price,Side,OrderQuantity\n" +
                    "Order1,0700.HK,610,Sell,20000\nOrder2,0700.HK,610,Sell,10000\n" +
                    "Order3,0700.HK,610,Buy,10000\n\n");
        }
    }
}
