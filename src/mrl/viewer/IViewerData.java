package mrl.viewer;

import adf.agent.info.WorldInfo;
import mrl.extaction.clear.GuideLine;
import mrl.world.routing.graph.GraphModule;
import rescuecore2.misc.geometry.Point2D;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface IViewerData {//

    void print(String s);

    void setUnreachableBuildings(EntityID id, HashSet<EntityID> entityIDs);//设置无法到达的建筑物

    void setPFGuideline(EntityID id, GuideLine guideLine);//设置警察准则

    void setPFClearAreaLines(EntityID id, rescuecore2.misc.geometry.Line2D targetLine, rescuecore2.misc.geometry.Line2D first, rescuecore2.misc.geometry.Line2D second);
    //设置警察清理区域线
    void setScaledBlockadeData(EntityID id, List<StandardEntity> obstacles, Polygon scaledBlockades, Map<rescuecore2.misc.geometry.Line2D, List<Point2D>> freePoints, List<Point2D> targetPoint);
    //设置缩放障碍物数据
    void setObstacleBounds(EntityID id, List<rescuecore2.misc.geometry.Line2D> boundLines);
    //设置障碍边界
    void setGraphEdges(EntityID id, GraphModule graph);
    //设置图的边
    void drawBuildingDetectorTarget(WorldInfo worldInfo, EntityID agentID, EntityID target);
    //设置图边
    void drawSearchTarget(WorldInfo worldInfo, EntityID agentID, EntityID target);
    //绘制搜索目标
}
