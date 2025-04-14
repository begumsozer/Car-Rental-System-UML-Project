import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int id;
    private String name;
    private String contactDetails;

    public Customer(int id, String name, String contactDetails) {
        this.id = id;
        this.name = name;
        this.contactDetails = contactDetails;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public static List<Customer> readFromTextFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String contactDetails = parts[2];
                customers.add(new Customer(id, name, contactDetails));
            }
        }
        return customers;
    }

    public static void writeToTextFile(String filePath, List<Customer> customers) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Customer customer : customers) {
                writer.write(customer.id + "," + customer.name + "," + customer.contactDetails);
                writer.newLine();
            }
        }
    }

    public void save() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true))) {
            writer.write(this.id + "," + this.name + "," + this.contactDetails);
            writer.newLine();
        }
    }

    public static Customer findById(int id) throws IOException {
        List<Customer> customers = readFromTextFile("customers.txt");
        for (Customer customer : customers) {
            if (customer.id == id) {
                return customer;
            }
        }
        return null;
    }
}