public class Vector {

    double x;

    double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector() {
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector subtract(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }

    public double dotProduct(Vector v) {
        return this.x * v.x + this.y * v.y;
    }

    public double getMagnitude() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public Vector add(Vector vector) {
        return new Vector(this.x + vector.x, this.y + vector.y);
    }


    public Vector edge(Vector vector) {
        return this.subtract(vector);
    }

    public Vector prependicular() {
        return new Vector(this.y, -this.x);
    }

    // 获取单位向量
    public Vector normalize() {
        Vector v = new Vector(0, 0);
        double m = this.getMagnitude();
        if (m != 0) {
            v.x = this.x / m;
            v.y = this.y / m;
        }
        return v;
    }

    // 获取单位向量
    public Vector normalizeSelf() {
        double m = this.getMagnitude();
        if (m != 0) {
            this.x = this.x / m;
            this.y = this.y / m;
        }
        return this;
    }


    // 获取法向量
    public Vector normal() {
        return this.prependicular().normalize();
    }

}
