/*
Yachyaa Toefy (TFYYAC001)
Luke Clayton (CLYLUK001)
Fabio O'Ryan Paulo (ORYFAB001)

SettingsPanel.java
This class is responsible for managing the second UI pane to the left of the screen (SettingsPanel).
Some of its functionality includes: creating sliders, buttons, radio buttons and text fields.
This class also manages all the listeners that the sliders and buttons require to function optimally.
This class is also responsible managing the grid used to implement the overall layout of the SettingsPanel


 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;


class SettingsPanel extends JPanel {

    private JSlider alignmentSlider;
    private JSlider separationSlider;
    private JSlider cohesionSlider;
    private JSlider herdSizeSlider;
    private JTextField alignmentTextbox;
    private JTextField separationTextbox;
    private JTextField cohesionTextbox;
    private JTextField herdSizeField;
    private JRadioButton treesRadioButton;
    private JRadioButton waterRadioButton;
    private JRadioButton herdAtWP;
    private JRadioButton raceToWP;
    private JRadioButton wrap;
    private JRadioButton reflect;
    private JButton resetButton;
    private JButton clearButton;

    public final int panelWidth = 200;
    public boolean clearClicked;
    public boolean animalsOnScreen;

    public SettingsPanel() {
        addPanelInfo();

        createSliders();
        createRadioButtons();
        createButtons();

        positionElements();
    }

    public void addPanelInfo(){
        setLayout(new BorderLayout());
        Dimension panelDims = new Dimension(panelWidth, TerrainLoader.getTerrainDimY());
        setClearClicked(false);

    }


    public void createSliders(){
        // Create sliders for Boid rules
        alignmentSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
        separationSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
        cohesionSlider = new JSlider(JSlider.HORIZONTAL, 0, 500, 10);
        herdSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 500, 5);

        // Create text fields for Boid rules
        alignmentTextbox = new JTextField(2);
        alignmentTextbox.setText(String.valueOf(alignmentSlider.getValue()));
        separationTextbox = new JTextField(2);
        separationTextbox.setText(String.valueOf(separationSlider.getValue()));
        cohesionTextbox = new JTextField(2);
        cohesionTextbox.setText(String.valueOf(cohesionSlider.getValue()));
        herdSizeField = new JTextField(2);
        herdSizeField.setText(String.valueOf(herdSizeSlider.getValue()));

        updateTextFieldsOnChange();
    }

    public void createRadioButtons(){
        // Create radio buttons for obstacles
        treesRadioButton = new JRadioButton("Trees");
        waterRadioButton = new JRadioButton("Water");
        ButtonGroup obstacleButtonGroup = new ButtonGroup();
        obstacleButtonGroup.add(treesRadioButton);
        obstacleButtonGroup.add(waterRadioButton);

        // Set default value for when the program started
        treesRadioButton.setSelected(true); // "Trees" radio button is selected by default
        waterRadioButton.setSelected(false);

        // Create radio buttons for way point behaviour
        herdAtWP = new JRadioButton("Herd at Waypoint");
        raceToWP = new JRadioButton("Race");
        ButtonGroup waypointReachedBehaviour = new ButtonGroup();
        waypointReachedBehaviour.add(herdAtWP);
        waypointReachedBehaviour.add(raceToWP);

        // Set default value for radio buttons
        raceToWP.setSelected(true); // "Trees" radio button is selected by default
        herdAtWP.setSelected(false);

        // Radio buttons for border behaviour
        wrap = new JRadioButton("Wrap");
        reflect = new JRadioButton("Reflect");
        ButtonGroup boarderBehaviour = new ButtonGroup();
        boarderBehaviour.add(wrap);
        boarderBehaviour.add(reflect);

        // Set default values for border behaviour when program is started
        wrap.setSelected(true);
        reflect.setSelected(false);

    }

    public void createButtons(){
        // Create clear button
        clearButton = new JButton("Restart simulation");
        clearButton.addActionListener(e -> {
            clearClicked = true; // Call the method in SimulationPanel to clear animals and obstacles
        });
    }

    public void updateTextFieldsOnChange() {
        // Listens for when slider is moved and updates the slider value.
        ActionListener updateListener = e -> {
            int value = Integer.parseInt(((JTextField) e.getSource()).getText());
            if (e.getSource() == alignmentTextbox) {
                alignmentSlider.setValue(value);
            } else if (e.getSource() == separationTextbox) {
                separationSlider.setValue(value);
            } else if (e.getSource() == cohesionTextbox) {
                cohesionSlider.setValue(value);
            } else if (e.getSource() == herdSizeField) {
                herdSizeSlider.setValue(value);
            }
        };
        alignmentTextbox.addActionListener(updateListener);
        separationTextbox.addActionListener(updateListener);
        cohesionTextbox.addActionListener(updateListener);
        herdSizeField.addActionListener(updateListener);

        // Add listeners to update text fields when sliders change
        ChangeListener sliderChangeListener = e -> {
            JSlider sourceSlider = (JSlider) e.getSource();
            if (sourceSlider == alignmentSlider) {
                alignmentTextbox.setText(String.valueOf(alignmentSlider.getValue()));
            } else if (sourceSlider == separationSlider) {
                separationTextbox.setText(String.valueOf(separationSlider.getValue()));
            } else if (sourceSlider == cohesionSlider) {
                cohesionTextbox.setText(String.valueOf(cohesionSlider.getValue()));
            } else if (sourceSlider == herdSizeSlider) {
                herdSizeField.setText(String.valueOf(herdSizeSlider.getValue()));
            }
        };

        alignmentSlider.addChangeListener(sliderChangeListener);
        separationSlider.addChangeListener(sliderChangeListener);
        cohesionSlider.addChangeListener(sliderChangeListener);
        herdSizeSlider.addChangeListener(sliderChangeListener);
    }

    public void positionElements(){
        // Create panels for sliders, text fields, reset button and clear button
        JPanel slidersPanel = new JPanel();
        slidersPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 0, 3, 10);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        slidersPanel.add(new JLabel("Alignment"), gbc);
        gbc.gridy = 1;
        slidersPanel.add(alignmentSlider, gbc);
        gbc.gridy = 2;
        slidersPanel.add(alignmentTextbox, gbc);

        gbc.gridy = 3;
        slidersPanel.add(new JLabel("Separation"), gbc);
        gbc.gridy = 4;
        slidersPanel.add(separationSlider, gbc);
        gbc.gridy = 5;
        slidersPanel.add(separationTextbox, gbc);

        gbc.gridy = 6;
        slidersPanel.add(new JLabel("Cohesion"), gbc);
        gbc.gridy = 7;
        slidersPanel.add(cohesionSlider, gbc);
        gbc.gridy = 8;
        slidersPanel.add(cohesionTextbox, gbc);

        gbc.gridy = 9;
        slidersPanel.add(new JLabel("Herd Spawn Size"), gbc);
        gbc.gridy = 10;
        slidersPanel.add(herdSizeSlider, gbc);
        gbc.gridy = 11;
        slidersPanel.add(herdSizeField, gbc);

        gbc.gridy = 12;
        slidersPanel.add(new JLabel("Obstacle Type"), gbc);
        gbc.gridy = 13;
        slidersPanel.add(treesRadioButton, gbc);
        gbc.gridy = 14;
        slidersPanel.add(waterRadioButton, gbc);

        gbc.gridy = 15;
        slidersPanel.add(new JLabel("Waypoint Behaviour"), gbc);
        gbc.gridy = 16;
        slidersPanel.add(herdAtWP, gbc);
        gbc.gridy = 17;
        slidersPanel.add(raceToWP, gbc);

        gbc.gridy = 18;
        slidersPanel.add(new JLabel("Barrier Behaviour"), gbc);
        gbc.gridy = 19;
        slidersPanel.add(wrap, gbc);
        gbc.gridy = 20;
        slidersPanel.add(reflect, gbc);


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BorderLayout());
        settingsPanel.add(slidersPanel, BorderLayout.CENTER);
        settingsPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(settingsPanel, BorderLayout.CENTER);
    }



    // GETTERS AND SETTERS to get value of sliders

    public int getCohesionValue() {
        return cohesionSlider.getValue();
    }

    public void setCohesionValue(int n){
        cohesionSlider.setValue(n);
    }

    public void setAlignmentValue(int n){
        alignmentSlider.setValue(n);
    }

    public void setSeparationValue(int n){
        separationSlider.setValue(n);
    }

    public void setHerdSizeSlider(int n){
        herdSizeSlider.setValue(n);
    }

    public int getSeparationValue() {
        return separationSlider.getValue();
    }

    public int getAlignmentValue() {
        return alignmentSlider.getValue();
    }

    public int getInitialHerdSizeValue() {
        return herdSizeSlider.getValue();
    }

        public String getObstacle() {
            if (treesRadioButton.isSelected()) {
                return "tree";
            } else if (waterRadioButton.isSelected()) {
                return "water";
            }
            return null;
        }

        public String getHerdBehaviour (){
            if (herdAtWP.isSelected()) {
                return "herdAtWP";
            } else if (raceToWP.isSelected()) {
                return "race";
            }

            return null;
        }

        public String getBarrierBehaviour(){
            if (wrap.isSelected())
                return "wrap";
            else if (reflect.isSelected())
                return "reflect";


            return null;
        }

        public void setClearClicked(boolean clearArrays) {
            this.clearClicked = clearArrays;
        }

        public boolean getClearClicked(){
            return clearClicked;
        }
}  // class
