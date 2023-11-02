import java.util.Random;

public class HungryWord {
    String word;

    FallingWord fallingWord;
    int x;
    int y;
    int maxX;
    boolean end;
    int speed;
    int maxWait = 1000;
    int minWait = 100;
   static  WordDictionary dict;

    HungryWord ()
    {
        word = "HungryWord";
        x = 0;
        y = 0;
        maxX = 300;
        end = false;
        speed = (int)(Math.random() * (maxWait - minWait) + minWait);
    }

    HungryWord (String word)
    {
        this();
        this.word = word;
    }

    HungryWord(String word, int y, int maxX)
    {
        this(word);
        this.y = y;
        this.maxX = maxX;
    }

    public void increaseSpeed()
    {
        maxWait += 50;
        minWait += 50;
    }

    public void resetSpeed()
    {
        maxWait += 1000;
        minWait += 100;
    }

    public synchronized  void setY(int y) {
        this.y = y;
    }

    public synchronized  void setWord(String text) {
        this.word=text;
    }

    public synchronized  String getWord() {
        return word;
    }

    public synchronized  int getX() {
        return x;
    }
    public synchronized  int getY() {
        return y;
    }
    public synchronized void  slideAcross(int slide)
    {
        setX((x + slide));
        //System.out.println("Sliding in method || x value of " + getWord() +  "is " + this.getX());
    }

    public synchronized int getSpeed() {return this.speed;}


    public synchronized void setX(int x)
    {
        if (x > maxX)
        {
            x = this.maxX;
            end = true;
        }
        this.x = x;
    }

    public synchronized void setPosition(int x, int y)
    {
        setX((x));
        this.y = y;
    }

    public void resetPosition()
    {
        this.x = 0;
    }

    public synchronized void resetWord()
    {
        resetPosition();
        word = dict.getNewWord();
    }

    public synchronized boolean endReached()
    {
        return end;

    }

    public synchronized boolean matchWord(String typedText) {
        //System.out.println("Matching against: "+text);
        if (typedText.equals(this.word))
        {
            resetWord();
            return true;
        }
        else
            return false;
    }

    public synchronized void caught()
    {
        setPosition(0, 0);
    }

    public boolean touched (FallingWord fw)
    {
        int low = -50 ;
        int hi = 50;
        int xDiff =  this.getX() - fw.getX();
        int yDiff = this.getY() - fw.getY();

        System.out.println(fw.getWord() + " xDiff = " + xDiff + " and yDiff = " + yDiff);

        if (xDiff > low && xDiff < hi && yDiff >low && yDiff < hi)
        {
            //System.out.println(fallingWord.getWord() + " TOUCHED");
            fw.resetWord();
            return true;
        }
        else
            return false;
    }

}
