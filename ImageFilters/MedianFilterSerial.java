import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


//iterate thru each pixel
//for each pixel add pixel value to AL
//get average ARGD colours for each pixel
//set initial pixel to averages of ARGB values

public class MedianFilterSerial {
    public static void main(String[] args)
    {
        MedianFilterSerial m = new MedianFilterSerial();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter image filename/filepath: ");
        String fileName = input.next();
        System.out.println("Enter output filename/filepath: ");
        String outputFile = input.next();
        System.out.println("Enter window size (must be an odd number): ");
        int windowSize = input.nextInt();



        while  (windowSize == 0 || windowSize%2 == 0)
        {
            System.out.println("WINDOW SIZE MUST BE ODD!");
            System.out.println("Enter window size: ");
            windowSize = input.nextInt();
        }
        long min = 999999999;
        for (int run = 0; run < 5; run++)
        {
            long startTime = System.currentTimeMillis();
            File imgFile = new File(fileName);
            BufferedImage img = null;
            try {
                img = ImageIO.read(imgFile);
            } catch (IOException e) {
                System.out.println("File not found!");
                System.exit(5);
            }


            //iterates through all pixels in an image inside the windowSize
            for (int row = windowSize; row < img.getWidth() - windowSize; row++) {
                for (int col = windowSize; col < img.getHeight() - windowSize; col++) {
                    {
                        //puts all surrounding pixels into separate AL for each colour
                        ArrayList<Integer> aSurroundings = m.getPixelValues(row, col, img, windowSize);
                        ArrayList<Integer> rSurroundings = m.getPixelValues(row, col, img, windowSize);
                        ArrayList<Integer> gSurroundings = m.getPixelValues(row, col, img, windowSize);
                        ArrayList<Integer> bSurroundings = m.getPixelValues(row, col, img, windowSize);

                        int b = m.getBlueMedian(bSurroundings);
                        int g = m.getGreenMedian(gSurroundings);
                        int r = m.getRedMedian(rSurroundings);
                        int a = m.getAvAlpha(aSurroundings);

                        int p = (a << 24) | (r << 16) | (g << 8) | b;
                        img.setRGB(row, col, p);
                    }
                }
            }
            //write file
            try {
                File output = new File(outputFile);
                ImageIO.write(img, "jpg", output);
            } catch (IOException e) {
                System.out.println("FIle now found");
            }

            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            long outputTime = time;
            if (time < min)
                min = outputTime;

            //print the performance
            System.out.println("Program took " + time + " milliseconds/ " + time / 1000 + " seconds to complete with "
                    + Runtime.getRuntime().availableProcessors() + " processors.");
        }
        System.out.println("Minimum time: " + min + " milliseconds");
    } //main

    //takes in co-ordinates for a pixel and adds all surrounding pixel values to an AL
    public ArrayList<Integer> getPixelValues(int x, int y, BufferedImage img, int windowSize)
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
    public  int getAvAlpha (ArrayList<Integer> al)
    {
        int size = al.size();
        int middle = size/2;

        for (int i = 0; i < size; i++)
        {
            int pv = al.get(i);
            int alpha = (pv>>24) & 0xff;
            al.set(i, alpha);
        }
        Collections.sort(al);

        return al.get(middle);
    }
    public int getRedMedian(ArrayList<Integer> al)
    {
        int size = al.size();
        int middle = size/2;

        for (int i = 0; i < size; i++)
        {
            int pv = al.get(i);
            int red = (pv>>16) & 0xff;
            al.set(i, red);
        }
        Collections.sort(al);

        return al.get(middle);
    }
    public int getGreenMedian(ArrayList<Integer> al)
    {
        int size = al.size();
        int middle = size/2;

        for (int i = 0; i < size; i++)
        {
            int pv = al.get(i);
            int green = (pv>>8) & 0xff;
            al.set(i, green);
        }
        Collections.sort(al);

        return al.get(middle);
    }
    public int getBlueMedian(ArrayList<Integer> al)
    {
        int size = al.size();
        int middle = size/2;

        for (int i = 0; i < size; i++)
        {
            int pv = al.get(i);
            int blue = pv & 0xff;
            al.set(i, blue);
        }
        Collections.sort(al);

        return al.get(middle);
    }
}