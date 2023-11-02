/////////////////////  Imports /////////////////////////////////////

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.Scanner; //for handling user input

public class TerrainLoader{

    ///////////////////// Terrain Attributes //////////////////////////////
    private static double[][] elevation = null;
    private static int terrainDimx = 0;
    private static int terrainDimy = 0;
    private static double gridSpacing = 0;
    private static double maxElevation = Double.NEGATIVE_INFINITY;
    private static double minElevation = Double.POSITIVE_INFINITY;
    private static BufferedImage terrainImage = null;

    ///////////////////// Private helper methods //////////////////////////////
    private static double toMetres(double feetValue){
        return feetValue/3.281;
    }

    private static void resetStaticValues() {
        elevation = null;
        terrainDimx = 0;
        terrainDimy = 0;
        gridSpacing = 0;
        maxElevation = Double.NEGATIVE_INFINITY;
        minElevation = Double.POSITIVE_INFINITY;
    }


    ///////////////////// Public class methods //////////////////////////////
    public static boolean loadElevation(String filepath) {
        resetStaticValues();
        try {
            // Read the header
            BufferedReader headerReader = new BufferedReader(new FileReader(filepath));
            String headerLine = headerReader.readLine();
            String[] headerData = headerLine.split(" ");

            terrainDimx = Integer.parseInt(headerData[0]);
            terrainDimy = Integer.parseInt(headerData[1]);
            gridSpacing = Double.parseDouble(headerData[2]);

            headerReader.close();

            System.out.println("Header read successfully");

            // Read and convert elevation data
            elevation = new double[terrainDimx][terrainDimy];

            BufferedReader dataReader = new BufferedReader(new FileReader(filepath));
            dataReader.readLine(); // Skip the header

            System.out.println("Loading data");

            int x = 0;
            int y = 0;

            String line;
            while ((line = dataReader.readLine()) != null) {
                String[] row = line.trim().split(" ");
                for (String value : row) {
                    double feetValue = Double.parseDouble(value);
                    double metersValue = toMetres(feetValue);
                    elevation[x][y] = metersValue;

                    // Update min and max elevation
                    if (metersValue < minElevation) {
                        minElevation = metersValue;
                    }
                    if (metersValue > maxElevation) {
                        maxElevation = metersValue;
                    }

                    y++;
                    if (y == terrainDimy) {
                        y = 0;
                        x++;
                    }
                }
            }

            dataReader.close();
            System.out.println("Successfully loaded elevation data");
            return true;

        } catch (Exception e) {
            System.out.println("Error loading elevation data");
            e.printStackTrace();
            resetStaticValues();
            return false;
        }
    }

    public static void scaleTerrain(double scaleFactor) {
        if (elevation != null) {
            if (scaleFactor > 0) {
                if (scaleFactor < 1) {
                    // Scale down the terrain dimensions and spacing
                    terrainDimx = (int) (terrainDimx * scaleFactor);
                    terrainDimy = (int) (terrainDimy * scaleFactor);
                    gridSpacing = gridSpacing / scaleFactor;

                    int originalSize = elevation.length;
                    int newSize = (int) (originalSize * scaleFactor);
                    double[][] scaledArray = new double[newSize][newSize];

                    for (int i = 0; i < newSize; i++) {
                        for (int j = 0; j < newSize; j++) {
                            int originalX = (int) (i / scaleFactor);
                            int originalY = (int) (j / scaleFactor);
                            scaledArray[i][j] = elevation[originalX][originalY];
                        }
                    }
                    elevation = scaledArray;
                } else {
                    // Scale up the terrain dimensions and spacing
                    terrainDimx = (int) (terrainDimx * scaleFactor);
                    terrainDimy = (int) (terrainDimy * scaleFactor);
                    gridSpacing = gridSpacing / scaleFactor;

                    int originalSize = elevation.length;
                    int newSize = (int) (originalSize * scaleFactor);
                    double[][] scaledArray = new double[newSize][newSize];

                    for (int i = 0; i < originalSize; i++) {
                        for (int j = 0; j < originalSize; j++) {
                            double originalValue = elevation[i][j];
                            for (int m = 0; m < scaleFactor; m++) {
                                for (int n = 0; n < scaleFactor; n++) {
                                    scaledArray[i * (int)scaleFactor + m][j * (int)scaleFactor + n] = originalValue;
                                }
                            }
                        }
                    }
                    elevation = scaledArray;
                }
            } else {
                System.out.println("Error scaling terrain: Invalid scaleFactor. It must be greater than 0.");
            }
        } else {
            System.out.println("Error scaling terrain: No elevation data stored. First load terrain elevation data.");
        }
    }


