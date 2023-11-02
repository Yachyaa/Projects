/*
Yachyaa Toefy (TFYYAC001)
Luke Clayton (CLYLUK001)
Fabio O'Ryan Paulo (ORYFAB001)

Obstacle.java
This class is responsible for creating obstacle objects.
 */

public class Obstacle {
    private PVector location;
    private String type;
    int size; // size is determined by the type of obstacle

    public Obstacle(PVector location, String type){
        setLocation(location);
        setType(type);
        setSize(); // size set based on obstacle type
    }

    // Copy constructor
    public Obstacle(Obstacle otherObstacle) {
        this.location = new PVector(otherObstacle.getLocation().getX(), otherObstacle.getLocation().getY());
        this.type = otherObstacle.getType();
        this.size = otherObstacle.getSize();
    }



    //GETTERS AND SETTERS
    public String getType() {
        return type;
    }

    public PVector getLocation() {
        return location;
    }

    public float getX () {
        return this.location.getX();
    }
    public float getY () {
        return this.location.getY();
    }


    public int getSize() {
        return size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(PVector location) {
        this.location = location;
    }

    public void setSize() {
        // Sizes of different obstacles
        if (type.equalsIgnoreCase("water")){
            this.size = 10;
        }
        if (type.equalsIgnoreCase("tree")){
            this.size = 10;
        }
    }

    public void increaseSize(int increment){
        size = size + increment;
    }


}
