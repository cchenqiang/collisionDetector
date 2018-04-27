import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

/**
 * 有方向的碰撞盒
 * <p>
 * <p>
 * 为了解决在四叉树中的 粗略检测， 为该碰撞盒，创建一个AABB盒，用于场景中
 */
public class OBB implements CrashBox {

    Vector centerPoint;

    //四个顶点的坐标
    Vector[] points = new Vector[4];

    private double halfWidth;
    private double halfHeight;
    //轴向量
    List<Vector> axes = new ArrayList<>();
    /**
     * 旋转度数
     */
    private double rotation;

    //先用这个盒做粗略判断
    private Rect aabb;

    public OBB() {
        points = new Vector[4];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector();
        }
        aabb = new Rect();
        axes.add(new Vector());
        axes.add(new Vector());
        this.centerPoint = new Vector();
    }

    @Override
    public void move(double x, double y) {
        this.centerPoint.setX(this.centerPoint.getX() + x);
        this.centerPoint.setY(this.centerPoint.getY() + y);
        for (Vector point : this.points) {
            point.setX(point.getX() + x);
            point.setY(point.getY() + y);
        }
        aabb.moveTo(aabb.getX() + x, aabb.getY() + y);
    }

    @Override
    public double rotation() {
        return rotation;
    }

    public OBB(Vector center, double width, double height, double rotation) {
        this();
        this.centerPoint = center;
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;
        rotate(rotation);
    }

    public void init(double x, double y, double width, double height, double rotation) {
        this.centerPoint.setX(x);
        this.centerPoint.setY(y);
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;
        rotate(rotation);
    }

    public void rotate(double rotation) {
        this.rotation = rotation;
        Vector vector = this.axes.get(0);
        vector.setX(Math.sin(rotation));
        vector.setY(Math.cos(rotation));
        vector = this.axes.get(1);
        vector.setX(-Math.cos(rotation));
        vector.setY(Math.sin(rotation));
        setPoints(rotation);
    }

    /**
     * https://www.cnblogs.com/MachineVision/p/5778677.html
     * 设置每个顶点的坐标，和包围该形状的AABB盒
     *
     * @param rotation
     */
    public void setPoints(double rotation) {
        double minX = centerPoint.getX() - halfWidth;
        double minY = centerPoint.getY() + halfHeight;
        double maxX = centerPoint.getX() + halfWidth;
        double maxY = centerPoint.getY() - halfHeight;


        minX = minX - centerPoint.getX();
        minY = minY - centerPoint.getY();
        maxX = maxX - centerPoint.getX();
        maxY = maxY - centerPoint.getY();
        double cos = Math.cos(rotation);
        double sin = Math.sin(rotation);
        //(minX,minY)
        points[0].setX(minX * cos + minY * sin + centerPoint.getX());
        points[0].setY(-minX * sin + minY * cos + centerPoint.getY());
        //(maxX,minY)
        points[1].setX(maxX * cos + minY * sin + centerPoint.getX());
        points[1].setY(-maxX * sin + minY * cos + centerPoint.getY());
        //(maxX,maxY)
        points[2].setX(maxX * cos + maxY * sin + centerPoint.getX());
        points[2].setY(-maxX * sin + maxY * cos + centerPoint.getY());
        //(minX,maxY)
        points[3].setX(minX * cos + maxY * sin + centerPoint.getX());
        points[3].setY(-minX * sin + maxY * cos + centerPoint.getY());

        minX = Math.min(points[0].getX(), points[1].getX());
        minX = Math.min(minX, points[2].getX());
        minX = Math.min(minX, points[3].getX());

        maxX = Math.max(points[0].getX(), points[1].getX());
        maxX = Math.max(maxX, points[2].getX());
        maxX = Math.max(maxX, points[3].getX());


        minY = Math.min(points[0].getY(), points[1].getY());
        minY = Math.min(minY, points[2].getY());
        minY = Math.min(minY, points[3].getY());

        maxY = Math.max(points[0].getY(), points[1].getY());
        maxY = Math.max(maxY, points[2].getY());
        maxY = Math.max(maxY, points[3].getY());

        aabb.reset(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public CollisionDetectResult collidesWith(CrashBox anotherShape) {
        if (anotherShape.type() == SHAPE_TYPE_CIRCLE) {
            return collisionDetector.polygonCollidesWithCircle(this, (Circle) anotherShape);
        } else if (anotherShape.type() == SHAPE_TYPE_RECT) {
            return collisionDetector.polygonCollidesWithPolygon(this, (OBB) anotherShape);
        }
        return null;
    }

    @Override
    public byte type() {
        return SHAPE_TYPE_RECT;
    }

    @Override
    public List<Vector> getAxes() {
        return axes;
    }

    Projection projection = new Projection();

    @Override
    public Projection project(Vector axis) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double e;
        for (Vector point : this.points) {
            e = point.dotProduct(axis);
            if (e < min || min == Double.MAX_VALUE) {
                min = e;
            }
            if (e > max || max == Double.MIN_VALUE) {
                max = e;
            }
        }
        projection.setMinMax(min, max);
        return projection;
    }


    @Override
    public Rect rect() {
        return aabb;
    }

    public Vector centroid() {
        return centerPoint;
    }
//
//    public double getProjectionRadius(Vector axis) {
//        return this.halfWidth * Math.abs(axis.dotProduct(this.axes[0])) + this.halfHeight * Math.abs(axis.dotProduct(this.axes[1]));
//    }


    public static void main(String[] args) {
        OBB obb = new OBB();
        double rotation = 45 * Math.PI / 180;
        obb.init(0, 0, 100, 100, rotation);
        System.out.println(rotation);
        double v = Math.toRadians(45);

        System.out.println(v);
        System.out.println("");
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.strokeLine(points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY());
        gc.strokeLine(points[1].getX(), points[1].getY(), points[2].getX(), points[2].getY());
        gc.strokeLine(points[2].getX(), points[2].getY(), points[3].getX(), points[3].getY());
        gc.strokeLine(points[0].getX(), points[0].getY(), points[3].getX(), points[3].getY());
        gc.strokeRect(aabb.getX(), aabb.getY(), aabb.getW(), aabb.getH());
    }

    @Override
    public Projection getProjection() {
        return projection;
    }
}
