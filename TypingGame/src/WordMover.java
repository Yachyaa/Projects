

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WordMover extends Thread {
	private FallingWord myWord;
	HungryWord hword;
	private AtomicBoolean done;
	private AtomicBoolean pause; 
	private Score score;
	CountDownLatch startLatch; //so all can start at once
	
	WordMover( FallingWord word) {
		myWord = word;
	}
	
	WordMover( FallingWord word, HungryWord hw,  WordDictionary dict, Score score,
			CountDownLatch startLatch, AtomicBoolean d, AtomicBoolean p) {
		this(word);
		this.hword = hw;
		this.startLatch = startLatch;
		this.score=score;
		this.done=d;
		this.pause=p;
	}
	
	
	
	public void run() {
		//System.out.println(myWord.getWord() + " falling speed = " + myWord.getSpeed());
		try {
			System.out.println(myWord.getWord() + " waiting to start " );
			startLatch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //wait for other threads to start
		System.out.println(myWord.getWord() + " started" );
		while (!done.get()) {				
			//animate the word
			while (!myWord.dropped() && !done.get())
			{
				if (!hword.touched(myWord))
				{
					myWord.drop(10);
					//System.out.println(myWord.getWord() +  " NOT TOUCHED and incrementned. myWord co-ordinates = (" +
					//		myWord.getX() + ", " + myWord.getY() +  ") and hWord co-ords = (" + hword.getX() + ", " + hword.getY() + ")" );
					try {
						sleep(myWord.getSpeed());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					while (pause.get() && !done.get()) {
					}
					;
				}
				else
				{
					score.missedWord();
					myWord.resetWord();
				}
			}
			if (!done.get() && myWord.dropped()) {
				score.missedWord();
				myWord.resetWord();
			}
			myWord.resetWord();
		}
	}
	
}
