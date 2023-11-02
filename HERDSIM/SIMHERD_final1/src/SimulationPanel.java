/*
Yachyaa Toefy (TFYYAC001)
Luke Clayton (CLYLUK001)
Fabio O'ryan Paulo (ORYFAB001)

SimulationPanel.java
This class is responsible for starting and animating the movement and behaviour of boids. It does
this by keeping track of all the objects (animals, obstacles and waypoint) on the screen and
painting them on their respective (x,y) co-ordinates which are obtain and managed from their respective classes.
It is not responsible for the settings panel however it does make use of the values
that the settings panel provides from its listeners.
This class also contains all the mouse listeners responsible for tracking and managing
mouse clicks and mouse drags .
 */


import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;





public class SimulationPanel extends JPanel implements MouseMotionListener {

    private ArrayList<Frame> frames;
    private ArrayList<Animal> animals;
    private ArrayList<Obstacle> obstacles;
    private PVector waypoint;
    private SettingsPanel settingsPanel;
    private HorizontalPanel horizontalPanel;

    private int animationSpeed = 30;
    private Timer animationTimer;

    private boolean animalsOnScreen = false;
    private int numberOfAnimalsOnScreen = 0;
    private boolean obstaclesOnScreen = false;
    private String currentlyOnScreen;
    public int numberOfFrames;
    private int rewindFrameIndex = 0; // Add this field to keep track of the current frame

    private boolean isRewinding;
    private int currentFrameIndex; // Track the current frame index for rewind
    private Timer rewindTimer; // Timer for controlling rewind animation
    private ArrayList<Animal> lastHerdPainted;
    private ArrayList<Obstacle> lastObstaclesPAinted;


    public int zoomIndex = 0;
    private long lastZoomTime = 0; // Timestamp of the last zoom action
    private static final long ZOOM_COOLDOWN = 500;
    public boolean filesCleared = false;
    public ArrayList<BufferedImage> trackZoom;
    public BufferedImage startImage;


    public SimulationPanel(SettingsPanel sp, HorizontalPanel hp) {
        initialize(sp, hp);
        addMouseListeners();
        startAnimation();

    }

    public void initialize(SettingsPanel sp, HorizontalPanel hp){
//        clearFile("Frames\\AnimalFrames.txt");
//        clearFile("Frames\\ObstacleFrames.txt");
//        filesCleared = true;


        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        setSettingsPanel(sp);
        setHorizontalPanel(hp);
        setObstacles(new ArrayList<>());
        setAnimals(new ArrayList<>());
        setFrames(new ArrayList<>());
        setWaypoint(null);

        setTrackZoom(new ArrayList<>());
    }



    ////////////////////////////////Animation Methods////////////////////////////////////////////

    // Creates an animation loop with a delay between each update
    public void startAnimation(){
        animationTimer = new Timer(animationSpeed, e -> {
            checkButtons();

            // Frame updates if pause is not clicked
            if (!getHorizontalPanel().pauseClicked && !horizontalPanel.rewindClicked){
                numberOfFrames++;
                updateSimulation();
                repaint();
            }});


        animationTimer.start();
    }

    // Updates the simulation with the updated values
    private void updateSimulation() {
        for (Animal animal : animals) {
            checkWaypoint(animal);
            animal.move(TerrainLoader.getTerrainDimx(), TerrainLoader.getTerrainDimY(), animals, waypoint);
        }

        if (!getAnimals().isEmpty() || !getObstacles().isEmpty()) {
            Frame frame = new Frame(new ArrayList<>(getAnimals()), new ArrayList<>(getObstacles()));
            frames.add(frame);
            writeAnimalFrame();
        }
    }




    ////////////////////////////////////// Painting Method /////////////////////////////////////////////////////////////


    // Paints all the animals, obstacles and waypoints using the repaint() method
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(TerrainLoader.getTerrainImage(), 0, 0, TerrainLoader.getTerrainDimx(), TerrainLoader.getTerrainDimx(), this);

