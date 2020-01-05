package mrl.algorithm.clustering;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Set;

/**
 * @author Pooya Deldar
 *         Date: 3/11/12
 *         Time: 8:38 PM
 */
public class ConvexObject {

    //Polygon 多边形类
    private Polygon convexPolygon;//凸多边形
    private Polygon triangle;//三角形

    public ConvexObject(Polygon convexPolygon) {
        this.convexPolygon = convexPolygon;
    }

    public ConvexObject() {
    }

    public Point CENTER_POINT;//中心点
    public Point FIRST_POINT;//第一个点
    public Point SECOND_POINT;//第二个点
    public Point CONVEX_POINT;//凸起点
    //-------------
    public Point OTHER_POINT1;//其他点1
    public Point OTHER_POINT2;//其他点2
    public Set<Point2D> CONVEX_INTERSECT_POINTS;
    public Set<Line2D> CONVEX_INTERSECT_LINES;
    public Polygon DIRECTION_POLYGON;


    public Polygon getConvexPolygon() {
        return convexPolygon;
    }

    public void setConvexPolygon(Polygon convexPolygon) {
        this.convexPolygon = convexPolygon;
    }

    public void setTrianglePolygon(Polygon shape) {
        int xs[] = new int[shape.npoints];
        int ys[] = new int[shape.npoints];
        for (int i = 0; i < shape.npoints; i++) {
            xs[i] = shape.xpoints[i];
            ys[i] = shape.ypoints[i];
        }
        triangle = new Polygon(xs, ys, shape.npoints);
    }

    public Polygon getTriangle() {
        return triangle;
    }

}
