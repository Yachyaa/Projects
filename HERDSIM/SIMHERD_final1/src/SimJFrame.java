/*
Yachyaa Toefy (TFYYAC001)
Luke Clayton (CLYLUK001)
Fabio O'ryan Paulo (ORYFAB001)

Frame.java
This classes is responsible for initializing the frame
which includes setting frame info (size, title), setting the ON-EXIT behaviour as well
as splitting the panes
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SimJFrame extends javax.swing.JFrame {
    private SimulationPanel simulationPanel;
    private SettingsPanel settingsPanel;
    private HorizontalPanel horizontalPanel;
    private JScrollPane settingsScroll;
    private JScrollPane simulationScroll;


    public SimJFrame() {
        initialize();
        setFrameInfo();
        // what to do when simulation ends
        Runtime.getRuntime().addShutdownHook(new Thread(this::handleShutdown));

    }

    ////////////////////////////////// Set Frame Info //////////////////////////////////////////////////////////////////


    public void initialize(){
        // instantiate instance variables (All panels used)
        settingsPanel = new SettingsPanel();
        horizontalPanel = new HorizontalPanel();
        simulationPanel = new SimulationPanel(settingsPanel, horizontalPanel);
        settingsScroll = new JScrollPane(settingsPanel);
        simulationScroll = new JScrollPane(simulationPanel);

        // Divide the pane into 2 panels for simulationPanel and settingsPanel
        splitPane();

        setVisible(true);
    }


    public void setFrameInfo(){
        setTitle("SimHerd"); // Title at top of frame
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setDynamicFrameSize();
        setResizable(false);
        setLocationRelativeTo(null);
    }


    ///////////////////////////////////////////// Helper Methods ///////////////////////////////////////////////////////


    public void splitPane(){
        // Divide pane into 2 panels
        this.setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, simulationScroll, settingsScroll);
        splitPane.setResizeWeight(0.99); // Adjust the division between panels

        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(TerrainLoader.getTerrainDimx());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(horizontalPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    public void setDynamicFrameSize(){
        int settingPanelWidth = 200;
        int settingPanelBoarder = 50;
        int horizontalPanelHeight = 105;
        System.out.println("horizontalPanelHeight: " + horizontalPanelHeight );
        setSize(TerrainLoader.getTerrainDimx() + settingPanelWidth + settingPanelBoarder, TerrainLoader.getTerrainDimY() + horizontalPanelHeight); // Frame Size
    }


    public void handleShutdown() {
        simulationPanel.writeObstacles();
    }



    //////////////////////////////////////////// Getters and Setters //////////////////////////////////////////////////



    public SettingsPanel getSettingsPanel() {
        return settingsPanel;
    }

    public SimulationPanel getSimulationPanel() {
        return simulationPanel;
    }

    public void setSettingsPanel(SettingsPanel settingsPanel) {
        this.settingsPanel = settingsPanel;
    }

    public void setSimulationPanel(SimulationPanel simulationPanel) {
        this.simulationPanel = simulationPanel;
    }


} // class