        for (Animal animal : animals){
            g.setColor(animal.getColor());
            int x = (int) animal.getPosition().getX();
            int y = (int) animal.getPosition().getY();
            int dotSize = 5; // Adjust the size of the dots as needed
            g.fillOval(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
        }

        if (obstacles.size() != 0) {
            for (Obstacle obstacle : obstacles) {
                if (obstacle.getType().equalsIgnoreCase("water")) {
                    Color transparentBlue = new Color(0, 0, 255, 10); // 128 is the alpha value (0-255)
                    g.setColor(transparentBlue);
                    g.fillOval((int) obstacle.getX(), (int) obstacle.getY(), obstacle.getSize(), obstacle.getSize());
                } else if (obstacle.getType().equalsIgnoreCase("tree")) {
                    g.setColor(Color.GREEN);
                    g.fillOval((int) obstacle.getX(), (int) obstacle.getY(), obstacle.getSize(), obstacle.getSize());
                }

            }
        }


        // Creates the arrows based on the animals size
        for (Animal animal : animals) {
            g.setColor(animal.getColor());

            // Calculate the angle of the velocity vector
            float angle = animal.getVelocity().heading();

            // Calculate the coordinates of the tip of the arrow
            float arrowTipX = (float) (animal.getPosition().getX() + animal.getSize() * 0.8 * Math.cos(angle));
            float arrowTipY = (float) (animal.getPosition().getY() + animal.getSize() * 0.8 * Math.sin(angle));

            // Calculate the coordinates of the two endpoints of the arrow base
            float arrowBaseX1 = (float) (arrowTipX + animal.getSize() * 0.2 * Math.cos(angle - 0.75 * Math.PI));
            float arrowBaseY1 = (float) (arrowTipY + animal.getSize() * 0.2 * Math.sin(angle - 0.75 * Math.PI));
            float arrowBaseX2 = (float) (arrowTipX + animal.getSize() * 0.2 * Math.cos(angle + 0.75 * Math.PI));
            float arrowBaseY2 = (float) (arrowTipY + animal.getSize() * 0.2 * Math.sin(angle + 0.75 * Math.PI));

            // Draw the arrow
            g.drawLine((int) animal.getPosition().getX(), (int) animal.getPosition().getY(), (int) arrowTipX, (int) arrowTipY);
            g.drawLine((int) arrowTipX, (int) arrowTipY, (int) arrowBaseX1, (int) arrowBaseY1);
            g.drawLine((int) arrowTipX, (int) arrowTipY, (int) arrowBaseX2, (int) arrowBaseY2);
        }

        // if waypoint set --> draw the waypoint
        if (waypoint != null) {
            g.setColor(Color.RED);
            g.fillOval((int) waypoint.getX() - 5, (int) waypoint.getY() - 5, 10, 10);
        }

        // if there are obstacles --> draw obstacles
        if (obstacles.size() != 0) {
            for (Obstacle obstacle : obstacles) {
                if (obstacle.getType().equalsIgnoreCase("water")) {
                    Color transparentBlue = new Color(0, 0, 255, 10); // 128 is the alpha value (0-255)
                    g.setColor(transparentBlue);
                    System.out.println("painting transparent");
                    g.fillOval((int) obstacle.getX(), (int) obstacle.getY(), obstacle.getSize(), obstacle.getSize());
                } else if (obstacle.getType().equalsIgnoreCase("tree")) {
                    g.setColor(Color.GREEN);
                    g.fillOval((int) obstacle.getX(), (int) obstacle.getY(), obstacle.getSize(), obstacle.getSize());
                }

            }
        }

    }




