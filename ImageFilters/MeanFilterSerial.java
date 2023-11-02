import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

//iterate thru each pixel
//for each pixel add pixel value to AL
//get average ARGD colours for each pixel
//set initial pixel to averages of ARGB values

public class MeanFilterSerial {
    public static void main(String[] args)
    {
        MeanFilterSerial m = new MeanFilterSerial();
        Scanner input = new Scanner(System.in);

        System.out.println("Enter image filename/filepath: ");
        String fileName = input.next();
        System.out.println("Enter output filename/filepath: ");
        String outputFile = input.next();
        System.out.println("Enter window size (must be an odd number): ");
        int windowSize = input.nextInt();


        long startTime = System.currentTimeMillis();

        while  (windowSize == 0 || windowSize%2 == 0)
        {
            System.out.println("WINDOW SIZE MUST BE ODD!");
            System.out.println("Enter window size: ");
            windowSize = input.nextInt();
        }
        long min = 999999999;

        for (int i = 0; i<5; i++)
        {
            File imgFile = new File(fileName);
            BufferedImage img = null;
            try {
                img = ImageIO.read(imgFile);
            } catch (IOException e) {
                System.out.println("File not found");
                System.exit(5);
            }
            //iterates through all pixels in an image

            long outputTime = 0;
            long start = System.currentTimeMillis();
            for (int row = windowSize; row < img.getWidth() - windowSize; row++) {
                for (int col = windowSize; col < img.getHeight() - windowSize; col++) {
                    ArrayList<Integer> pixelValues = m.getPixelValues(row, col, img, windowSize);
                    int a = getAvAlpha(pixelValues);
                    int r = m.getAvRed(pixelValues);
                    int g = m.getAvGreen(pixelValues);
                    int b = m.getAvBlue(pixelValues);
                    int p = (a << 24) | (r << 16) | (g << 8) | b;
                    img.setRGB(row, col, p);
                }
            }
            try {
                File output = new File(outputFile);
                ImageIO.write(img, "jpg", output);
            } catch (IOException e) {
                System.out.println("FIle now found");
            }

            long end = System.currentTimeMillis();
            long time = end - start;
            outputTime = time;

            if (outputTime<min)
                min = outputTime;

            System.out.println("Program took " + time + " milliseconds/" + (float) time / 1000 + " seconds to complete with "
                    + Runtime.getRuntime().availableProcessors() + " processors.");
        }

        System.out.println("Minimum time: " + min + " milliseconds.");
    } //main

    //takes in co-ordinates for a pixel and adds all surrounding pixel values to AL
    public ArrayList<Integer> getPixelValues(int x, int y, BufferedImage img, int windowSize )
    {
        ArrayList<Integer> colours = new ArrayList<>();

        for (int i = -windowSize; i < windowSize + 1; i++)
        {
            for (int j = -windowSize; j < windowSize + 1; j++)
            {
                colours.add(img.getRGB(x + i, y + j));
            }
        }
        return colours;
    }
    public static int getAvAlpha (ArrayList<Integer> al)
    {
        int alphaTot = 0;

        for (int i=0; i<al.size(); i++)
        {
            int pixelVal = al.get(i);
            alphaTot +=  (pixelVal>>24) & 0xff;
        }

        return alphaTot/al.size();
    }

    //Gets the average of the red pixels in AL
    public int getAvRed (ArrayList<Integer> al)
    {
        int redTot = 0;

        for (int i=0; i<al.size(); i++)
        {
            int pixelVal = al.get(i);
            redTot +=  (pixelVal>>16) & 0xff;
            //System.out.println(al);
        }

        return redTot/al.size() ;
    }

    public int getAvGreen (ArrayList<Integer> al)
    {
        int greenTot = 0;

        for (int i=0; i<al.size(); i++)
        {
            int pixelVal = al.get(i);
            greenTot +=  (pixelVal>>8) & 0xff;
        }

        return greenTot/al.size() ;
    }

    public int getAvBlue (ArrayList<Integer> al)
    {
        int blueTot = 0;

        for (int i=0; i<al.size(); i++)
        {
            int pixelVal = al.get(i);
            blueTot +=  pixelVal & 0xff;
        }

        return blueTot/al.size() ;
    }

}