    public static boolean generateTerrainBackground() {
        if (elevation == null || terrainDimx == 0 || terrainDimy == 0) {
            System.out.println("Error generating grayscale image: No data available.");
            return false;
        }

        try {
            System.out.println("Generating terrain background image");

            // Create a BufferedImage with 32-bit grayscale type
            terrainImage = new BufferedImage(terrainDimx, terrainDimy, BufferedImage.TYPE_BYTE_GRAY);

            // Determine grayscale mapping based on elevation range
            double elevationRange = maxElevation - minElevation;

            if (elevationRange != 0.0) {
                for (int x = 0; x < terrainDimx; x++) {
                    for (int y = 0; y < terrainDimy; y++) {
                        double normalizedValue = (elevation[x][y] - minElevation) / elevationRange;
                        float grayValue = (float) normalizedValue; // Use a float for 32-bit grayscale

                        // Create a Color object with grayscale value and alpha of 1.0 (fully opaque)
                        Color color = new Color(grayValue, grayValue, grayValue, 1.0f);

                        // Set the pixel in the BufferedImage
                        terrainImage.setRGB(x, y, color.getRGB());
                    }
                }
            } else {
                for (int x = 0; x < terrainDimx; x++) {
                    for (int y = 0; y < terrainDimy; y++) {
                        // For the case when elevationRange is 0.0, create a fully opaque white pixel
                        Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                        terrainImage.setRGB(x, y, color.getRGB());
                    }
                }
            }

            System.out.println("Successfully generated terrain background image");
            return true;

        } catch (Exception e) {
            System.out.println("Error generating grayscale image");
            return false;
        }
    }


    public static double slopeBetween(PVector pInitial, PVector pFinal) {
        // Calculate the wrapped coordinates to avoid out-of-bounds errors
        int xInitial = (int) (pInitial.getX() + terrainDimx) % terrainDimx;
        int yInitial = (int) (pInitial.getY() + terrainDimy) % terrainDimy;
        int xFinal = (int) (pFinal.getX() + terrainDimx) % terrainDimx;
        int yFinal = (int) (pFinal.getY() + terrainDimy) % terrainDimy;

        double distance = Math.abs(PVector.sub(pFinal, pInitial).mag()) * gridSpacing;
        double dz = getElevation(xFinal, yFinal) - getElevation(xInitial, yInitial);

        return dz / distance;
    }

    public static double getElevation(int x, int y){
        if (x < getTerrainDimx() && y < getTerrainDimy()) {
            return elevation[x][y];
        }
        else{
            System.out.println("Error: accessing x or y > terrain dimensions");
            return 0.0;
        }
    }


    ///////////////////// Load and Save terrain features to directory  //////////////////////////////
    public static boolean loadFeatures(Terrain terrain, String fileName){
        //create obstacle arrayList
        //read featuresFile;
        //and populate obstacles list during each iteration
        //if terrain is small, load from /small directory else load from /large
        return false;
    }

    public static boolean saveFeatures(Terrain terrain, String fileName){
        // if create file
        //loop through obstacles arrlist
        //write to file during each iteration
        return false;
    }

