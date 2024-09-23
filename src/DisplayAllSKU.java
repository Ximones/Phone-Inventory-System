import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Product {
    String SKU, Model, RAM, ROM, Color, Type;
    int Price;

    Product(String SKU, String Model, String RAM, String ROM, String Color, int Price, String Type) {
        this.SKU = SKU;
        this.Model = Model;
        this.RAM = RAM;
        this.ROM = ROM;
        this.Color = Color;
        this.Price = Price;
        this.Type = Type;
    }

    @Override
    public String toString() {
        return "SKU: " + SKU + ", Model: " + Model + ", RAM: " + RAM + ", ROM: " + ROM + ", Color: " + Color + ", Price: " + Price + ", Type: " + Type;
    }
}

public class DisplayAllSKU {
    private static JTable table;
    private static DefaultTableModel tableModel;
    private static Map<String, String> manufacturerFiles = new HashMap<>();
    private static JComboBox<String> typeFilterComboBox;

    private static JMenu manufacturerMenu;
    private static String currentManufacturer = "Apple";

    public DisplayAllSKU(Menu menu) {
        JFrame frame = new JFrame("Product Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("SKU");
        tableModel.addColumn("Model");
        tableModel.addColumn("RAM");
        tableModel.addColumn("ROM");
        tableModel.addColumn("Color");
        tableModel.addColumn("Price");
        tableModel.addColumn("Type");

        // Create table and add to frame
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        manufacturerMenu = new JMenu(currentManufacturer + " ↓");
        menuBar.add(manufacturerMenu);

        // Add manufacturer options to menu (replace with your actual file names)
        addManufacturerMenuItem(manufacturerMenu, "Apple", "aux_files/all_txt/Apple.txt");
        addManufacturerMenuItem(manufacturerMenu, "Samsung", "aux_files/all_txt/Samsung.txt");
        addManufacturerMenuItem(manufacturerMenu, "POCO", "aux_files/all_txt/POCO.txt");
        addManufacturerMenuItem(manufacturerMenu, "Nothing", "aux_files/all_txt/Nothing.txt");
        addManufacturerMenuItem(manufacturerMenu, "Xiaomi", "aux_files/all_txt/Xiaomi.txt");

        // Create type filter JComboBox
        typeFilterComboBox = new JComboBox<>();
        typeFilterComboBox.addItem("All");

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        // Load initial data
        loadProductsFromFile("aux_files/all_txt/Apple.txt");

        // Get unique types from productList after initial load
        Set<String> uniqueTypes = getUniqueTypes(readProductsFromFile("aux_files/all_txt/Apple.txt"));
        for (String type : uniqueTypes) {
            typeFilterComboBox.addItem(type);
        }

        typeFilterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) typeFilterComboBox.getSelectedItem();
                filterTableByType(selectedType);
            }
        });

        // Add JComboBox and search field to frame
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter by Type:"));
        filterPanel.add(typeFilterComboBox);

        JTextField searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText();
                filterTableBySearch(searchText);
            }
        });
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        frame.add(filterPanel, BorderLayout.NORTH);

        // Back button
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void addManufacturerMenuItem(JMenu menu, String manufacturerName, String fileName) {
        JMenuItem menuItem = new JMenuItem(manufacturerName);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProductsFromFile(fileName);
                currentManufacturer = manufacturerName;
                manufacturerMenu.setText(currentManufacturer + " ↓");

                // Update typeFilterComboBox with new types
                Set<String> uniqueTypes = getUniqueTypes(readProductsFromFile(fileName));

                ActionListener[] listeners = typeFilterComboBox.getActionListeners();
                for (ActionListener listener : listeners) {
                    typeFilterComboBox.removeActionListener(listener);
                }

                typeFilterComboBox.removeAllItems();
                typeFilterComboBox.addItem("All");
                for (String type : uniqueTypes) {
                    typeFilterComboBox.addItem(type);
                }

                typeFilterComboBox.setSelectedItem("All");

                for (ActionListener listener : listeners) {
                    typeFilterComboBox.addActionListener(listener);
                }

                filterTableByType("All");
            }
        });
        menu.add(menuItem);
        manufacturerFiles.put(manufacturerName, fileName);
    }


    private static void loadProductsFromFile(String fileName) {
        tableModel.setRowCount(0); // Clear existing data

        ArrayList<Product> productList = readProductsFromFile(fileName);
        for (Product product : productList) {
            tableModel.addRow(new Object[]{product.SKU, product.Model, product.RAM, product.ROM, product.Color, product.Price, product.Type});
        }
    }

    public static ArrayList<Product> readProductsFromFile(String fileName) {
        ArrayList<Product> productList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    Product product = new Product(parts[0], parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]), parts[6]);
                    productList.add(product);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return productList;
    }

    private static void filterTableByType(String selectedType) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        if (selectedType.equals("All")) {
            sorter.setRowFilter(null); // Show all rows
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(selectedType, 6)); // Filter by Type column (index 6)
        }
    }

    private static void filterTableBySearch(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null); // If search is empty, show all rows
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); // Case-insensitive search
        }
    }

    private static Set<String> getUniqueTypes(ArrayList<Product> productList) {
        Set<String> uniqueTypes = new HashSet<>();
        for (Product product : productList) {
            uniqueTypes.add(product.Type);
        }
        return uniqueTypes;
    }
}