/*
Yachyaa Toefy (TFYYAC001)
Luke Clayton (CLYLUK001)
Fabio O'Ryan Paulo (ORYFAB001)

Simulation.java
This is the main class responsible for running the program
 */


import javax.swing.*;
import java.awt.image.BufferedImage;

public class Simulation {
    private static String terrainChoice = "";
    private static String elvPath = "";

    public static void main(String[] args) {
        //Create Terrain Choose frame
        JFrame frame = new JFrame("Terrain Chooser");
        TerrainChooser terrainChooser = new TerrainChooser();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(terrainChooser);
        frame.pack();
        frame.setVisible(true);

        //While user hasn't chosen a terrain, wait
        while (terrainChoice.equals("")){
            System.out.print("");
        }
        //Once they have chosen terrain, hide frame
        System.out.println("Terrain chosen: " + terrainChoice);

        frame.setVisible(false);

        if (!terrainChoice.equals("") && !terrainChoice.equals("custom")) {
            TerrainLoader.loadElevation("./Elevation/"+ terrainChoice + ".elv");
        }

        else if (!elvPath.equals("")) {
            TerrainLoader.loadElevation(elvPath);
        }
        //scale small terrain
        if (TerrainLoader.getTerrainDimx() < 1024){
            TerrainLoader.scaleTerrain(2);
        }
        if (TerrainLoader.getTerrainDimx() >= 1024){
            TerrainLoader.scaleTerrain(0.6);
            System.out.println("Scaling down");
        }
        TerrainLoader.generateTerrainBackground();

        //image drawn in simulation panel paintComponent method

        //create Simulation Jframe
        new SimJFrame();

    }

    public static void setTerrainChoice(String terrainChoice) {
        Simulation.terrainChoice = terrainChoice;
    }

    public static void setElvPath(String elvPath) {
        Simulation.elvPath = elvPath;
    }

    public String getTerrainChoice() {
        return terrainChoice;
    }
}