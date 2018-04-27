import java.util.ArrayList;
import java.util.List;

public interface CollisionDetector {

    /**
     * 多边形与圆碰撞
     *
     * @param polygon
     * @param circle
     * @return
     */
    default CrashBox.CollisionDetectResult polygonCollidesWithCircle(OBB polygon, Circle circle) {
        List<Vector> axes = new ArrayList<>(polygon.getAxes());
        Vector closestPoint = getPolygonPointClosestToCircle(polygon, circle);

        Vector vector = circle.getAxes().get(0);
        vector.setX(closestPoint.x - circle.center.getX());
        vector.setY(closestPoint.y - circle.center.getY());
        axes.add(vector.normalizeSelf());
        return polygon.minimumTranslationVector(axes, circle);
    }

    /**
     * 多边形与多边形碰撞
     */
    default CrashBox.CollisionDetectResult polygonCollidesWithPolygon(OBB p1, OBB p2) {
        List<Vector> axes = new ArrayList<>(p1.getAxes());
        axes.addAll(p2.getAxes());
        return p1.minimumTranslationVector(axes, p2);
    }

    /**
     * 圆与圆碰撞
     */
    default CrashBox.CollisionDetectResult circleCollidesWithCircle(Circle circle1, Circle circle2) {
        return new CrashBox.CollisionDetectResult(new Vector(circle1.center.x - circle2.center.x, circle1.center.y - circle2.center.y),
                circle1.radius + circle2.radius - Math.sqrt(Math.pow(circle1.center.x - circle2.center.x, 2) + Math.pow(circle1.center.y - circle2.center.y, 2))
        );
    }

    /**
     * 获取多边形里圆最近的顶点
     *
     * @param polygon
     * @param circle
     * @return
     */
    default Vector getPolygonPointClosestToCircle(OBB polygon, Circle circle) {
        double min = Double.MAX_VALUE;
        Vector closestPoint = null;
        double length;

        for (Vector point : polygon.points) {
            length = Math.pow(circle.center.x - point.x, 2) + Math.pow(circle.center.y - point.y, 2);
            if (length < min) {
                min = length;
                closestPoint = point;
            }
        }
        return closestPoint;
    }


}
