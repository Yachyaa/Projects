/*
Yachyaa Toefy (TFYYAC001)
Luke Clayton (CLYLUK001)
Fabio O'ryan Paulo (ORYFAB001)

Animal.java - creates and moves animals on the SimulationPanel.
This class implements all the rules of Craig Reynolds Enhanced Boids Model as well
as additional features such as obstacle detection and avoidance algorithms, waypoint setting, detecting
and behavioral algorithms as well as border collision algorithms

 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Animal  {
    private String type;
    private PVector position;
    private PVector velocity;

    private PVector heading;
    private Color color;

    private ArrayList<Obstacle> obstacles;

    public double maxSlopeUp;
    public double maxSlopeDown;
    private float slopeWeighting = 0.8f;

    private SettingsPanel settingsPanel;
    private  float MAX_FORCE;         // Maximum steering force

    private float MAX_SPEED;
    private int size;

    private boolean animalsOnScreen;


    public Animal(String type, float x, float y, SettingsPanel sp, ArrayList<Obstacle> obstacles, boolean animalsOnScreen) {

        setType(type);
        setSettingsPanel(sp);
        setObstacles(obstacles);
        setPosition(new PVector(x, y));
        // Set boids velocity in random direction to avoid crowding when spawning
        setVelocity(new PVector((float) Math.random() - 0.5f, (float) Math.random() - 0.5f));
        setHeading();
        setAnimalsOnScreen(animalsOnScreen);

        setAnimalDependentVariables(getType()); // instance variable initialization depends on animal type

    }


    // Copy constructor
    public Animal(Animal otherAnimal) {
        setType(otherAnimal.getType());
        setPosition(new PVector(otherAnimal.getPosition().getX(), otherAnimal.getPosition().getY()));
        setVelocity(new PVector(otherAnimal.getVelocity().getX(), otherAnimal.getVelocity().getY()));
        setHeading();
        setSettingsPanel(otherAnimal.getSettingsPanel());
        setAnimalDependentVariablesFrame(otherAnimal.getType());

        // Deep copy of obstacles
        this.obstacles = new ArrayList<>();
        for (Obstacle obstacle : otherAnimal.getObstacles()) {
            this.obstacles.add(new Obstacle(obstacle)); // Assuming Obstacle has a copy constructor
        }
    }



    /////////////////////////////////////////// Move Method ////////////////////////////////////////////////////////////


    // Move boids according to Boids rules. Also implements additional features
    public void move(int panelWidth, int panelHeight, List<Animal> otherAnimals, PVector waypoint) {
        // Get forces to apply on boids
        avoid(obstacles);
        PVector separationForce = separate(otherAnimals);
        PVector alignmentForce = align(otherAnimals);
        PVector cohesionForce = cohesion(otherAnimals);
        PVector slopeForce = slopeForce(getPosition());
        PVector seekForce = new PVector(0, 0);

        //waypoint set
        if (waypoint != null) {
            seekForce = seek(waypoint);
            // Adjust the weight of the seek force to balance with other forces
            seekForce.mult(0.5f);
        }

        // Apply forces with different weights
        separationForce.mult(1.5f);
        alignmentForce.mult(1.0f);
        cohesionForce.mult(1.0f);
        slopeForce.mult(slopeWeighting);


        // Combine forces and limit the total force
        PVector totalForce = new PVector(0, 0);
        totalForce.add(separationForce);
        totalForce.add(alignmentForce);
        totalForce.add(cohesionForce);
        totalForce.add(seekForce);
        totalForce.limit(MAX_FORCE);
        totalForce.add(slopeForce);


        // Update velocity
        velocity.add(totalForce);
        velocity.limit(MAX_SPEED);

        // Update position based on velocity
        position.add(velocity);

        getBarrierCollisionBehaviour(panelWidth, panelHeight);

    }




    /////////////////////////////////////////////// Boid Rules /////////////////////////////////////////////////////////




    // Returns steering force required to ensure separation in the herd
    private PVector separate(List<Animal> otherAnimals) {
        // Calculate a force to avoid other animals
        PVector steer = new PVector(0, 0);  // steering force applied to boids to create separation

        int count = 0;  // used to average the steering force
        for (Animal other : otherAnimals) {
            float d = position.dist(other.position);  // Distance between this vector and other vector
            if (d > 0 && d < settingsPanel.getSeparationValue()) {  // Distance between animals smaller than distance on slider
                PVector diff = PVector.sub(position, other.position);
                diff.normalize();
                diff.div(d);
                steer.add(diff);
                count++;
            }
        }

        if (count > 0) { // Ensures that there are animals on screen
            steer.div((float) count); // get average steering force
        }
        if (steer.mag() > 0) {
            steer.normalize();
            steer.mult(2);
            steer.sub(velocity);
            steer.limit(MAX_FORCE);
        }
        return steer;
    }



    // Ensures alignment by aligning the animals using the average velocity of nearby animals (Perception radius determined by slider)
    private PVector align(List<Animal> otherAnimals) {

        // Calculate average velocity of nearby animals
        PVector sum = new PVector(0, 0);
        int count = 0;
        for (Animal other : otherAnimals) {
            float d = position.dist(other.position);
            if (d > 0 && d < settingsPanel.getAlignmentValue()) {  // if distance between animals are smaller that value of alignment slider
                sum.add(other.velocity);
                count++;
            }
        }
        if (count > 0) {  // Ensures that there are animals on the screen
            sum.div((float) count);
            sum.normalize();
            sum.mult(2);  // Adjust the desired alignment force
            PVector steer = PVector.sub(sum, velocity);
            steer.limit(MAX_FORCE);
            return steer;
        }


        return new PVector(0, 0); // Will not get executed if there are animals on screen
    }



    // Calculates cohesion force between animals based on the rest of the animals position (limited by animals perception radius)
    private PVector cohesion(List<Animal> otherAnimals ) {

        PVector sum = new PVector(0, 0);

        int count = 0;
        for (Animal other : otherAnimals) {
            float d = position.dist(other.position);
            if (d > 0 && d < settingsPanel.getCohesionValue()) {  // if distance between animals are smaller that value of cohesion slider
                sum.add(other.position);
                count++;
            }
        }
        if (count > 0) {
            sum.div((float) count);
            return seek(sum);  // Steer towards the average position
        }

        return new PVector(0, 0);
    }




    ///////////////////////////////////////////Extra Behaviour /////////////////////////////////////////////////////////



    // Apply a force to vector based on the targets position to steer vector to targer
    private PVector seek(PVector target) {

        PVector desired = PVector.sub(target, position);
        desired.normalize();
        desired.mult(2);  // Adjust the desired steering force

        PVector steer = PVector.sub(desired, velocity);
        steer.limit(MAX_FORCE);

        return steer;
    }


    // apply steering force to vectors based on the location and distance between the vector and the obstacle
    private void avoid(ArrayList<Obstacle> obstacles) {

        PVector avoidanceForce = new PVector(0, 0);

        for (Obstacle obstacle : obstacles) {
            if (obstacle.getType().equalsIgnoreCase("tree")) {
                PVector toObstacle = PVector.sub(obstacle.getLocation(), position);
                float distance = toObstacle.mag() + obstacle.getSize();

                if (distance < 0.3 * 150) {
                    toObstacle.normalize();
                    toObstacle.div(distance); // Weight the force by distance
                    avoidanceForce.add(toObstacle);
                }

                if (!avoidanceForce.isZero()) {
                    avoidanceForce.normalize();
                    avoidanceForce.mult(2); // MAX FORCE
                    avoidanceForce.sub(velocity);
                    avoidanceForce.limit(MAX_FORCE);
                    velocity.sub(avoidanceForce);
                }
            }
            else if (obstacle.getType().equalsIgnoreCase("water")) {
                PVector toObstacle = PVector.sub(obstacle.getLocation(), position);
                float distance = toObstacle.mag() + obstacle.getSize();

                if (distance < 0.3 * 150) {
                    toObstacle.normalize();
                    toObstacle.div(distance); // Weight the force by distance
                    if (!getType().equalsIgnoreCase("penguin")) {
                        avoidanceForce.add(toObstacle);
                    }
                    else{
                        float slowdownFactor = 0.9f; // Adjust this value as needed
                        avoidanceForce.mult(-slowdownFactor);
                    }
                }

                if (!avoidanceForce.isZero()) {
                    avoidanceForce.normalize();
                    avoidanceForce.mult(2); // MAX FORCE
                    avoidanceForce.sub(velocity);
                    avoidanceForce.limit(MAX_FORCE);
                    velocity.sub(avoidanceForce);
                }
            }
        }
    }

    private PVector slopeForce(PVector currentPosition) {
        PVector heading = new PVector(getVelocity().getX(), getVelocity().getY());
        heading.normalize();

        double slope = TerrainLoader.slopeBetween(PVector.add(currentPosition, heading), currentPosition);
        float slopeForceMag = (float) (Math.abs(slope) / Math.abs(maxSlopeUp));

        if (slope < maxSlopeDown || slope > maxSlopeUp) {
            // Steer away from the unfavorable slope based on current position
            return PVector.mult(heading, -slopeForceMag); // I think we should steer it left or right instead to avoid boid getting stuck
        } else if (slope == 0.0) {
            // If slope is flat return 0 force vector
            return new PVector(0, 0);
        } else if (slope > 0) {
            // Return a force to slow down when climbing
            return PVector.mult(heading, -slopeForceMag);
        } else {
            // Return a force to speed up when descending
            return PVector.mult(heading, slopeForceMag);
        }
    }






    /////////////////////////////////////////// Helper Methods ////////////////////////////////////////////////////////


    // set/change certain variables depending on animal type
    public void setAnimalDependentVariables(String type){
        // Animal dependent features
        if (type.equalsIgnoreCase("sheep"))
        {
            MAX_SPEED = 0.6f;
            MAX_FORCE = 0.1f;
            color = color.BLUE;
            maxSlopeUp = 2.5;
            maxSlopeDown = -20;
            size = 10;

            if (!animalsOnScreen)
                setBoidRulesValues(100, 50, 15);
        }
        if (type.equalsIgnoreCase("penguin"))
        {
            MAX_SPEED = 2.5f;
            MAX_FORCE = 0.1f;
            color = new Color(255, 165, 0);
            maxSlopeUp = 10;
            maxSlopeDown = -20;
            size = 7;

            if (!animalsOnScreen)
                setBoidRulesValues(50, 20, 40);

        }
        if (type.equalsIgnoreCase("elephant"))
        {

            MAX_SPEED = 1f;
            MAX_FORCE = 0.1f;
            color = color.PINK;
            maxSlopeUp = 0.8;
            maxSlopeDown = -0.8;
            size = 30;

            if (!animalsOnScreen)
                setBoidRulesValues(100, 50, 50);

        }
    }

    public void setAnimalDependentVariablesFrame(String type){
        // Animal dependent features
        if (type.equalsIgnoreCase("sheep"))
        {
            MAX_SPEED = 0.6f;
            MAX_FORCE = 0.1f;
            color = color.BLUE;
            maxSlopeUp = 2.5;
            maxSlopeDown = -20;
            size = 10;
        }
        if (type.equalsIgnoreCase("penguin"))
        {
            MAX_SPEED = 2.5f;
            MAX_FORCE = 0.1f;
            color = new Color(255, 165, 0);
            maxSlopeUp = 10;
            maxSlopeDown = -20;
            size = 7;

        }
        if (type.equalsIgnoreCase("elephant"))
        {
            MAX_SPEED = 1f;
            MAX_FORCE = 0.1f;
            color = color.PINK;
            maxSlopeUp = 0.8;
            maxSlopeDown = -0.8;
            size = 30;


        }
    }

    public void increaseAnimalSize(int increment){
        setSize(this.size + increment);
    }


    public void changeSpeed(float factor) {
        velocity.mult(factor); // Multiply the velocity by the factor
    }

    public void setBoidRulesValues(int cohValue, int alignValue, int sepValue){
        settingsPanel.setCohesionValue(cohValue);
        settingsPanel.setAlignmentValue(alignValue);
        settingsPanel.setSeparationValue(sepValue);
        settingsPanel.setHerdSizeSlider(1);
    }


    public void getBarrierCollisionBehaviour(int panelWidth, int panelHeight){
        // checks settings panel for  wrap border behaviour
        if (settingsPanel.getBarrierBehaviour().equalsIgnoreCase("wrap")) {
            if (position.getX() > panelWidth) {
                position.setX(0);
            } else if (position.getX() < 0) {
                position.setX(panelWidth);
            }

            if (position.getY() > panelHeight) {
                position.setY(0);
            } else if (position.getY() < 0) {
                position.setY(panelHeight);
            }
        }

        // check settings panel for the reflection border behavior
        if (settingsPanel.getBarrierBehaviour().equalsIgnoreCase("reflect")) {
            if (position.getX() < 0 || position.getX() > panelWidth - size - 5) {
                velocity.setX(-velocity.getX());
                position.setX(Math.max(position.getX(), 0));
                position.setX(Math.min(position.getX(), panelWidth - size));
            }
            if (position.getY() < 0 || position.getY() > panelHeight - size - 5) {
                velocity.setY(-velocity.getY());
                position.setY(Math.max(position.getY(), 0));
                position.setY(Math.min(position.getY(), panelHeight - size));
            }
        }

        if (settingsPanel.getBarrierBehaviour().equalsIgnoreCase("follow")) {
            if (position.getX() > panelWidth) {
                position.setX(panelWidth);
            } else if (position.getX() < 0) {
                position.setX(0);
            }

            if (position.getY() > panelHeight) {
                position.setY(panelHeight);
            } else if (position.getY() < 0) {
                position.setY(0);
            }
        }
    }


    // checks if waypoint is reached
    public boolean wayPointReached (Animal animal, PVector waypoint)
    {
        // Check if the animal has reached the waypoint
        return waypoint != null && animal.getPosition().dist(waypoint) < animal.getSize();
    }


    @Override
    public String toString(){
        return getType() + "at (" + getPosition().getX() + ", " + getPosition().getX() + ")";
    }





    ///////////////////////////////////////////// GETTERS AND SETTERS /////////////////////////////////////////////////

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
    public int getSize() {
        return size;
    }
    public PVector getVelocity() {
        return velocity;
    }
    public String getType() {
        return type;
    }
    public Color getColor() {
        return color;
    }

    public void setHeading(PVector heading) {
        this.heading = heading;
    }

    public void setSlopeWeighting(float slopeWeighting) {
        this.slopeWeighting = slopeWeighting;
    }

    public PVector getPosition() {
        return position;
    }

    public SettingsPanel getSettingsPanel() {
        return settingsPanel;
    }

    public void setObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }
    public boolean getAnimalsOnScreen(){
        return this.animalsOnScreen;
    }

    public void setAnimalsOnScreen(boolean animalsOnScreen) {
        this.animalsOnScreen = animalsOnScreen;
    }

    public void setVelocity(PVector velocity) {
        this.velocity = velocity;
        velocity.normalize();
    }

    public void setHeading() {
        getVelocity().div(getVelocity().mag());
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setMAX_FORCE(float MAX_FORCE) {
        this.MAX_FORCE = MAX_FORCE;
    }

    public void setSettingsPanel(SettingsPanel settingsPanel) {
        this.settingsPanel = settingsPanel;
    }
}
