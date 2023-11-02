

import java.util.concurrent.atomic.AtomicBoolean;

//Thread to monitor the word that has been typed.
public class CatchWord extends Thread {
	String target;
	static AtomicBoolean done ; //REMOVE
	static AtomicBoolean pause; //REMOVE
	
	private static  FallingWord[] words; //list of words

	static HungryWord hWord;
	private static int noWords; //how many
	private static Score score; //user score
	
	CatchWord(String typedWord) {
		target=typedWord;
	}
	
	public static void setWords(FallingWord[] wordList, HungryWord hw) {
		words=wordList;
		hWord = hw;
		noWords = words.length;
	}
	
	public static void setScore(Score sharedScore) {
		score=sharedScore;
	}
	
	public static void setFlags(AtomicBoolean d, AtomicBoolean p) {
		done=d;
		pause=p;
	}
	
	public void run() {
		int i=0;
		while (i<noWords) {		
			while(pause.get()) {};
			if (words[i].matchWord(target)) {
				int index = i;
				int yMax = words [i].getY();
				for (int j = 0 ; j < words.length; j++)
				{
					if (words[j].matches(target) && words[j].getY() >= yMax)
					{
						yMax = words[j].getY();
						index = j;
					}
				}

				words[index].matchWord(target);
				System.out.println( " score! '" + target); //for checking
				score.caughtWord(target.length());	
				//FallingWord.increaseSpeed();
				break;
			}

			if (hWord.matchWord(target))
			{
				System.out.println("HunryWord reset");
				score.caughtWord(target.length());
				hWord.caught();

			}
		   i++;
		}
		
	}	
}
