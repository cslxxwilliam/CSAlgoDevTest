# Exchange Orders Matching Engine
## Description
**Matching Engine** (<code>matchingEngine-1.0.jar</code>) is a Java application serving as financial exchange orders matching engine. The algorithm is based on the price/time priority.

## Requirements and Dependencies
Matching Engine requires Java 14.

It is a Maven project (see pom.xml, file Apache Maven 3.8.1 is recommended) and depends on:
* JUnit 4.13.2

All dependencies are downloaded from Internet when you run <code>mvn clean install</code>.

## Building
Build the application using:

<code>mvn clean install</code>

Resulting matchingEngine-1.0.jar JAR file will be created in <code>target</code> sub-folder.

This JAR file is a standalone JAR that contains all dependencies required to run the application.

## Running
Start the application using:

<code>java -jar target/matchingEngine-1.0.jar</code>

The orders from standard input end by 2 new lines, e.g.
```
Welcome to Matching Engine!
Usage: 2 newlines to end your orders
#OrderID,Symbol,Price,Side,OrderQuantity
Order1,0700.HK,610,Sell,20000
Order2,0700.HK,MKT,Sell,10000
Order3,0700.HK,610,Buy,10000

#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity
Ack,Order1,0700.HK,610,Sell,20000
Ack,Order2,0700.HK,MKT,Sell,10000
Ack,Order3,0700.HK,610,Buy,10000
Fill,Order2,0700.HK,MKT,Sell,10000,610,10000
Fill,Order3,0700.HK,610,Buy,10000,610,10000
```

## Testing
Run the test using:

<code>mvn clean test</code>

## Latency Measuring
Using Java Microbenchmark Harness(JMH) for latency measuring: https://github.com/openjdk/jmh

**Run the test using:**

<code>mvn clean install</code>
<code>mvn exec:exec@run-benchmarks -Dmaven.exec.skip=false</code>

The measurement takes a simple sample input
* Iterations of measurements: 5
* Warmup: 5 times
* Iterations of order matching: 1 million

**Sample input:**
```
#OrderID,Symbol,Price,Side,OrderQuantity
Order1,0700.HK,610,Sell,20000
Order2,0700.HK,MKT,Sell,10000
Order3,0700.HK,610,Buy,10000
```

**Average latency per run**
```
14.05 microseconds
```

**Latency measurements**
```
# Run progress: 0.00% complete, ETA 00:01:40
# Fork: 1 of 1
# Warmup Iteration   1: 16136754.131 us/op
# Warmup Iteration   2: 12492521.254 us/op
# Warmup Iteration   3: 12089602.040 us/op
# Warmup Iteration   4: 14232004.541 us/op
# Warmup Iteration   5: 12106411.616 us/op
Iteration   1: 13094877.553 us/op
Iteration   2: 15746059.914 us/op
Iteration   3: 12068368.882 us/op
Iteration   4: 17046910.812 us/op
Iteration   5: 12296209.842 us/op

Benchmark             (iterations)  Mode  Cnt         Score         Error  Units
EngineRunner.measure       1000000  avgt    5  14050485.401 ± 8561297.197  us/op
```

