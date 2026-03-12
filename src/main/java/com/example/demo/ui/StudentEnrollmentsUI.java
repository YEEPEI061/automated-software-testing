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

public class StudentEnrollmentsUI {

    private JFrame frame;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTable table;
    private DefaultTableModel tableModel;

    public StudentEnrollmentsUI() {

        frame = new JFrame("Retrieve Student Enrollments");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // MAIN PANEL with margin
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(mainPanel);

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(new Color(245, 245, 245));

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(firstNameLabel.getFont().deriveFont(14f));
        topPanel.add(firstNameLabel);

        firstNameField = new JTextField(12);
        firstNameField.setFont(firstNameField.getFont().deriveFont(14f));
        topPanel.add(firstNameField);
        firstNameField.addActionListener(e -> fetchEnrollments());

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(lastNameLabel.getFont().deriveFont(14f));
        topPanel.add(lastNameLabel);

        lastNameField = new JTextField(12);
        lastNameField.setFont(lastNameField.getFont().deriveFont(14f));
        topPanel.add(lastNameField);
        lastNameField.addActionListener(e -> fetchEnrollments());

        JButton getButton = new JButton("Search 🔍");
        getButton.setFont(getButton.getFont().deriveFont(14f));

        JButton clearButton = new JButton("Clear ❌");
        clearButton.setFont(clearButton.getFont().deriveFont(14f));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(getButton);
        buttonPanel.add(clearButton);

        topPanel.add(buttonPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{"Course Name", "Instructor"}, 0);
        table = new JTable(tableModel);
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

        // Scroll pane with preferred size
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        getButton.addActionListener(e -> fetchEnrollments());

        clearButton.addActionListener(e -> {
            firstNameField.setText("");
            lastNameField.setText("");
            tableModel.setRowCount(0);
        });

        // Auto size window correctly
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void fetchEnrollments() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both first name and last name!");
            return;
        }

        try {
            // Encode spaces
            String encodedFirstName = URLEncoder.encode(firstName, StandardCharsets.UTF_8);
            String encodedLastName = URLEncoder.encode(lastName, StandardCharsets.UTF_8);
            URL url = new URL("http://localhost:8080/students/search?firstName="
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

                // Clear previous table data
                tableModel.setRowCount(0);

                // Parse JSON using Jackson
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
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to retrieve data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentEnrollmentsUI::new);
    }
}
