
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
		private AtomicBoolean done ; //REMOVE
		private AtomicBoolean started ; //REMOVE
		private AtomicBoolean won ;//REMOVE

		private HungryWord hw;
		private FallingWord[] words;

		private int noWords;
		private final static int borderWidth=25; //appearance - border

		GamePanel(FallingWord[] words, HungryWord word, int maxY,
				 AtomicBoolean d, AtomicBoolean s, AtomicBoolean w) {
			this.words=words; //shared word list
			this.hw = word;
			noWords = words.length; //only need to do this once
			done=d; //REMOVE
			started=s; //REMOVE
			won=w; //REMOVE
		}
		
		public void paintComponent(Graphics g) {
		    int width = getWidth()-borderWidth*2;
		    int height = getHeight()-borderWidth*2;
		    g.clearRect(borderWidth,borderWidth,width,height);//the active space
		    g.setColor(Color.pink); //change colour of pen
		    g.fillRect(borderWidth,height,width,borderWidth); //draw danger zone

		    g.setColor(Color.black);
		    g.setFont(new Font("Arial", Font.PLAIN, 26));
		   //draw the words
		    if (!started.get()) {
		    	g.setFont(new Font("Arial", Font.BOLD, 26));
				g.drawString("Type all the words before they hit the red zone,press enter after each one.",borderWidth*2,height/2);	
		    	
		    }
		    else if (!done.get()) {
				g.setColor(Color.green);
				g.drawString(hw.getWord() ,hw.getX() + borderWidth, hw.getY());
				//System.out.println("game pannel word is " + hw.getWord());
				g.setColor(Color.black);

		    	for (int i=0;i<noWords;i++){	    	
		    		g.drawString(words[i].getWord(),words[i].getX()+borderWidth,words[i].getY());	
		    	}


		    	g.setColor(Color.lightGray); //change colour of pen
		    	g.fillRect(borderWidth,0,width,borderWidth);

				//g.drawString(hungryWord.getWord(), hungryWord.getX(), hungryWord.getY());
				g.setColor(Color.green);
		   }
		   else { if (won.get()) {
			   g.setFont(new Font("Arial", Font.BOLD, 36));
			   g.drawString("Well done!",width/3,height/2);	
		   } else {
			   g.setFont(new Font("Arial", Font.BOLD, 36));
			   g.drawString("Game over!",width/2,height/2);	
		   }
		   }
		}
		
		public int getValidXpos() {
			int width = getWidth()-borderWidth*4;
			int x= (int)(Math.random() * width);
			return x;
		}
		public int getValidYpos()
		{
			int height = getHeight() - borderWidth * 4;
			int y = (int)(Math.random() * height);
			return y;
		}
		
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(10); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				};
			}
		}

	}


