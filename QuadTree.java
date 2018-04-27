import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class QuadTree {
    public static final int MAX_LEVEL = 5;
    public static final int MAX_OBJECTS = 1;

    List<Rect> objects;

    QuadTree[] nodes;

    int level;

    Rect bounds;

    public QuadTree(Rect bounds, int level) {
        this.level = level;
        this.bounds = bounds;
        this.objects = new ArrayList<>();
        this.nodes = new QuadTree[4];
    }

    public QuadTree(Rect bounds) {
        this(bounds, 0);
    }


    public void clear() {
        this.objects.clear();
        if (nodes[0] != null) {
            for (QuadTree node : nodes) {
                node.clear();
            }
        }
    }

    public void split() {
        int level = this.level;
        Rect bounds = this.bounds;
        double x = bounds.x,
                y = bounds.y,
                sWidth = bounds.sWidth,
                sHeight = bounds.sHeight;
        this.nodes[0] = new QuadTree(new Rect(bounds.cX, y, sWidth, sHeight), level + 1);
        this.nodes[1] = new QuadTree(new Rect(x, y, sWidth, sHeight), level + 1);
        this.nodes[2] = new QuadTree(new Rect(x, bounds.cY, sWidth, sHeight), level + 1);
        this.nodes[3] = new QuadTree(new Rect(bounds.cX, bounds.cY, sWidth, sHeight), level + 1);
    }


    public int getIndex(Rect rect, boolean checkIsInner) {
        Rect bounds = this.bounds;
        boolean onTop = rect.y + rect.h <= bounds.cY,
                onBottom = rect.y >= bounds.cY,
                onLeft = rect.x + rect.w <= bounds.cX,
                onRight = rect.x >= bounds.cX;

        // 检测矩形是否溢出象限界限
        if (checkIsInner &&
                (Math.abs(rect.cX - bounds.cX) + rect.sWidth > bounds.sWidth ||
                        Math.abs(rect.cY - bounds.cY) + rect.sHeight > bounds.sHeight)) {

            return -1;
        }

        if (onTop) {
            if (onRight) {
                return 0;
            } else if (onLeft) {
                return 1;
            }
        } else if (onBottom) {
            if (onLeft) {
                return 2;
            } else if (onRight) {
                return 3;
            }
        }

        return -1;
    }

    public int getIndex(Rect rect) {
        return getIndex(rect, false);
    }

    public void remove(Rect rect) {
        if (this.nodes[0] != null) {
            int index = this.getIndex(rect);
            if (index != -1) {
                this.nodes[index].remove(rect);
            } else {
                pRemove(rect);
            }
        } else {
            pRemove(rect);
        }
    }

    private void pRemove(Rect rect) {
        for (int i = 0; i < this.objects.size(); i++) {
            if (this.objects.get(i) == rect) {
                objects.remove(i);
                break;
            }
        }
    }


    public void insert(Rect rect) {
        List<Rect> objs = this.objects;
        int i, index;

        if (this.nodes[0] != null) {
            index = this.getIndex(rect);
            if (index != -1) {
                this.nodes[index].insert(rect);
                return;
            }
        }
        objs.add(rect);

        if (this.nodes[0] == null &&
                this.objects.size() > MAX_OBJECTS &&
                this.level < MAX_LEVEL) {
            this.split();
            for (i = objs.size() - 1; i >= 0; i--) {
                index = this.getIndex(objs.get(i));
                if (index != -1) {
                    this.nodes[index].insert(objs.remove(i));
                }
            }
        }
    }

    public void refresh(QuadTree root) {
        List<Rect> objs = this.objects;
        Rect rect;
        int index, i, len;

        for (i = objs.size() - 1; i >= 0; i--) {
            index = this.getIndex(objs.get(i), true);

            // 如果矩形不属于该象限，则将该矩形重新插入
            if (index == -1) {
                if (this != root) {
                    rect = objs.remove(i);
                    root.insert(rect);
                    // root.insert(objs.splice(i, 1)[0]);

                }

                // 如果矩形属于该象限 且 该象限具有子象限，则
                // 将该矩形安插到子象限中
            } else if (this.nodes[0] != null) {
                rect = objs.remove(i);
                this.nodes[index].insert(rect);
            }
        }

        // 递归刷新子象限
        if (this.nodes[0] != null)
            for (i = 0, len = this.nodes.length; i < len; i++) {
                this.nodes[i].refresh(root);
            }
    }


    public List<Rect> retrieve(Rect rect) {
        List<Rect> result = new ArrayList<>(), arr;
        int i, index;
        result.addAll(this.objects);
        if (this.nodes[0] != null) {
            index = this.getIndex(rect);
            if (index != -1) {
                result.addAll(this.nodes[index].retrieve(rect));
            } else {
                arr = rect.carve(this.bounds.cX, this.bounds.cY);
                for (i = arr.size() - 1; i >= 0; i--) {
                    index = this.getIndex(arr.get(i));
                    result.addAll(this.nodes[index].retrieve(rect));

                }
            }
        }
        return result;
    }

    public Rect getBounds() {
        return bounds;
    }

    public void draw(GraphicsContext gc) {
        gc.strokeRect(bounds.getX(), bounds.getY(), bounds.getW(), bounds.getH());

        if (nodes[0] != null) {
            for (QuadTree node : nodes) {
                node.draw(gc);
            }
        }
    }
}
