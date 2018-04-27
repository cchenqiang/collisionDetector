import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class TestMain extends Application {

    public static void main(String[] args) {
        launch(args);

    }

    static int angle = 360;

    static int getAngle() {
        int angle = TestMain.angle;
        if (angle >= 0) {
            TestMain.angle--;
            return angle;
        }
        TestMain.angle = 360;
        return 360;
    }

    public static CrashBox getTarget() {
//        OBB circle = new OBB();
//        circle.init(200, 200, 100, 100, Math.toRadians(60));
        Circle circle = new Circle(200, 200, 100, Math.toRadians(60));
        return circle;
    }

    public static CrashBox getZiDan() {
        final double rotation = Math.toRadians(216);
        OBB ziDan = new OBB();
        ziDan.init(500, 500, 20, 20, rotation);
//        Circle ziDan = new Circle(500, 500, 20, rotation);
        return ziDan;
    }

    static int speed = 2;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Canvas canvas = new Canvas(1280, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //将目标放入四叉树中
        QuadTree quadTree = new QuadTree(new Rect(0, 0, 1280, 800));


        CrashBox target = getTarget();
        quadTree.insert(target.rect());
        CrashBox ziDan = getZiDan();

        Rect rect = ziDan.rect();
        quadTree.insert(rect);


        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Runnable runnable = new Runnable() {
            public void run() {

                gc.clearRect(0, 0, 1280, 800);
                target.draw(gc);
                //移动子弹
                ziDan.move(speed * Math.cos(ziDan.rotation()), speed * Math.sin(ziDan.rotation()));
                ziDan.draw(gc);
                List<Rect> retrieve = quadTree.retrieve(ziDan.rect());
                if (!retrieve.isEmpty()) {
//                    System.out.println("不为空");
                    for (Rect rect1 : retrieve) {
                        if (rect1 == ziDan.rect()) {
                            continue;
                        }
                        CrashBox.CollisionDetectResult collisionDetectResult = ziDan.collidesWith(target);
                        if (collisionDetectResult.overlap > 0) {

                            Vector axis = collisionDetectResult.axis;
                            int a = 200;
                            gc.strokeLine(a + 0, a + 0, a + axis.getX() * 100, a + axis.getY() * 100);
                            speed = 0;
                        }
                    }
                }
            }
        };
        TimerManager.instance().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Platform.runLater(runnable);
                } finally {
                    TimerManager.instance().schedule(this, 16);
                }
            }
        }, 16);

    }
}
