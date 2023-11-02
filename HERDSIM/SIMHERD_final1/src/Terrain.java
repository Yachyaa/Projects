import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class Terrain{
    private ArrayList<Obstacle> obstacles;
    private BufferedImage terrainImage;
    private double[][] elevation;
    private double gridSpacing;
    private int dimx;
    private int dimy; 

    public Terrain(int dimx, int dimy, double gridSpacing, double[][] elevation, BufferedImage terrainImage, ArrayList<Obstacle> obstacles){
        this.dimx = dimx;
        this.dimy = dimy;
        this.gridSpacing = gridSpacing;
        this.elevation = elevation;
        this.terrainImage = terrainImage;
        this.obstacles = obstacles;
    }

    // public double slopeBetween(Pvector pFinal, Pvector pInitial){
    //     double distance = abs(mag(pfinal.subtract(pInitial)))*gridSpacing;
    //     dz = getElevation(pfinal) - getElevation(pInitial);
    //     return dz/distance;
    // }


    ///////////////////// Getters and setters //////////////////////////////
    public int getDimx() {
        return dimx;
    }

    public int getDimy() {
        return dimy;
    }

    public double[][] getElevation() {
        return elevation;
    }

    public double getElevation(int x, int y){
        return elevation[x][y];
    }

    public double getGridSpacing() {
        return gridSpacing;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public BufferedImage getTerrainImage() {
        return terrainImage;
    }
    
    public void setDimx(int dimx) {
        this.dimx = dimx;
    }

    public void setDimy(int dimy) {
        this.dimy = dimy;
    }

    public void setElevation(double[][] elevation) {
        this.elevation = elevation;
    }

    public void setGridSpacing(double gridSpacing) {
        this.gridSpacing = gridSpacing;
    }
    
    public void setObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public void setTerrainImage(BufferedImage terrainImage) {
        this.terrainImage = terrainImage;
    }
    

        
}