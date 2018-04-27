public class Projection {

    double min;
    double max;

    public Projection(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public Projection() {
    }

    public double overlap(Projection projection) {
        return Math.min(this.max, projection.max) - Math.max(this.min, projection.min);
    }

    public void setMinMax(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        return "Projection{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