    ////////////////////////////////////// Writing output files ////////////////////////////////////////////////////////
    public void writeAnimalFrame() {
        String animalFramesTxtPath = "./Frames/AnimalFrames.txt";

//        if (filesCleared) {
            try (FileWriter animalFileWriter = new FileWriter(animalFramesTxtPath, true)) {
                Frame latestFrame = frames.get(frames.size() - 1);

                if (!latestFrame.getAnimals().isEmpty()) {
                    int boidNumber = 0;
                    for (Animal animal : latestFrame.getAnimals()) {
                        String currAnimalX = Float.toString(animal.getPosition().getX());
                        String currAnimalY = Float.toString(animal.getPosition().getY());
                        String currAnimalType = animal.getType();

                        animalFileWriter.write(currAnimalX + ", " + "0, " + currAnimalY + "\n");
                        boidNumber++;
                    }

                    animalFileWriter.write("\n");
                    animalFileWriter.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("Error writing animal frame: " + e.getMessage(), e);
            }
//        }
    }

    public void writeObstacles() {
        System.out.println("Writing obstacles");
        String obstacleFramesTxtPath = "./Frames/ObstacleFrames.txt";

        try (FileWriter obstacleFileWriter = new FileWriter(obstacleFramesTxtPath, true)) {
            if (!getObstacles().isEmpty()) {
                for (Obstacle obstacle : getObstacles()) {
                    String currObstacleX = Float.toString(obstacle.getLocation().getX());
                    String currObstacleY = Float.toString(obstacle.getLocation().getY());
                    String currObstacleType = obstacle.getType();

                    obstacleFileWriter.write(currObstacleX + ", " + "0, "+ currObstacleY +  "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing obstacles: " + e.getMessage(), e);
        }
    }








//////////////////////////////////////// Saving Obstacles Features ////////////////////////////////////////////////
    public void writeSavedFeaturesFile() {
    int dimX = TerrainLoader.getTerrainDimx();
    int dimY = TerrainLoader.getTerrainDimY();

    String directoryPath = (dimX > 613 && dimY > 613) ? "./Features/large" : "./Features/small";
    String fileName = "output.txt";

    try {
        // Create directories if they don't exist
        new File(directoryPath).mkdirs();

        String filePath = directoryPath + "/" + fileName;

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            if (!obstacles.isEmpty()) {
                for (Obstacle obstacle : obstacles) {
                    String obX = Float.toString(obstacle.getX());
                    String obY = Float.toString(obstacle.getY());
                    String type = obstacle.getType();
                    fileWriter.write(obX + ", " + obY + ", " + type + "\n");
                    System.out.println("Wrote obstacle: " + obX + ", " + obY);
                }
            } else {
                System.out.println("No obstacles on screen");
            }
        }

        System.out.println("File written successfully to: " + filePath);
    } catch (IOException e) {
        e.printStackTrace();
        System.err.println("Error writing to file.");
    }
}






////////////////////////////////////////// Load Obstacles Features ////////////////////////////////////////////////////////////////
    public void loadSavedObstacles(ArrayList<Obstacle> obAL){
        if (this.obstacles.isEmpty()) {
            System.out.println("Loaded Obstacles from features file");
            this.obstacles = obAL;
        }
        else
            System.out.println("Arraylist not empty");
    }

    public static String selectFile() {
        JFileChooser fileChooser = new JFileChooser();

        // Set the file chooser to open in the user's home directory by default
        fileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));

        int result = fileChooser.showOpenDialog(new JFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            System.out.println("File selected: " + fileChooser.getSelectedFile().getAbsolutePath());
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null; // User canceled the file selection
        }
    }

    public ArrayList<Obstacle> getSavedFeatruesAL(String filePath) {
        ArrayList<Obstacle> newObstacles= new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] obstacleData = line.split(", ");

                float obX = Float.parseFloat(obstacleData[0]);
                float obY = Float.parseFloat(obstacleData[1]);
                String type = obstacleData[2];

                Obstacle newObstacle = new Obstacle(new PVector(obX, obY), type);

                newObstacles.add(newObstacle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newObstacles;
    }




    ////////////////////////////////////////// Zooming Feature //////////////////////////////////////////////////////////////////
    public void zoomImage(BufferedImage originalImage, MouseWheelEvent e) {
        long currentTime = System.currentTimeMillis();

        // Check if the Control key is held down along with the mouse wheel scroll
        if (e.isControlDown()) {
            int wheelRotation = e.getWheelRotation();

            // Check if enough time has passed since the last zoom action
            if (currentTime - lastZoomTime >= ZOOM_COOLDOWN) {
                // Update the last zoom time
                lastZoomTime = currentTime;

                // Check if the mouse wheel was scrolled forward (positive rotation)
                if (wheelRotation > 0) {
                    zoomOut(originalImage);
                } else if (wheelRotation < 0) {
                    zoomIn(originalImage);
                }
            }
        }
        // If Control key is not held down or within the cooldown period, do nothing (no zooming)
    }

