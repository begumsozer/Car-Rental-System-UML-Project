// src/Main.java
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Read data from text files
            List<Customer> customers = Customer.readFromTextFile("customers.txt");
            List<Car> cars = Car.readFromTextFile("cars.txt");
            List<Rental> rentals = Rental.readFromTextFile("rentals.txt");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}