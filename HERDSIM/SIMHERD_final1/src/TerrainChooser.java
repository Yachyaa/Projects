import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TerrainChooser extends JPanel {
    private JButton smallFlat;
    private JButton largeFlat;
    private JButton small1;
    private JButton small2;
    private JButton small3;
    private JButton small4;
    private JButton large1;
    private JButton importElv;

    private String terrainChoice;
    private String elvPath;

    public TerrainChooser() {
        // Construct components
        smallFlat = new JButton("Small");
        largeFlat = new JButton("Large");
        small1 = new JButton("Small #1");
        small2 = new JButton("Small #2");
        small3 = new JButton("Small #3");
        small4 = new JButton("Small #4");
        large1 = new JButton("Large ");
        importElv = new JButton("Import .elv file");

        // Create headings
        JLabel flatTerrainHeading = new JLabel("Flat Terrain");
        JLabel elevatedTerrainHeading = new JLabel("Elevated Terrain");
        JLabel selectElevationFileHeading = new JLabel("Import Elevation File");

        // Set headings to be centered and bold
        flatTerrainHeading.setHorizontalAlignment(JLabel.CENTER);
        elevatedTerrainHeading.setHorizontalAlignment(JLabel.CENTER);
        selectElevationFileHeading.setHorizontalAlignment(JLabel.CENTER);
        Font boldFont = new Font("SansSerif", Font.BOLD, 16);
        flatTerrainHeading.setFont(boldFont);
        elevatedTerrainHeading.setFont(boldFont);
        selectElevationFileHeading.setFont(boldFont);

        // Adjust size and set layout
        setPreferredSize(new Dimension(667, 366));
        setLayout(new BorderLayout());

        // Create sub-panel for buttons and headings
        JPanel buttonPanel = new JPanel(new GridLayout(11, 1, 0, 5)); // Increased rows to accommodate headings and added vertical gap

        // Add components and headings to the sub-panel
        buttonPanel.add(flatTerrainHeading); // Add "Flat Terrain" heading
        buttonPanel.add(smallFlat);
        buttonPanel.add(largeFlat);

        buttonPanel.add(elevatedTerrainHeading); // Add "Elevated Terrain" heading
        buttonPanel.add(small1);
        buttonPanel.add(small2);
        buttonPanel.add(small3);
        buttonPanel.add(small4);
        buttonPanel.add(large1);
        buttonPanel.add(selectElevationFileHeading);
        buttonPanel.add(importElv);

        // Add the sub-panel to the main panel
        add(buttonPanel, BorderLayout.CENTER);

        // Set button widths
        Dimension buttonSize = new Dimension(200, 30); // Adjust the width as needed
        smallFlat.setPreferredSize(buttonSize);
        largeFlat.setPreferredSize(buttonSize);
        small1.setPreferredSize(buttonSize);
        small2.setPreferredSize(buttonSize);
        small3.setPreferredSize(buttonSize);
        small4.setPreferredSize(buttonSize);
        large1.setPreferredSize(buttonSize);
        importElv.setPreferredSize(buttonSize);


        // Add action listeners to the buttons
        smallFlat.addActionListener(new ButtonClickListener());
        largeFlat.addActionListener(new ButtonClickListener());
        small1.addActionListener(new ButtonClickListener());
        small2.addActionListener(new ButtonClickListener());
        small3.addActionListener(new ButtonClickListener());
        small4.addActionListener(new ButtonClickListener());
        large1.addActionListener(new ButtonClickListener());
        importElv.addActionListener(new ButtonClickListener());
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get the button's text (name)
            String buttonText = ((JButton) e.getSource()).getText();

            switch (buttonText) {
                case "Small":
                    terrainChoice = "smallFlat";
                    Simulation.setTerrainChoice(terrainChoice);
                    break;
                case "Large":
                    terrainChoice = "largeFlat";
                    Simulation.setTerrainChoice(terrainChoice);
                    break;
                case "Small #1":
                    terrainChoice = "small1";
                    Simulation.setTerrainChoice(terrainChoice);
                    break;
                case "Small #2":
                    terrainChoice = "small2";
                    Simulation.setTerrainChoice(terrainChoice);
                    break;
                case "Small #3":
                    terrainChoice = "small3";
                    Simulation.setTerrainChoice(terrainChoice);
                    break;
                case "Small #4":
                    terrainChoice = "small4";
                    Simulation.setTerrainChoice(terrainChoice);
                    break;
                case "Large ":
                    terrainChoice = "large1";
                    Simulation.setTerrainChoice(terrainChoice);
                    break;
                case "Import .elv file":
                    // Show a file chooser dialog for selecting an .elv file
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("ELV Files", "elv");
                    fileChooser.setFileFilter(filter);
                    int returnValue = fileChooser.showOpenDialog(TerrainChooser.this); // Pass the parent component

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        // Get the selected file
                        File selectedFile = fileChooser.getSelectedFile();

                        // Get the file path
                        String filePath = selectedFile.getAbsolutePath();
                        System.out.println("Selcted file path: "+ filePath);
                        // Set terrainChoice to the selected file's path
                        Simulation.setTerrainChoice("custom");
                        elvPath = filePath;
                        Simulation.setElvPath(elvPath);
                    } else {
                        elvPath = ""; // No file selected
                    }
                    break;
            }

        }
    }

}