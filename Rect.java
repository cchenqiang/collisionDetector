import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class Rect {
    // cX 中心点x cY 中心点y
    double x, y, cX, cY, w, h, sWidth, sHeight;

    Object source;

    public Rect() {
    }


    public void reset(double x, double y, double w, double h) {
        resize(w, h);
        moveTo(x, y);
    }

    public Rect(double x, double y, double w, double h) {
        reset(x, y, w, h);
    }

    public void moveTo(double x, double y) {
        this.x = x;
        this.y = y;
        this.cX = x + this.sWidth;
        this.cY = y + this.sHeight;
    }

    public Object getSource() {
        return source;
    }

    public void resize(double width, double height) {
        this.w = width;
        this.h = height;
        this.sWidth = width / 2;
        this.sHeight = height / 2;
    }

    public void copy(Rect rect) {
        this.resize(rect.w, rect.h);
        this.moveTo(rect.x, rect.y);
    }


    public List<Rect> carve(double cX, double cY) {
        List<Rect> result = new ArrayList<>(),
                temp;
        double dX = cX - this.x,
                dY = cY - this.y;
        boolean carveX = dX > 0 && dX < this.w,
                carveY = dY > 0 && dY < this.h;

        // 切割XY方向
        if (carveX && carveY) {
            temp = this.carve(cX, this.y);
            for (Rect rect : temp) {
                result.addAll(rect.carve(this.x, cY));
            }

            // 只切割X方向
        } else if (carveX) {
            result.add(
                    new Rect(this.x, this.y, dX, this.h));
            result.add(
                    new Rect(cX, this.y, this.w - dX, this.h)
            );

            // 只切割Y方向
        } else if (carveY) {
            result.add(
                    new Rect(this.x, this.y, this.w, dY));
            result.add(
                    new Rect(this.x, cY, this.w, this.h - dY)
            );
        }

        return result;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getW() {
        return w;
    }

    public double getH() {
        return h;
    }

    public void draw(GraphicsContext gc) {
        gc.save();
        gc.beginPath();
        gc.rect(this.getX(), this.getY(), this.getW(), this.getH());
        gc.closePath();
        gc.restore();
        gc.fill();
    }
}
