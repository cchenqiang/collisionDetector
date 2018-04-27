import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

/**
 * 圆
 */
public class Circle implements CrashBox {
    Vector center;//中心点
    double radius;//半径
    Rect rect;//粗略碰撞盒
    Projection projection = new Projection();//投影
    double rotation;
    List<Vector> axes = new ArrayList<>();

    {
        axes.add(new Vector());
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public byte type() {
        return SHAPE_TYPE_CIRCLE;
    }

    public Vector centroid() {
        return center;
    }

    public Circle(double x, double y, double radius, double rotation) {
        this.rotation = rotation;
        this.center = new Vector(x, y);
        this.radius = radius;
        rect = new Rect();
        rect.resize(2 * radius, 2 * radius);
        setRect();
    }

    private void setRect() {
        rect.moveTo(this.center.x - radius, this.center.y - radius);
    }

    @Override
    public CollisionDetectResult collidesWith(CrashBox anotherShape) {
        if (anotherShape.type() == SHAPE_TYPE_CIRCLE) {
            return collisionDetector.circleCollidesWithCircle(this, (Circle) anotherShape);
        } else if (anotherShape.type() == SHAPE_TYPE_RECT) {
            return collisionDetector.polygonCollidesWithCircle((OBB) anotherShape, this);
        }
        return null;
    }

    @Override
    public List<Vector> getAxes() {
        return axes;
    }

    @Override
    public Projection project(Vector axis) {

        double dotProduct = centroid().dotProduct(axis);
        projection.setMinMax(dotProduct - this.radius, dotProduct + this.radius);
        return projection;
    }

    @Override
    public void move(double x, double y) {
        this.center.setX(this.centroid().getX() + x);
        this.center.setY(centroid().getY() + y);
        this.setRect();
    }

    @Override
    public double rotation() {
        return rotation;
    }

    @Override
    public void rotate(double rotation) {
        this.rotation = rotation;
    }

    @Override
    public Rect rect() {
        return rect;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.strokeOval(rect.getX(), rect.getY(), rect.getW(), rect.getH());
        gc.strokeRect(rect.getX(), rect.getY(), rect.getW(), rect.getH());
    }

    @Override
    public Projection getProjection() {
        return projection;
    }
}
