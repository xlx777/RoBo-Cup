package mrl.world.entity;

import adf.agent.info.WorldInfo;
import mrl.MRLConstants;
import mrl.util.Util;
import mrl.world.MrlWorldHelper;
import mrl.world.helper.RoadHelper;
import rescuecore2.misc.Pair;
import rescuecore2.misc.geometry.Line2D;
import rescuecore2.misc.geometry.Point2D;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.Road;
import rescuecore2.worldmodel.EntityID;

/**
 * created by: Mahdi Taherian
 * User: mrl
 * Date: 5/17/12
 * Time: 8:27 PM
 */
public class MrlEdge {//边缘

    private boolean isPassable;//是否可通过
    private Line2D line;//线
    private Point2D start;//开始点
    private Point2D end;//结束点
    private Point2D middle;//中间点
    private boolean isBlocked;//是否被封锁
    private boolean isAbsolutelyBlocked;//是否被绝对封锁
    private Integer blockedSize;//障碍物的大小
    private Edge parent;//底层的edge边缘
    private Line2D openPart;//打开部分
    private Pair<EntityID, EntityID> neighbours;//周围的
    private double length;//长度
    private boolean tooSmall;//是否太小了
    MrlWorldHelper worldHelper;//

    public MrlEdge(MrlWorldHelper worldHelper, Edge edge, EntityID parentID) {
        parent = edge;
        this.worldHelper = worldHelper;
        neighbours = new Pair<>(parentID, edge.getNeighbour());
        initialize(edge.isPassable(), edge.getStart(), edge.getEnd());
    }

    private void initialize(boolean passable, Point2D start, Point2D end) {
        this.isAbsolutelyBlocked = false;
        this.isBlocked = false;
        this.blockedSize = null;
        this.start = start;
        this.end = end;
        this.middle = Util.getMiddle(start, end);
        this.line = new Line2D(start, end);
        this.isPassable = passable;
        this.openPart = new Line2D(start, end);
        length = Util.distance(start, end);
        tooSmall = length < MRLConstants.AGENT_PASSING_THRESHOLD;
    }

    public boolean isPassable() {
        return isPassable;
    }

    public boolean isOtherSideBlocked(WorldInfo world) {//被另一面阻挡
        if (isPassable()) {
            MrlEdge mrlEdge = getOtherSideEdge(world);
            if (mrlEdge != null && mrlEdge.isBlocked()) {
                return true;
            }
        }
        return false;
    }

    public MrlEdge getOtherSideEdge(WorldInfo world) {//获取其他侧边
        Area neighbour = (Area) world.getEntity(getNeighbours().second());
        if (neighbour instanceof Road) {
            MrlRoad mrlRoadNeighbour = getMrlRoad(neighbour.getID());
            return mrlRoadNeighbour.getEdgeInPoint(getMiddle());
        }
        return null;
    }

    private MrlRoad getMrlRoad(EntityID id) {
        return worldHelper.getMrlRoad(id);
    }

    public Line2D getLine() {
        return line;
    }

    public Point2D getStart() {
        return start;
    }

    public Point2D getEnd() {
        return end;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public boolean isAbsolutelyBlocked() {
        return isAbsolutelyBlocked;
    }

    public Integer getBlockedSize() {
        return blockedSize;
    }

    public void setBlockedSize(Integer blockedSize) {
        if (blockedSize == null || blockedSize < this.blockedSize) {
            this.blockedSize = blockedSize;
        }
    }

    public void setBlocked(boolean blocked) {
        if (!blocked) {//flase时执行
            setAbsolutelyBlocked(false);
        }
        isBlocked = blocked;
    }

    public void setAbsolutelyBlocked(boolean absolutelyBlocked) {
        if (absolutelyBlocked) {
            setBlocked(true);
        }
        isAbsolutelyBlocked = absolutelyBlocked;
    }

    public Edge getParent() {
        return parent;
    }

    public Point2D getMiddle() {
        return middle;
    }

    public Pair<EntityID, EntityID> getNeighbours() {
        return neighbours;
    }

    public boolean equals(MrlEdge other) {
        return (other.getLine().equals(getLine()));
    }

    public Line2D getOpenPart() {
        return openPart;
    }

    public void setOpenPart(Line2D openPart) {
        this.openPart = openPart;
    }

    public double getLength() {
        return length;
    }

    public boolean isTooSmall() {
        return tooSmall;
    }
}
