import models.MatchingEngine;
import models.MatchingEngineApp;
import models.Order;
import models.OrderCreator;

import java.io.*;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        MatchingEngineApp matchingEngineApp = new MatchingEngineApp();
        System.out.println("Welcome to Matching Engine!\nUsage: 2 newlines to end your orders");

        Scanner stdin = new Scanner(new BufferedInputStream(System.in));
        stdin.useDelimiter("\\R");
        while(true){

            String inputOrders = "";
            String line="";
            while(!(line = stdin.nextLine()).isEmpty()) {
                inputOrders = inputOrders.concat(line).concat("\n");
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