    public static void bufferedToPNG(BufferedImage bf, String currElvName) throws IOException {
        BufferedImage currentBufeeredImage = TerrainLoader.getTerrainImage();

        File outputFile = new File("TerrainPNG's\\" + currElvName + " 32bit " + "(" + TerrainLoader.getTerrainDimx() +")" + "(" + TerrainLoader.getTerrainDimy() +")" + "(" + TerrainLoader.getGridSpacing() +")" + "(" + TerrainLoader.getMaxElevation() +")"  + ".png");

        ImageIO.write(currentBufeeredImage , "png", outputFile);

    }

    public static BufferedImage getTerrainImage() {

        return terrainImage;
    }

    public static double getMaxElevation() {
        return maxElevation;
    }

    public static double getGridSpacing() {
        return gridSpacing;
    }

    public static double getMinElevation() {
        return minElevation;
    }

    public static double[][] getElevation() {
        return elevation;
    }

    public static int getTerrainDimy() {
        return terrainDimy;
    }



    ///////////////////// Generate Terrain Object //////////////////////////////
    public Terrain generateTerrain(){
        return new Terrain(terrainDimx, terrainDimy, gridSpacing, elevation, terrainImage, null);
    }

    ///////////////////// Testing methods //////////////////////////////
    public static void displayImage(BufferedImage image) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.setSize(terrainDimx, terrainDimy);

        JLabel label = new JLabel(new ImageIcon(image));
        frame.add(label, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }

    public static int getTerrainDimx() {
        return terrainDimx;
    }

    public static void setBufferedImageWithFile(String filepath){
        TerrainLoader.loadElevation(filepath);
        //scale small terrain
        if (TerrainLoader.getTerrainDimx() < 1000){
            TerrainLoader.scaleTerrain(2);
        }

        if (TerrainLoader.getTerrainDimY() > 1000){
            TerrainLoader.scaleTerrain(0.6);
        }
        TerrainLoader.generateTerrainBackground();

    }

    public static void setBufferedImage(BufferedImage i){
        terrainImage = i;
        //scale small terrain
        if (TerrainLoader.getTerrainDimx() < 1000){
            TerrainLoader.scaleTerrain(2);
        }

        if (TerrainLoader.getTerrainDimY() > 1000){
            TerrainLoader.scaleTerrain(0.6);
        }
    }

    public static BufferedImage getBufferedImage(){

        return terrainImage;
    }
    public static int getTerrainDimY() {
        return terrainDimy;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("----Terrain Loader----");
        System.out.println("Flat terrains");
        System.out.println("1. Small");
        System.out.println("2. Large");
        System.out.println("");
        System.out.println("Elevated terrains");
        System.out.println("3. Small #1");
        System.out.println("4. Small #2");
        System.out.println("5. Small #3");
        System.out.println("6. Small #4");
        System.out.println("7. Large");
        System.out.println("----------------------");


        Scanner input = new Scanner(System.in);
        String choice = "";
        do {
            System.out.print("Enter terrain number: ");
            choice = input.next();
        } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4") && !choice.equals("5") && !choice.equals("6") && !choice.equals("7"));

        String terrainName ="";

        switch (choice) {
            case "1":
                terrainName = "smallFlat";
                break;
            case "2":
                terrainName = "largeFlat";
                break;
            case "3":
                terrainName = "small1";
                break;
            case "4":
                terrainName = "small2";
                break;
            case "5":
                terrainName = "small3";
                break;
            case "6":
                terrainName = "small4";
                break;
            case "7":
                terrainName = "large1";
                break;

        }

        input.close();

        TerrainLoader.loadElevation("" +
                ""+ terrainName + ".elv");


        //if terrain is small, scale it up
        if (TerrainLoader.terrainDimx < 1000){
            TerrainLoader.scaleTerrain(2);
        }

        if (TerrainLoader.getTerrainDimY() > 1000){
            TerrainLoader.scaleTerrain(0.6);
            System.out.println("Scaling down");
        }

        if (TerrainLoader.generateTerrainBackground()){
            displayImage(terrainImage);
        }

        TerrainLoader.bufferedToPNG(TerrainLoader.getTerrainImage(), terrainName);
        System.out.println("png created for: " + terrainName);

    }
}