    private void zoomIn(BufferedImage image) {
        trackZoom.add(deepCopy(image));

        if (zoomIndex < 3) {
            zoomIndex++;
            System.out.println("Zooming in: zoomIndex = " + zoomIndex);

            for (Animal animal : animals)
                animal.increaseAnimalSize(2);

            for (Obstacle obstacle : obstacles)
                obstacle.increaseSize(2); // Increase obstacle size

            final int w = image.getWidth();
            final int h = image.getHeight();

            BufferedImage scaledImage = new BufferedImage((int) (w * 1.5), (int) (h * 1.5), BufferedImage.TYPE_INT_ARGB);

            final AffineTransform at = AffineTransform.getScaleInstance(1.5, 1.5); // Scale factor increased to zoom in
            final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);

            scaledImage = ato.filter(image, scaledImage);

            TerrainLoader.setBufferedImage(scaledImage);

            trackZoom.add(scaledImage);
        } else {
            System.out.println("Max Zoom Reached");
        }
    }

//    public void zoomOut(BufferedImage image){
//        if (zoomIndex > 0 & !trackZoom.isEmpty()){
//            zoomIndex--;
//            System.out.println("Zooming out: Zoom index = " + zoomIndex);
//            BufferedImage lastZoom = trackZoom.get(trackZoom.size()-1);
//            TerrainLoader.setBufferedImage(lastZoom);
//            trackZoom.remove(trackZoom.get(trackZoom.size()-1));
//        }
//        else if (zoomIndex == 0){
//            trackZoom.clear();
//        }
//
//    }

    private void zoomOut(BufferedImage image) {
        if (zoomIndex > 0) {
            zoomIndex--;
            for (Animal animal : animals)
                animal.increaseAnimalSize(-2);

            for (Obstacle obstacle : obstacles)
                obstacle.increaseSize(-2);

            final int w = image.getWidth();
            final int h = image.getHeight();
            BufferedImage scaledImage = new BufferedImage((int) (w / 1.5), (int) (h / 1.5), BufferedImage.TYPE_INT_ARGB); // Scale factor adjusted to zoom out
            final AffineTransform at = AffineTransform.getScaleInstance(1.0 / 1.5, 1.0 / 1.5); // Scale factor adjusted to zoom out
            final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
            scaledImage = ato.filter(image, scaledImage);
            TerrainLoader.setBufferedImage(scaledImage);
        } else {
            System.out.println("Max Zoom Reached");
        }
    }







