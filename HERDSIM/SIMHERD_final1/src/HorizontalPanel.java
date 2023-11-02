import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HorizontalPanel extends JPanel {
    private JButton pauseButton;
    private JButton rewindButton;
    private JButton fastForwardButton;
    private JButton resetButton;
    private JButton saveFeaturesButton;
    private JButton slowDown;
    private JButton loadButton;


    private JLabel animalLabel;
    private JRadioButton penguinRadioButton;
    private JRadioButton elephantRadioButton;
    private JRadioButton sheepRadioButton;

    public boolean pauseClicked = false;
    public boolean rewindClicked = false;
    public boolean fastFowardClicked = false;
    public boolean resetClicked = false;
    public boolean savedFeaturesClicked = false;
    public boolean slowDownClicked = false;
    public boolean loadButtonClicked = false;

    public HorizontalPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(2, 4, 10, 10)); // Use GridLayout to organize components
        createButtons();
    }


    ////////////////////////////////////// Creating Buttons ////////////////////////////////////////////////////////////

    public void createButtons(){
        // initialize buttons
        pauseButton = new JButton("Pause");
        rewindButton = new JButton("Rewind");
        fastForwardButton = new JButton("Fast Forward");
        resetButton = new JButton("Reset Speed");
        saveFeaturesButton = new JButton("Save Features");
        slowDown = new JButton("Slow Down");
        loadButton = new JButton("Load");

        // initialize radio buttons
        animalLabel = new JLabel("Select Animal:");
        penguinRadioButton = new JRadioButton("Penguin");
        elephantRadioButton = new JRadioButton("Elephant");
        sheepRadioButton = new JRadioButton("Sheep");

        // create button group for radio buttons
        ButtonGroup animalGroup = new ButtonGroup();
        animalGroup.add(penguinRadioButton);
        animalGroup.add(elephantRadioButton);
        animalGroup.add(sheepRadioButton);

        // set starting value for radio buttons
        penguinRadioButton.setSelected(false);
        sheepRadioButton.setSelected(true);
        elephantRadioButton.setSelected(false);


        addActionListeners();


        // Add buttons and radio buttons to the panel
        add(rewindButton);
        add(pauseButton);
        add(fastForwardButton);
        add(slowDown);
        add(resetButton);
        add(saveFeaturesButton);
        add(loadButton);
        add(animalLabel); // Label for radio buttons
        add(sheepRadioButton);
        add(penguinRadioButton);
        add(elephantRadioButton);
    }


    ////////////////////////////////////////// Action Listeners ////////////////////////////////////////////////////////


    public void addActionListeners(){
        // Add action listeners to the buttons
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseClicked = !pauseClicked;
                updateButtonState(pauseButton, pauseClicked, "Pause", "Resume");
            }
        });

        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rewindClicked = !rewindClicked;
                updateButtonState(rewindButton, rewindClicked, "Rewind", "Resume");

            }
        });

        fastForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fastFowardClicked = true;
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadButtonClicked = true;
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetClicked = true;
            }
        });

        slowDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slowDownClicked = true;
            }
        });

        saveFeaturesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Save features clicked");
                savedFeaturesClicked = true;
            }
        });
    }


    /////////////////////////////////////////// Helper Methods /////////////////////////////////////////////////////////



    // Helper method to update button text based on state
    public void updateButtonState(JButton button, boolean clicked, String start, String change) {
        button.setText(clicked ? change : start);
    }


    ////////////////////////////////////////// Getters and Setters /////////////////////////////////////////////////////


    public boolean isPauseClicked(){
        return pauseClicked;
    }
    public boolean isRewindClicked(){
        return rewindClicked;
    }
    public boolean isFastFowardClicked(){
        return fastFowardClicked;
    }

    public JButton getFastForwardButton() {
        return fastForwardButton;
    }

    public void setPauseButton(JButton pauseButton) {
        this.pauseButton = pauseButton;
    }

    public void setResetButton(boolean clicked) {
        this.resetClicked = clicked;
    }

    public void setFastForwardButton(boolean clicked) {
        this.fastFowardClicked = clicked;
    }

    public void setRewindClicked(boolean clicked) {
        this.rewindClicked = clicked;
    }

    public void setPauseButton(boolean clicked) {
        this.pauseClicked = clicked;
    }



    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public JButton getRewindButton() {
        return rewindButton;
    }

    public JButton getSaveFeaturesButton() {
        return saveFeaturesButton;
    }

    public boolean getSavedFeaturesClicked(){
        return savedFeaturesClicked;
    }

    public void setSavedFeaturesClicked(boolean savedFeaturesClicked) {
        this.savedFeaturesClicked = savedFeaturesClicked;
    }

    public JRadioButton getElephantRadioButton() {
        return elephantRadioButton;
    }

    public JRadioButton getPenguinRadioButton() {
        return penguinRadioButton;
    }

    public JRadioButton getSheepRadioButton() {
        return sheepRadioButton;
    }

    public void showAnimalRadioButtons(boolean b){

        this.sheepRadioButton.setVisible(b);
        this.elephantRadioButton.setVisible(b);
        this.penguinRadioButton.setVisible(b);
        this.animalLabel.setVisible(b);

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Horizontal Panel Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new HorizontalPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
