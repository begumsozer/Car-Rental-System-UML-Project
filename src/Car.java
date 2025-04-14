import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Car {
    private int id;
    private String model;
    private int categoryId;
    private double dailyPrice;

    public Car(int id, String model, int categoryId, double dailyPrice) {
        this.id = id;
        this.model = model;
        this.categoryId = categoryId;
        this.dailyPrice = dailyPrice;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public String getCategory() {
        switch (categoryId) {
            case 1: return "EconomyCar";
            case 2: return "SUV";
            case 3: return "LuxurySUV";
            case 4: return "Minivan";
            case 5: return "EV";
            default: return "Unknown";
        }
    }

    public static List<Car> readFromTextFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        List<Car> cars = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    throw new IOException("Invalid data format in file: " + filePath);
                }
                try {
                    int id = Integer.parseInt(parts[0]);
                    String model = parts[1];
                    int categoryId = Integer.parseInt(parts[2]);
                    double dailyPrice = Double.parseDouble(parts[3]);
                    cars.add(new Car(id, model, categoryId, dailyPrice));
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid number format in file: " + filePath, e);
                }
            }
        }
        return cars;
    }

    public void save() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cars.txt", true))) {
            writer.write(this.id + "," + this.model + "," + this.categoryId + "," + this.dailyPrice);
            writer.newLine();
        } catch (IOException e) {
            throw new IOException("Error writing to file: cars.txt", e);
        }
    }

    public static Car findByModel(String model) throws IOException {
        List<Car> cars = readFromTextFile("cars.txt");
        for (Car car : cars) {
            if (car.model.equals(model)) {
                return car;
            }
        }
        throw new IOException("Car model not found: " + model);
    }

    public static Car findById(int id) throws IOException {
        List<Car> cars = readFromTextFile("cars.txt");
        for (Car car : cars) {
            if (car.id == id) {
                return car;
            }
        }
        throw new IOException("Car ID not found: " + id);
    }

    public static void writeToTextFile(String fileName, List<Car> cars) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Car car : cars) {
                writer.write(car.getId() + "," + car.getModel() + "," + car.getCategoryId() + "," + car.getDailyPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Error writing to file: " + fileName, e);
        }
    }
}