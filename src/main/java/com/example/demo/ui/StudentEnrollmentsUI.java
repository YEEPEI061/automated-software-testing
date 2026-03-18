package com.example.demo.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentEnrollmentsUI {

    private static final Logger logger = Logger.getLogger(StudentEnrollmentsUI.class.getName());

    private final JFrame frame;
    private final JComboBox<String> firstNameCombo;
    private final JComboBox<String> lastNameCombo;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public StudentEnrollmentsUI() {

        // Map of firstName -> list of lastNames
        Map<String, List<String>> studentMap = new HashMap<>();
        studentMap.put("John", Collections.singletonList("Low"));
        studentMap.put("Jasmine", Collections.singletonList("Davies"));
        studentMap.put("Alice", Arrays.asList("Thomas", "Evans"));

        frame = new JFrame("Retrieve Student Enrollments");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(mainPanel);

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(new Color(245, 245, 245));

        // First Name ComboBox
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(firstNameLabel.getFont().deriveFont(14f));
        topPanel.add(firstNameLabel);

        firstNameCombo = new JComboBox<>();
        firstNameCombo.setEditable(true);
        firstNameCombo.setFont(firstNameCombo.getFont().deriveFont(14f));
        firstNameCombo.setPreferredSize(new Dimension(150, 28));
        for (String firstName : studentMap.keySet()) {
            firstNameCombo.addItem(firstName);
        }
        firstNameCombo.setSelectedItem(null); // start empty
        topPanel.add(firstNameCombo);

        // Last Name ComboBox
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(lastNameLabel.getFont().deriveFont(14f));
        topPanel.add(lastNameLabel);

        lastNameCombo = new JComboBox<>();
        lastNameCombo.setEditable(true);
        lastNameCombo.setFont(lastNameCombo.getFont().deriveFont(14f));
        lastNameCombo.setPreferredSize(new Dimension(150, 28));
        lastNameCombo.setSelectedItem(null); // start empty
        topPanel.add(lastNameCombo);

        // Update last name options only when a first name is selected
        firstNameCombo.addActionListener(e -> {
            String selectedFirst = (String) firstNameCombo.getSelectedItem();
            lastNameCombo.removeAllItems();
            if (selectedFirst != null && studentMap.containsKey(selectedFirst)) {
                for (String lastName : studentMap.get(selectedFirst)) {
                    lastNameCombo.addItem(lastName);
                }
            }
            lastNameCombo.setSelectedItem(null);
        });

        // Buttons
        JButton getButton = new JButton("Search 🔍");
        getButton.setFont(getButton.getFont().deriveFont(14f));

        JButton clearButton = new JButton("Clear ❌");
        clearButton.setFont(clearButton.getFont().deriveFont(14f));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(getButton);
        buttonPanel.add(clearButton);
        topPanel.add(buttonPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{"Course Name", "Instructor"}, 0);
        table = new JTable(tableModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : new Color(235, 240, 245));
                }
                return c;
            }
        };
        table.setFont(table.getFont().deriveFont(14f));
        table.setRowHeight(28);

        // Table header style
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 14f));
        table.getTableHeader().setBackground(new Color(220, 220, 220));

        // Center table content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Center table header text
        DefaultTableCellRenderer headerRenderer =
                (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        getButton.addActionListener(e -> fetchEnrollments());
        clearButton.addActionListener(e -> {
            firstNameCombo.setSelectedItem(null);
            lastNameCombo.removeAllItems();
            tableModel.setRowCount(0);
        });

        // Press Enter to search
        firstNameCombo.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) fetchEnrollments();
            }
        });
        lastNameCombo.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) fetchEnrollments();
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void fetchEnrollments() {
        String firstName = (String) firstNameCombo.getSelectedItem();
        String lastName = (String) lastNameCombo.getSelectedItem();

        if (firstName == null || lastName == null || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both first name and last name!");
            return;
        }

        firstName = firstName.trim();
        lastName = lastName.trim();

        try {
            String encodedFirstName = URLEncoder.encode(firstName, StandardCharsets.UTF_8);
            String encodedLastName = URLEncoder.encode(lastName, StandardCharsets.UTF_8);
            URL url = new URL("http://localhost:8080/students/student-enrollment?firstName="
                    + encodedFirstName + "&lastName=" + encodedLastName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                tableModel.setRowCount(0);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode courses = mapper.readTree(response.toString());

                if (courses.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No enrollments found for \"" + firstName + " " + lastName + "\".");
                    return;
                }

                for (JsonNode course : courses) {
                    String courseName = course.get("name").asText();
                    JsonNode instructor = course.get("instructor");
                    String instructorName = instructor.get("firstName").asText() + " " + instructor.get("lastName").asText();
                    tableModel.addRow(new Object[]{courseName, instructorName});
                }

            } else if (responseCode == 404) {
                JOptionPane.showMessageDialog(frame, "Student \"" + firstName + " " + lastName + "\" not found.");
            } else {
                JOptionPane.showMessageDialog(frame, "Oops! Something went wrong. Please try again.");
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to retrieve enrollment data", ex);
            JOptionPane.showMessageDialog(frame, "Failed to retrieve data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentEnrollmentsUI::new);
    }
}
