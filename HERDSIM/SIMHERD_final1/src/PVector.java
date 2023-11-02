/*
Yachyaa Toefy (TFYYAC001)
Luke Clayton (CLYLUK001)
Fabio O'R Paulo (ORYFAB001)

PVector.java
This class is responsible for creating PVectors which are responsible for storing values
such as velocity, position etc. This clas also manages all teh vector calculations that take
place within the program.
 */

public class PVector {
    private float x;
    private float y;

    public PVector(float x, float y) {
        setX(x);
        setY(y);
    }

    public void add(PVector v) {
        x += v.x;
        y += v.y;
    }

    public void sub(PVector v) {
        x -= v.x;
        y -= v.y;
    }

    public void mult(float scalar) {
        x *= scalar;
        y *= scalar;
    }

    public float mag() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public boolean isZero(){
        return (this.mag() == 0);
    }

    // convert vector to a unit vector
    public void normalize() {
        float m = mag();
        if (m != 0) {
            x /= m;
            y /= m;
        }
    }

    public void div(float scalar) {
        x /= scalar;
        y /= scalar;
    }

    public float angle() {
        // Calculate the angle in radians using the arctangent function
        float angle = (float) Math.atan2(y, x);

        // Ensure the angle is between 0 and 2*PI (0 to 360 degrees)
        if (angle < 0) {
            angle += 2 * Math.PI;
        }

        return angle;
    }

    public static PVector sub(PVector v1, PVector v2) {
        return new PVector(v1.x - v2.x, v1.y - v2.y);
    }
    public static PVector add(PVector v1, PVector v2) {
        return new PVector(v1.x + v2.x, v1.y + v2.y);
    }
    public static PVector mult(PVector v, float m){
        return new PVector(v.x * m, v.y*m);
    }

    // Limits magnitude of vector to a maximum
    public void limit(float max) {
        float mSq = x * x + y * y;
        if (mSq > max * max) {
            normalize();
            mult(max);
        }
    }

    // Returns direction of vectr (Radians)
    public float heading() {
        return (float) Math.atan2(y, x);
    }

    // Calculates distance between 2 vectors
    public float dist(PVector v) {
        float dx = x - v.x;
        float dy = y - v.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
