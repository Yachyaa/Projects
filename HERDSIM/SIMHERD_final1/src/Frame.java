import java.util.ArrayList;

public class Frame {
    ArrayList<Animal> animals;
    ArrayList<Obstacle> obstacles;

    public Frame(ArrayList<Animal> herd, ArrayList<Obstacle> obstacles) {
        // creates a deep copy of herd of the current frame
        this.animals = new ArrayList<>();
        for (Animal animal : herd) {
            this.animals.add(new Animal(animal)); // Create deep copy of each animal
        }

        // creates a deep copy of obstacles of the current frame
        this.obstacles = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            this.obstacles.add(new Obstacle(obstacle)); // Create deep copy of each obstacle
        }
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }

    public void setObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public void setAnimals(ArrayList<Animal> animals) {
        this.animals = animals;
    }

}
