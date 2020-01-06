package mrl.world.geometry;

import java.awt.*;
import java.util.Collection;

/**
 * Interface for any Convex Hull algorithm
 *任何凸包算法的接口
 * @author BrainX
 * @version 1.0
 */
public interface IConvexHull {//凸包接口

    /**
     * Getter for the globally unique id of this convex hull. This ID must be calculated similarly among different agents (threads or VM's)
     *此凸包的全局唯一ID的Getter。必须在不同的代理（线程或VM）之间类似地计算此ID。
     * @return
     */
    public Long getGuid();

    /**
     * Gets or calculates the output Convex hull based on internal point data
     *根据内部点数据获取或计算输出凸包
     * @return A Polygon object representing the convex hull
     */
    public Polygon getConvexPolygon();

    /**
     * Adds a point to the point-list. Early-calculation implementations should recalculate Convex Hull polygon after addition.
     *将点添加到点列表。早期计算实现应在添加后重新计算凸包体多边形
     * @param x X Coordinate of the added point
     * @param y Y Coordinate of the added point
     * @see #addPoint(Point)
     */
    public void addPoint(int x, int y);

    /**
     * Adds a point to the point-list. Early-calculation implementations should recalculate Convex Hull polygon after addition.
     *
     * @param point Coordinates of the added point
     * @see #addPoint(int, int)
     */
    public void addPoint(Point point);

    /**
     * Removes a point from the point-list. Early-calculation implementations should recalculate Convex Hull polygon after removal.
     *
     * @param x X Coordinate of the removed point
     * @param y Y Coordinate of the removed point
     * @see #removePoint(Point)
     */
    //移除
    public void removePoint(int x, int y);

    /**
     * Removes a point from the point-list. Early-calculation implementations should recalculate Convex Hull polygon after removal.
     *
     * @param point Coordinates of the removed point
     * @see #removePoint(int, int)
     */
    public void removePoint(Point point);

    /**
     * Updates the point-list based on the presented added and removed points. Early-calculation implementations should recalculate Convex Hull polygon after update.<br/>
     * Basic pre-optimization implementation can delegate to {@link #removePoint(Point)} and {@link #addPoint(Point)}.
     *根据显示的添加和删除的点更新点列表。早期计算实现应在更新后重新计算“凸包”多边形
     * @param addedPoints   Coordinates of the added points
     * @param removedPoints Coordinates of the removed points
     */
    public void updatePoints(Collection<Point> addedPoints, Collection<Point> removedPoints);
}
