import models.MatchingEngine;
import models.MatchingEngineApp;
import models.Order;
import models.OrderCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));


        OrderCreator orderCreator = new OrderCreator();
        MatchingEngine matchingEngine = new MatchingEngine();

        //MatchingEngineApp
         //input string
         //output string

        MatchingEngineApp matchingEngineApp = new MatchingEngineApp();

//        Scanner scanner = new Scanner(System.in);

        while(true){

            System.out.println("Please input orders");
            String line = "";
            String inputOrders = "";
            while(
                    (line=reader.readLine())!=null
                    &&(!line.isEmpty())
            ) {
                inputOrders = inputOrders.concat(line);
            }
            String output = matchingEngineApp.addInput(inputOrders);

            //print output to stdout
            System.out.println(output);

//            matchingEngine.addAndMatch(order);

        }
    }

    private static void readHeader(BufferedReader reader) throws IOException {

        // Reading data using readLine
        String name = reader.readLine();

        // Printing the read line
        System.out.println(name);
    }
}