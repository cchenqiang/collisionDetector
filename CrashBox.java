import java.util.List;

import javafx.scene.canvas.GraphicsContext;

/**
 * 碰撞盒
 */
public interface CrashBox {

    byte SHAPE_TYPE_CIRCLE = 1;
    byte SHAPE_TYPE_RECT = 2;
    CollisionDetector collisionDetector = new CollisionDetector() {
    };

    CollisionDetectResult collidesWith(CrashBox anotherShape);

    byte type();

    // 检查每个轴上投影的间隔，存在返回最小偏移量
    default CollisionDetectResult minimumTranslationVector(List<Vector> axes, CrashBox anotherShape) {
        double minOverlap = Double.MAX_VALUE;
        Vector axis;
        Projection projection1, projection2;
        double overlap = 0;
        Vector axisWithSmallOverlap = null;

        for (int i = 0; i < axes.size(); i++) {
            axis = axes.get(i);
            projection1 = this.project(axis);
            projection2 = anotherShape.project(axis);
            overlap = projection1.overlap(projection2);
            if (overlap < minOverlap || minOverlap == Double.MAX_VALUE) {
                axisWithSmallOverlap = axis;
                minOverlap = overlap;
            }
            if (overlap <= 0) break;
        }
        return new CollisionDetectResult(axisWithSmallOverlap, minOverlap);
    }

    List<Vector> getAxes();


    Projection project(Vector axis);


    void draw(GraphicsContext gc);


    void move(double x, double y);

    class CollisionDetectResult {
        public Vector axis;
        public double overlap;

        public CollisionDetectResult(Vector axis, double overlap) {
            this.axis = axis;
            this.overlap = overlap;
        }
    }

    double rotation();

    void rotate(double rotation);

    Rect rect();

    Vector centroid();

    Projection getProjection();

}
