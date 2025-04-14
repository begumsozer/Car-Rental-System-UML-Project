import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.sql.SQLException;

public class CarRentalApp {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JComboBox<String> customerComboBox;
    private JComboBox<String> carTypeComboBox;
    private JComboBox<String> carModelComboBox;
    private JTable rentalsTable;
    private DefaultTableModel tableModel;
    private DefaultTableModel customerTableModel;
    private DefaultTableModel carTableModel;

    public CarRentalApp() {
        mainFrame = new JFrame("Car Rental App");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Customer", createAddCustomerPanel());
        tabbedPane.addTab("Add Car", createAddCarPanel());
        tabbedPane.addTab("Create Rental", createCreateRentalPanel());
        tabbedPane.addTab("View Rentals", createViewRentalsPanel());
        tabbedPane.addTab("View Customers", createViewCustomersPanel());
        tabbedPane.addTab("View Cars", createViewCarsPanel());

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex == 2) { // Create Rental tab
                    updateCustomerComboBox();
                    updateCarTypeComboBox();
                    updateCarModelComboBox();
                } else if (selectedIndex == 3) { // View Rentals tab
                    updateRentalsTable();
                } else if (selectedIndex == 4) { // View Customers tab
                    updateCustomersTable();
                } else if (selectedIndex == 5) { // View Cars tab
                    updateCarsTable();
                }
            }
        });

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    private JPanel createAddCustomerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel contactLabel = new JLabel("Contact Details:");
        JTextField contactField = new JTextField();
        JButton addButton = new JButton("Add Customer");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String contactDetails = contactField.getText();
                try {
                    Customer customer = new Customer((int) (Math.random() * 1000), name, contactDetails);
                    customer.save();
                    JOptionPane.showMessageDialog(panel, "Customer added successfully!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error adding customer: " + ex.getMessage());
                }
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(contactLabel);
        panel.add(contactField);
        panel.add(new JLabel());
        panel.add(addButton);

        return panel;
    }

    private JPanel createAddCarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"EconomyCar", "SUV", "LuxurySUV", "Minivan", "EV"});
        JLabel modelLabel = new JLabel("Model:");
        JTextField modelField = new JTextField();
        JLabel dailyPriceLabel = new JLabel("Daily Price:");
        JTextField dailyPriceField = new JTextField();
        JButton addButton = new JButton("Confirm");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String model = modelField.getText();
                int categoryId = categoryComboBox.getSelectedIndex() + 1;
                double dailyPrice = Double.parseDouble(dailyPriceField.getText());
                try {
                    Car car = new Car((int) (Math.random() * 1000), model, categoryId, dailyPrice);
                    car.save();
                    JOptionPane.showMessageDialog(panel, "Car added successfully!");
                    updateCarModelComboBox(); // Update car model combo box
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error adding car: " + ex.getMessage());
                }
            }
        });

        panel.add(categoryLabel);
        panel.add(categoryComboBox);
        panel.add(modelLabel);
        panel.add(modelField);
        panel.add(dailyPriceLabel);
        panel.add(dailyPriceField);
        panel.add(new JLabel());
        panel.add(addButton);

        return panel;
    }

    private JPanel createCreateRentalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2));

        JLabel customerIdLabel = new JLabel("Customer:");
        customerComboBox = new JComboBox<>();
        JLabel carTypeLabel = new JLabel("Car Type:");
        carTypeComboBox = new JComboBox<>(new String[]{"EconomyCar", "SUV", "LuxurySUV", "Minivan", "EV"});
        JLabel carModelLabel = new JLabel("Car Model:");
        carModelComboBox = new JComboBox<>();
        JLabel startDateLabel = new JLabel("Start Date (yyyy-MM-dd):");
        JTextField startDateField = new JTextField();
        JLabel endDateLabel = new JLabel("End Date (yyyy-MM-dd):");
        JTextField endDateField = new JTextField();
        JLabel dailyPriceLabel = new JLabel("Daily Price:");
        JTextField dailyPriceField = new JTextField();
        dailyPriceField.setEditable(false);
        JButton createButton = new JButton("Create Rental");

        carTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCarModelComboBox();
            }
        });

        carModelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedModel = (String) carModelComboBox.getSelectedItem();
                if (selectedModel != null) {
                    try {
                        Car car = Car.findByModel(selectedModel);
                        if (car != null) {
                            dailyPriceField.setText(String.valueOf(car.getDailyPrice()));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCustomer = (String) customerComboBox.getSelectedItem();
                int customerId = Integer.parseInt(selectedCustomer.split("ID: ")[1].replace(")", ""));
                String selectedModel = (String) carModelComboBox.getSelectedItem();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date startDate = dateFormat.parse(startDateField.getText());
                    Date endDate = dateFormat.parse(endDateField.getText());
                    double dailyPrice = Double.parseDouble(dailyPriceField.getText());
                    int rentalDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
                    double totalRentalPrice = rentalDays * dailyPrice;

                    int response = JOptionPane.showConfirmDialog(panel, "Customer ID: " + customerId + "\nCar Model: " + selectedModel + "\nStart Date: " + startDateField.getText() + "\nEnd Date: " + endDateField.getText() + "\nDaily Price: " + dailyPrice + "\nTotal Rental Price: " + totalRentalPrice + "\nDo you want to proceed?", "Confirm Rental", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        Car car = Car.findByModel(selectedModel);
                        Rental rental = new Rental((int) (Math.random() * 1000), startDate, endDate, totalRentalPrice, dailyPrice, customerId, car.getId());
                        rental.save();
                        Car.writeToTextFile("cars.txt", Car.readFromTextFile("cars.txt"));
                        JOptionPane.showMessageDialog(panel, "Rental created successfully!");
                        updateRentalsTable(); // Update rentals display
                        updateCarsTable(); // Update cars display
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid date format. Please use yyyy-MM-dd.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error creating rental: " + ex.getMessage());
                }
            }
        });

        panel.add(customerIdLabel);
        panel.add(customerComboBox);
        panel.add(carTypeLabel);
        panel.add(carTypeComboBox);
        panel.add(carModelLabel);
        panel.add(carModelComboBox);
        panel.add(startDateLabel);
        panel.add(startDateField);
        panel.add(endDateLabel);
        panel.add(endDateField);
        panel.add(dailyPriceLabel);
        panel.add(dailyPriceField);
        panel.add(new JLabel());
        panel.add(createButton);

        return panel;
    }

    private JPanel createViewRentalsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[] columnNames = {"Rental ID", "Customer", "Contact", "Car Model", "Start Date", "End Date", "Rental Price", "Daily Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        rentalsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(rentalsTable);

        JButton endRentButton = new JButton("End Rent");
        endRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = rentalsTable.getSelectedRow();
                if (selectedRow != -1) {
                    int rentalId = (int) tableModel.getValueAt(selectedRow, 0);
                    double rentalPrice = (double) tableModel.getValueAt(selectedRow, 6);
                    int carId = getCarIdByRentalId(rentalId);
                    int response = JOptionPane.showConfirmDialog(null, "Was the rental returned late?", "End Rent", JOptionPane.YES_NO_OPTION);
                    double finalPrice = rentalPrice;
                    if (response == JOptionPane.YES_OPTION) {
                        finalPrice *= 1.3; // Increase price by 30% if late
                    }
                    JOptionPane.showMessageDialog(null, "Final rental price: " + finalPrice);
                    removeRentalFromFile(rentalId);
                    updateCarStatus(carId, "Available");
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a rental to end.");
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(endRentButton, BorderLayout.SOUTH);
        return panel;

    }

    private int getCarIdByRentalId(int rentalId) {
        try {
            List<Rental> rentals = Rental.readFromTextFile("rentals.txt");
            for (Rental rental : rentals) {
                if (rental.getId() == rentalId) {
                    return rental.getCarId();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private void removeRentalFromFile(int rentalId) {
        try {
            List<Rental> rentals = Rental.readFromTextFile("rentals.txt");
            rentals.removeIf(rental -> rental.getId() == rentalId);
            Rental.writeToTextFile("rentals.txt", rentals);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateCarStatus(int carId, String status) {
        try {
            List<Car> cars = Car.readFromTextFile("cars.txt");
            Car.writeToTextFile("cars.txt", cars);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void removeCustomerFromFile(int customerId) {
        try {
            List<Customer> customers = Customer.readFromTextFile("customers.txt");
            customers.removeIf(customer -> customer.getId() == customerId);
            Customer.writeToTextFile("customers.txt", customers);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void removeCarFromFile(int carId) {
        try {
            List<Car> cars = Car.readFromTextFile("cars.txt");
            cars.removeIf(car -> car.getId() == carId);
            Car.writeToTextFile("cars.txt", cars);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateCustomerComboBox() {
        customerComboBox.removeAllItems();
        try {
            List<Customer> customers = Customer.readFromTextFile("customers.txt");
            for (Customer customer : customers) {
                customerComboBox.addItem(customer.getName() + " (ID: " + customer.getId() + ")");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateCarTypeComboBox() {
        carTypeComboBox.removeAllItems();
        carTypeComboBox.addItem("EconomyCar");
        carTypeComboBox.addItem("SUV");
        carTypeComboBox.addItem("LuxurySUV");
        carTypeComboBox.addItem("Minivan");
        carTypeComboBox.addItem("EV");
    }

    private void updateCarModelComboBox() {
        String selectedType = (String) carTypeComboBox.getSelectedItem();
        carModelComboBox.removeAllItems();
        try {
            List<Car> cars = Car.readFromTextFile("cars.txt");
            List<Rental> rentals = Rental.readFromTextFile("rentals.txt");
            for (Car car : cars) {
                boolean isRented = false;
                for (Rental rental : rentals) {
                    if (rental.getCarId() == car.getId()) {
                        isRented = true;
                        break;
                    }
                }
                if (!isRented && car.getCategory().equals(selectedType)) {
                    carModelComboBox.addItem(car.getModel());
                }
            }
            if (carModelComboBox.getItemCount() > 0) {
                carModelComboBox.setSelectedIndex(0);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateRentalsTable() {
        try {
            List<Rental> rentals = Rental.readFromTextFile("rentals.txt");
            List<Car> cars = Car.readFromTextFile("cars.txt");
            List<Customer> customers = Customer.readFromTextFile("customers.txt");
            tableModel.setRowCount(0); // Clear existing rows
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Rental rental : rentals) {
                String carModel = cars.stream()
                        .filter(car -> car.getId() == rental.getCarId())
                        .findFirst()
                        .map(Car::getModel)
                        .orElse("Unknown");
                Customer customer = customers.stream()
                        .filter(c -> c.getId() == rental.getCustomerId())
                        .findFirst()
                        .orElse(null);
                String customerName = customer != null ? customer.getName() : "Unknown";
                String customerContact = customer != null ? customer.getContactDetails() : "Unknown";

                tableModel.addRow(new Object[]{
                        rental.getId(),
                        customerName,
                        customerContact,
                        carModel,
                        dateFormat.format(rental.getStartDate()),
                        dateFormat.format(rental.getEndDate()),
                        rental.getRentalPrice(),
                        rental.getDailyPrice()
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateCustomersTable() {
        try {
            List<Customer> customers = Customer.readFromTextFile("customers.txt");
            customerTableModel.setRowCount(0); // Clear existing rows

            for (Customer customer : customers) {
                customerTableModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getContactDetails()
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateCarsTable() {
        try {
            List<Car> cars = Car.readFromTextFile("cars.txt");
            carTableModel.setRowCount(0); // Clear existing rows

            for (Car car : cars) {
                carTableModel.addRow(new Object[]{
                        car.getId(),
                        car.getModel(),
                        car.getCategory(),
                        car.getDailyPrice()
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean hasActiveRentals(int id, String type) {
        try {
            List<Rental> rentals = Rental.readFromTextFile("rentals.txt");
            for (Rental rental : rentals) {
                if ((type.equals("customer") && rental.getCustomerId() == id) ||
                        (type.equals("car") && rental.getCarId() == id)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private JPanel createViewCustomersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[] columnNames = {"Customer ID", "Name", "Contact"};
        customerTableModel = new DefaultTableModel(columnNames, 0);
        JTable customerTable = new JTable(customerTableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);

        JButton deleteCustomerButton = new JButton("Delete Customer");
        deleteCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
                    if (hasActiveRentals(customerId, "customer")) {
                        JOptionPane.showMessageDialog(null, "Cannot delete customer with active rentals.");
                    } else {
                        removeCustomerFromFile(customerId);
                        customerTableModel.removeRow(selectedRow);
                        updateCustomerComboBox();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a customer to delete.");
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(deleteCustomerButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createViewCarsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[] columnNames = {"Car ID", "Model", "Category", "Daily Price"};
        carTableModel = new DefaultTableModel(columnNames, 0);
        JTable carTable = new JTable(carTableModel);
        JScrollPane scrollPane = new JScrollPane(carTable);

        JButton deleteCarButton = new JButton("Delete Car");
        deleteCarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = carTable.getSelectedRow();
                if (selectedRow != -1) {
                    int carId = (int) carTableModel.getValueAt(selectedRow, 0);
                    if (hasActiveRentals(carId, "car")) {
                        JOptionPane.showMessageDialog(null, "Cannot delete car with active rentals.");
                    } else {
                        removeCarFromFile(carId);
                        carTableModel.removeRow(selectedRow);
                        updateCarModelComboBox();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a car to delete.");
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(deleteCarButton, BorderLayout.SOUTH);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarRentalApp::new);
    }
}