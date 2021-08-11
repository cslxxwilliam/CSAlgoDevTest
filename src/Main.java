import models.MatchingEngine;
import models.Order;
import models.OrderCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        readHeader(reader);

        OrderCreator orderCreator = new OrderCreator();
        while(true){
            Order order = orderCreator.validateAndCreate(reader.readLine().split(","));

            MatchingEngine matchingEngine = new MatchingEngine();
            matchingEngine.addAndMatch(order);
        }
    }

    private static void readHeader(BufferedReader reader) throws IOException {

        // Reading data using readLine
        String name = reader.readLine();

        // Printing the read line
        System.out.println(name);
    }
}