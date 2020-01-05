package mrl.util;

import rescuecore2.misc.geometry.Line2D;
import rescuecore2.standard.entities.StandardEntity;

/**
 * Created with IntelliJ IDEA.
 * User: MRL
 * Date: 2/13/13
 * Time: 6:19 PM
 */
public class LineInfo {//道路信息
    private Line2D line;//底层中的2D的线
    private StandardEntity entity;//实体
    private boolean blocking;//是否被阻塞

    public LineInfo(Line2D line, StandardEntity entity, boolean blocking) {
        this.line = line;
        this.entity = entity;
        this.blocking = blocking;
    }

    public Line2D getLine() {
        return line;
    }

    public StandardEntity getEntity() {
        return entity;
    }

    public boolean isBlocking() {
        return blocking;
    }
}