//////////////////////////////////////// Media Controlls ///////////////////////////////////////////////////////////////

    public void fastFoward(){
        System.out.println("Animaiton speed: " + animationSpeed);
        if (animationSpeed > 5) {
            animationSpeed = animationSpeed - 5;
        }
        animationTimer.stop();
        startAnimation();
        getHorizontalPanel().setFastForwardButton(false);
    }

    public void slowDown(){
        System.out.println("Animaiton speed: " + animationSpeed);
        animationSpeed = animationSpeed + 5;
        animationTimer.stop();
        startAnimation();
        getHorizontalPanel().slowDownClicked = false;
    }

    public void resetSpeed(){
        System.out.println("Animaiton speed: " + animationSpeed);

        animationSpeed = 20;

        animationTimer.stop();
        startAnimation();
        getHorizontalPanel().setFastForwardButton(false);
    }



    public void startRewind() {
        if (horizontalPanel.isRewindClicked()) {
            // Ensure there are frames to rewind
            if (!frames.isEmpty()) {
                currentFrameIndex = frames.size() - 1;
                isRewinding = true;

                // Create a Swing Timer to schedule repaint requests
                rewindTimer = new Timer(animationSpeed, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (currentFrameIndex > 0) {
                            System.out.println("currFrameIndex: " + currentFrameIndex);
                            ArrayList<Animal> currFrameHerd = frames.get(currentFrameIndex).getAnimals();
                            ArrayList<Obstacle> currFrameObstacles = frames.get(currentFrameIndex).getObstacles();
                            setAnimals(currFrameHerd);
                            setObstacles(currFrameObstacles);
                            lastHerdPainted = currFrameHerd;
                            lastObstaclesPAinted = currFrameObstacles;
                            repaint();
                            currentFrameIndex--; // Move to the previous frame
                        } else {
                            beginningReachedMessage();
                            stopRewind();// Stop the rewind when all frames have been processed
                            System.out.println("Stopping rewind");
                        }
                    }
                });

                rewindTimer.start(); // Start the timer
            }
        }
    }

    // Add this method to stop the rewind process
    public void stopRewind() {
        if (rewindTimer != null && rewindTimer.isRunning()) {
            if (horizontalPanel.rewindClicked){  // if stopRewind called due to no more frames
                setAnimals(new ArrayList<Animal>());
                setObstacles(new ArrayList<Obstacle>());
                horizontalPanel.updateButtonState(horizontalPanel.getRewindButton(), horizontalPanel.rewindClicked, "Resume", "Rewind");
                frames.clear();
                setAnimalsOnScreen(false);
            }
            else if (!horizontalPanel.rewindClicked){ // stopRewind called due to user clicking rewind again
                setAnimals(lastHerdPainted);
                setObstacles(lastObstaclesPAinted);
                repaint();
            }
            rewindTimer.stop();
        }
        isRewinding = false;
        horizontalPanel.rewindClicked = false;

    }

    ///////////////////////////////////////////////// Listeners ////////////////////////////////////////////////////////////
    public void addMouseListeners(){
        // Listens for when the mouse is dragged
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                obstacles.add(new Obstacle(new PVector(e.getX(), e.getY()), getSettingsPanel().getObstacle().toLowerCase())); // Mouse dragged --> create obstacle
                setObstaclesOnScreen(true);
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // set waypoint with right click
                if (e.getButton() == MouseEvent.BUTTON3) {
                    waypoint = new PVector(e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    // Left click
                    if (!animalsOnScreen && !settingsPanel.animalsOnScreen) {
                        // If no animals on screen and not in settings panel
                        horizontalPanel.showAnimalRadioButtons(false);

                        int initialHerdSize = getSettingsPanel().getInitialHerdSizeValue();
                        String animalType = getSelectedAnimalType();

                        if (animalType != null) {
                            addAnimals(animalType, e.getX(), e.getY(), initialHerdSize);
                            selectAudio(animalType);

                        }
                    }
                    else if (animalsOnScreen) {
                        // If animals are already on screen
                        int initialHerdSize = getSettingsPanel().getInitialHerdSizeValue();
                        String animalType = currentlyOnScreen;
                        if (isValidHerSize(animalType)) {
                            addAnimals(animalType, e.getX(), e.getY(), initialHerdSize);
                            selectAudio(animalType);

                        }
                    }
                }
                repaint();
            }
        });

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                zoomImage(TerrainLoader.getBufferedImage(), e);

            }
        });

    }
    public void checkButtons(){
        // checks if clear button has been clicked after each update in order to clear the screen
        if (getSettingsPanel().getClearClicked()){
            obstacles.clear();
            animals.clear();
//            clearFile("Frames\\AnimalFrames.txt");
//            clearFile("Frames\\ObstacleFrames.txt");
            setWaypoint(null);
            animalsOnScreen = false;
            numberOfAnimalsOnScreen = 0;
            settingsPanel.setClearClicked(false);
            horizontalPanel.showAnimalRadioButtons(true);
            stopRewind();
            frames.clear();
        }

        // check if fast foward button has been clicked. Limits speed to 5
        if (getHorizontalPanel().isFastFowardClicked() && animationSpeed > 5){
            fastFoward();
            horizontalPanel.fastFowardClicked = false;
        }

        if(getHorizontalPanel().slowDownClicked){
            slowDown();
            horizontalPanel.slowDownClicked = false;
        }



        if(getHorizontalPanel().isRewindClicked() && !isRewinding){
            startRewind();
        }

        if(!getHorizontalPanel().isRewindClicked() && isRewinding){
            stopRewind();
        }

        if(getHorizontalPanel().resetClicked){
            resetSpeed();
            horizontalPanel.resetClicked = false;
        }


        if (horizontalPanel.getSavedFeaturesClicked()){
            writeSavedFeaturesFile();
            horizontalPanel.setSavedFeaturesClicked(false);
        }

        if(horizontalPanel.loadButtonClicked){
            System.out.println("Load clicked");
            String fileName = selectFile();
            if (fileName != null) {
                ArrayList<Obstacle> loadObstaclesAL = getSavedFeatruesAL(fileName);
                loadSavedObstacles(loadObstaclesAL);
            }
            else
                System.out.println("Selected null");


            repaint();
            horizontalPanel.loadButtonClicked = false;
            System.out.println("painting saved obstacles");
        }
    }







    ////////////////////////////////////// Helper Methods //////////////////////////////////////////////////////
    public static void clearFile(String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath, false);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void recordFrame(){
//        Frame frame = new Frame();
//        frame.setAnimals(getAnimals());
//        frame.setObstacles(getObstacles());
//        frames.add(frame);
//    }

    public void checkWaypoint(Animal animal){
        if (getSettingsPanel().getHerdBehaviour().equalsIgnoreCase("race")) {
            if (animal.wayPointReached(animal, getWaypoint()))
                setWaypoint(null); // removes waypoint
        }
    }


    // Helper function to get the selected animal type based on radio buttons
    private String getSelectedAnimalType() {
        if (getHorizontalPanel().getSheepRadioButton().isSelected()) {
            return "sheep";
        } else if (getHorizontalPanel().getPenguinRadioButton().isSelected()) {
            return "penguin";
        } else if (getHorizontalPanel().getElephantRadioButton().isSelected()) {
            return "elephant";
        }
        return null;
    }
    private boolean isValidHerSize(String animalType) {
        if (animalType.equalsIgnoreCase("sheep") && numberOfAnimalsOnScreen > 1000) {
            System.out.println("Sheep herds don't exceed 1000 animals");
            invalidHerdSizeMessage();
            return false;
        } else if (animalType.equalsIgnoreCase("penguin") && numberOfAnimalsOnScreen > 2000) {
            System.out.println("Penguins move in colonies that do not exceed 2000 animals");
            invalidHerdSizeMessage();
            return false;
        } else if (animalType.equalsIgnoreCase("elephant") && numberOfAnimalsOnScreen > 200) {
            System.out.println("Elephant herds do not exceed 200 animals");
            invalidHerdSizeMessage();
            return false;
        }
        return true;
    }

    public static BufferedImage deepCopy(BufferedImage source) {
        ColorModel cm = source.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = source.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    // Helper function to add animals to the list
    private void addAnimals(String animalType, int x, int y, int initialHerdSize) {
        for (int i = 0; i < initialHerdSize; i++) {
            Animal currAnimal = new Animal(animalType, x, y, getSettingsPanel(), obstacles, animalsOnScreen);
            currAnimal.setAnimalsOnScreen(true);
            animals.add(currAnimal);
            numberOfAnimalsOnScreen++;
            currentlyOnScreen = animalType;
            setAnimalsOnScreen(true);
        }
    }

/////////////////////////////////////// Warning Messages ///////////////////////////////////////////////////////////////
    public void invalidHerdSizeMessage(){
        if (this.getCurrentlyOnScreen().equalsIgnoreCase("sheep") & numberOfAnimalsOnScreen > 1000){
            JOptionPane.showMessageDialog(this, "Sheep herd do not exceed 1000 herd members", "Alert", JOptionPane.WARNING_MESSAGE);
        }
        if (this.getCurrentlyOnScreen().equalsIgnoreCase("penguin") & numberOfAnimalsOnScreen > 2000){
            JOptionPane.showMessageDialog(this, "Penguins move in colonies and do not exceed 2000 colony members", "Alert", JOptionPane.WARNING_MESSAGE);

        }
        if (this.getCurrentlyOnScreen().equalsIgnoreCase("elephant") & numberOfAnimalsOnScreen > 200){
            JOptionPane.showMessageDialog(this, "Elephant herds do not exceed 100 herd members", "Alert", JOptionPane.WARNING_MESSAGE);
            numberOfAnimalsOnScreen -= 1;
        }
    }

    public void selectAudio(String type) {
        if (type.equalsIgnoreCase("penguin"))
            playAudio("./Sounds/Penguin.wav");
        else if (type.equalsIgnoreCase("elephant"))
            playAudio("./Sounds/Elephant.wav");
        else if (type.equalsIgnoreCase("sheep"))
            playAudio("./Sounds/Sheep.wav");
    }




    public static void playAudio(String audioFilePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new java.io.File(audioFilePath));

            // Get the audio format
            AudioFormat audioFormat = audioInputStream.getFormat();

            // Create a data line for audio playback
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            // Open the line for playback
            line.open(audioFormat);

            // Start a separate thread for audio playback
            Thread playbackThread = new Thread(() -> {
                try {
                    line.start();
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                        line.write(buffer, 0, bytesRead);
                    }

                    // Close the line when playback is finished
                    line.drain();
                    line.stop();
                    line.close();
                    audioInputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Start the playback thread
            playbackThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void beginningReachedMessage(){
        JOptionPane.showMessageDialog(this, "Beginning of Simulation reached.", "Alert", JOptionPane.WARNING_MESSAGE);
    }






    // GETTERS AND SETTERS

    public ArrayList<Frame> getFrames() {
        return frames;
    }

    public void setFrames(ArrayList<Frame> frames) {
        this.frames = frames;
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(ArrayList<Animal> animals) {
        this.animals = animals;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public PVector getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(PVector waypoint) {
        this.waypoint = waypoint;
    }

    public SettingsPanel getSettingsPanel() {
        return settingsPanel;
    }

    public void setSettingsPanel(SettingsPanel settingsPanel) {
        this.settingsPanel = settingsPanel;
    }

    public HorizontalPanel getHorizontalPanel() {
        return horizontalPanel;
    }

    public void setHorizontalPanel(HorizontalPanel horizontalPanel) {
        this.horizontalPanel = horizontalPanel;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public Timer getAnimationTimer() {
        return animationTimer;
    }

    public void setAnimationTimer(Timer animationTimer) {
        this.animationTimer = animationTimer;
    }

    public boolean isAnimalsOnScreen() {
        return animalsOnScreen;
    }

    public void setAnimalsOnScreen(boolean animalsOnScreen) {
        this.animalsOnScreen = animalsOnScreen;
    }

    public int getNumberOfAnimalsOnScreen() {
        return numberOfAnimalsOnScreen;
    }

    public void setTrackZoom(ArrayList<BufferedImage> trackZoom) {
        this.trackZoom = trackZoom;
    }

    public void setStartImage(BufferedImage startImage) {
        this.startImage = startImage;
    }

    public void setNumberOfAnimalsOnScreen(int numberOfAnimalsOnScreen) {
        this.numberOfAnimalsOnScreen = numberOfAnimalsOnScreen;
    }

    public boolean isObstaclesOnScreen() {
        return obstaclesOnScreen;
    }

    public void setObstaclesOnScreen(boolean obstaclesOnScreen) {
        this.obstaclesOnScreen = obstaclesOnScreen;
    }

    public String getCurrentlyOnScreen() {
        return currentlyOnScreen;
    }

    public void setCurrentlyOnScreen(String currentlyOnScreen) {
        this.currentlyOnScreen = currentlyOnScreen;
    }

    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    public void setNumberOfFrames(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
    }











    @Override
    public void mouseDragged(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        //No functionality
    }
}
