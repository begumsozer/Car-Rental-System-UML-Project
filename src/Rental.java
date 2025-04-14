import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Rental {
    private int id;
    private Date startDate;
    private Date endDate;
    private double rentalPrice;
    private double dailyPrice;
    private int customerId;
    private int carId;

    public Rental(int id, Date startDate, Date endDate, double rentalPrice, double dailyPrice, int customerId, int carId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalPrice = rentalPrice;
        this.dailyPrice = dailyPrice;
        this.customerId = customerId;
        this.carId = carId;
    }

    public int getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getCarId() {
        return carId;
    }

    public void save() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("rentals.txt", true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            writer.write(this.id + "," + dateFormat.format(this.startDate) + "," + dateFormat.format(this.endDate) + "," + this.rentalPrice + "," + this.dailyPrice + "," + this.customerId + "," + this.carId);
            writer.newLine();
        }
    }

    public static List<Rental> readFromTextFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        List<Rental> rentals = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 7) {
                    throw new IOException("Invalid data format in file: " + filePath);
                }
                int id = Integer.parseInt(parts[0]);
                Date startDate = dateFormat.parse(parts[1]);
                Date endDate = dateFormat.parse(parts[2]);
                double rentalPrice = Double.parseDouble(parts[3]);
                double dailyPrice = Double.parseDouble(parts[4]);
                int customerId = Integer.parseInt(parts[5]);
                int carId = Integer.parseInt(parts[6]);
                rentals.add(new Rental(id, startDate, endDate, rentalPrice, dailyPrice, customerId, carId));
            }
        } catch (ParseException e) {
            throw new IOException("Invalid date format in file: " + filePath, e);
        }
        return rentals;
    }

    public static void writeToTextFile(String filePath, List<Rental> rentals) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Rental rental : rentals) {
                writer.write(rental.id + "," + dateFormat.format(rental.startDate) + "," + dateFormat.format(rental.endDate) + "," + rental.rentalPrice + "," + rental.dailyPrice + "," + rental.customerId + "," + rental.carId);
                writer.newLine();
            }
        }
    }
}