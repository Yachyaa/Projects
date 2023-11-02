import javax.sound.midi.SysexMessage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class HungryMover extends Thread {

    private HungryWord hWord;
    private AtomicBoolean done;
    private AtomicBoolean pause;
    private Score score;
    CountDownLatch startLatch; //so all can start at once


    HungryMover( HungryWord word) {
        hWord = word;
    }

    HungryMover( HungryWord word,WordDictionary dict, Score score,
               CountDownLatch startLatch, AtomicBoolean d, AtomicBoolean p) {
        this(word);
        this.startLatch = startLatch;
        this.score=score;
        this.done=d;
        this.pause=p;
    }


    public void run() {
        //System.out.println(myWord.getWord() + " falling speed = " + myWord.getSpeed());
        try {
            System.out.println("HungryWord : " + hWord.getWord() + "waiting to start " );
            startLatch.await();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } //wait for other threads to start
        System.out.println(hWord.getWord() + " started" );
        while (!done.get()) {
            //animate the word
            while (!hWord.endReached() && !done.get())
            {
                hWord.slideAcross(10);
                try {
                    sleep(hWord.getSpeed());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                while(pause.get()&&!done.get()) {};
            }
            if (!done.get() && hWord.endReached()) {
                score.missedWord();
                hWord.resetWord();
            }
            hWord.resetWord();
        }
    }
}
