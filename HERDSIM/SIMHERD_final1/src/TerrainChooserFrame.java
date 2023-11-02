import javax.swing.*;
import java.awt.image.BufferedImage;

public class TerrainChooserFrame extends JFrame {

    private static String terrainChoice = "";

    public TerrainChooserFrame(){
        JFrame frame = new JFrame("Terrain Chooser");
        TerrainChooser terrainChooser = new TerrainChooser();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(terrainChooser);
        frame.pack();
        frame.setVisible(true);

        //While user hasn't chosen a terrain, wait8
        while (terrainChoice.equals("")){
            System.out.print("");
        }
        //Once they have chosen terrain, hide frame
        System.out.println("Terrain chosen: " + terrainChoice);
        frame.setVisible(false);

        //load elevation and generate bg img
        TerrainLoader.loadElevation("Elevation\\"+ terrainChoice + ".elv");
        TerrainLoader.generateTerrainBackground();

        //scale small terrain
        if (TerrainLoader.getTerrainDimx() < 1000){
            TerrainLoader.scaleTerrain(2);
        }

        if (TerrainLoader.getTerrainDimY() > 1000){
            TerrainLoader.scaleTerrain(0.6);
            System.out.println("Scaling down");
        }

        BufferedImage terrainBG = TerrainLoader.getTerrainImage();
    }

    public static void setTerrainChoice(String terrainChoicee) {
        terrainChoice = terrainChoicee;
    }

    public String getTerrainChoice() {
        return terrainChoice;
    }
}
