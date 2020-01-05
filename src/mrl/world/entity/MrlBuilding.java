package mrl.world.entity;

import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import mrl.MRLConstants;
import mrl.util.MrlRay;
import mrl.util.Util;
import mrl.world.MrlWorldHelper;
import mrl.world.helper.RoadHelper;
import rescuecore2.misc.Pair;
import rescuecore2.misc.geometry.Point2D;
import rescuecore2.standard.entities.*;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

/**
 * Created by Mostafa Shabani.
 * Date: 4/27/11
 * Time: 12:28 PM
 */
public class MrlBuilding {
    private org.apache.log4j.Logger Logger = org.apache.log4j.Logger.getLogger(MrlBuilding.class);
    public double BUILDING_VALUE;//建筑价值
    private boolean probablyOnFire = false;//是否着火
    private Building selfBuilding;//本身的建筑
    private List<MrlBuilding> connectedBuilding;//连接的建筑物
    private List<Float> connectedValues;//连接建筑的价值（？）
    private Hashtable connectedBuildingsTable;//连接的建筑物表
    private List<EntityID> neighbourIdBuildings;//相邻建筑物id
    private List<EntityID> neighbourFireBuildings;//相邻着火建筑（？）
    private Collection<Wall> walls;//墙壁
    private double totalWallArea;//总墙面积
    private ArrayList<Wall> allWalls;//所有墙？
    private List<Entrance> entrances;//出入口
    private Integer zoneId;//区域编号
    private double cellCover;//？
    private boolean visited = false;//是否搜寻过
    private boolean shouldCheckInside;//是否应该检查里面
    private Set<Civilian> civilians;//平民
    private boolean isReachable;//是否可以到达
    private boolean visitable;//可以访问？
    private MrlWorldHelper world;//
    private int lastUpdateTime;//最后更新时间
    private Set<EntityID> civilianPossibly;//平民可能？
    private double civilianPossibleValue;//救助平民可能的价值
    private List<Polygon> centerVisitShapes;//访问中心的形状
    private Map<EntityID, List<Polygon>> centerVisitRoadShapes;//中心访问的道路的形状
    private Map<EntityID, List<Point>> centerVisitRoadPoints;//中心访问道路的点
    private Map<Edge, Pair<Point2D, Point2D>> edgeVisibleCenterPoints;//边缘可见中心点
    private boolean sensed;//是否可被感觉到
    private int sensedTime = -1;//感测到的时间
    private Set<EntityID> visibleFrom;//可见于
    private Set<EntityID> observableAreas;//可观察的区域
    private List<MrlRay> lineOfSight;//视线
    private double advantageRatio;//todo @Mostafam: Describe this  优势比率
    private Set<EntityID> extinguishableFromAreas;//可从区域扑灭
    private List<MrlBuilding> buildingsInExtinguishRange;//灭火范围内的建筑物
    private int ignitionTime = -1;//点火时间

    protected int totalHits;//总？？
    protected int totalRays;
    private double hitRate = 0;
//    private int cellX;
//    private int cellY;

    //    protected List<EntityID> buildingNeighbours = new ArrayList<EntityID>();
    private MrlWorldHelper worldHelper;//世界信息读取
    protected WorldInfo worldInfo;
    protected AgentInfo agentInfo;
    protected ScenarioInfo scenarioInfo;
    private RoadHelper roadHelper;

    public MrlBuilding(StandardEntity entity, MrlWorldHelper worldHelper, WorldInfo worldInfo, AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
        this.scenarioInfo = scenarioInfo;
        this.worldHelper = worldHelper;
        this.worldInfo = worldInfo;
        roadHelper = worldHelper.getHelper(RoadHelper.class);
        selfBuilding = (Building) entity;
        connectedBuildingsTable = new Hashtable(30);
        neighbourIdBuildings = new ArrayList<>();
        neighbourFireBuildings = new ArrayList<>();
        connectedBuilding = new ArrayList<>();
        entrances = new ArrayList<>();
        civilians = new HashSet<>();
        this.isReachable = true;
        this.visitable = true;
        this.world = worldHelper;
        lastUpdateTime = 0;
        civilianPossibly = new HashSet<>();
        centerVisitShapes = new ArrayList<>();
        centerVisitRoadShapes = new HashMap<>();
        centerVisitRoadPoints = new HashMap<>();
        edgeVisibleCenterPoints = new HashMap<>();
        setVisibleFrom(new HashSet<>());
        setObservableAreas(new HashSet<>());
        setEdgeVisibleCenterPoints();
        if (worldInfo.getEntity(agentInfo.getID()) instanceof FireBrigade) {
            initWalls(world);
            initSimulatorValues();
        }
    }

    public void addMrlBuildingNeighbour(MrlBuilding mrlBuilding) {
        allWalls.addAll(mrlBuilding.getWalls());
//        connectedBuilding.add(mrlBuilding);
    }

    public void cleanup() {
        allWalls.clear();
    }

    public void initWalls(MrlWorldHelper world) {//初始化墙

        int fx = selfBuilding.getApexList()[0];
        int fy = selfBuilding.getApexList()[1];
        int lx = fx;
        int ly = fy;
        Wall w;
        walls = new ArrayList<Wall>();
        allWalls = new ArrayList<Wall>();

        for (int n = 2; n < selfBuilding.getApexList().length; n++) {
            int tx = selfBuilding.getApexList()[n];
            int ty = selfBuilding.getApexList()[++n];
            w = new Wall(lx, ly, tx, ty, this, world.rayRate);
            if (w.validate()) {
                walls.add(w);
                totalWallArea += FLOOR_HEIGHT * 1000 * w.length;
            }
            lx = tx;
            ly = ty;
        }

        w = new Wall(lx, ly, fx, fy, this, world.rayRate);
        walls.add(w);
        totalWallArea = totalWallArea / 1000000d;

    }

    public void initWallValues(MrlWorldHelper world) {
//        int selfHits=0;
//        int strange=0;

        for (Wall wall : walls) {
            wall.findHits(this);
            totalHits += wall.hits;
//            selfHits+=wall.selfHits;
            totalRays += wall.rays;
//            strange=wall.strange;
        }
//        int c = 0;
        connectedBuilding = new ArrayList<MrlBuilding>();
        connectedValues = new ArrayList<Float>();
        float base = totalRays;

        for (Enumeration e = connectedBuildingsTable.keys(); e.hasMoreElements(); /*c++*/) {
            MrlBuilding b = (MrlBuilding) e.nextElement();
            Integer value = (Integer) connectedBuildingsTable.get(b);
            connectedBuilding.add(b);
            connectedValues.add(value.floatValue() / base);
//            buildingNeighbours.add(b.getSelfBuilding().getID());
        }
        hitRate = totalHits * 1.0 / totalRays;
//        Logger.debug("{"+(((float)totalHits)*100/((float)totalRays))+","+totalRays+","+totalHits+","+selfHits+","+strange+"}");
    }

    public List<Entrance> getEntrances() {
        return entrances;
    }

    public void addEntrance(Entrance entrance) {
        this.entrances.add(entrance);
    }

    public void setConnectedBuilding(List<MrlBuilding> connectedBuilding) {
        this.connectedBuilding = connectedBuilding;
    }

    public List<MrlBuilding> getConnectedBuilding() {
        return connectedBuilding;
    }

    public void setConnectedValues(List<Float> connectedValues) {
        this.connectedValues = connectedValues;
    }

    public List<Float> getConnectedValues() {
        return connectedValues;
    }

    public Collection<Wall> getWalls() {
        return walls;
    }

    public Hashtable getConnectedBuildingsTable() {
        return connectedBuildingsTable;
    }

    public ArrayList<Wall> getAllWalls() {
        return allWalls;
    }

    public double getHitRate() {
        return hitRate;
    }

    public void setHitRate(double hitRate) {
        this.hitRate = hitRate;
    }

    public void setNeighbourIdBuildings(List<EntityID> neighbourIdBuildings) {
        this.neighbourIdBuildings = neighbourIdBuildings;
    }

    public void setNeighbourFireBuildings(List<EntityID> neighbourFireBuildings) {
        this.neighbourFireBuildings = neighbourFireBuildings;
    }

    public List<EntityID> getNeighbourIdBuildings() {
        return neighbourIdBuildings;
    }

    public List<EntityID> getNeighbourFireBuildings() {
        return neighbourFireBuildings;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public double getCellCover() {
        return cellCover;
    }

    public void setCellCover(double cellCover) {
        this.cellCover = cellCover;
    }

    public boolean shouldCheckInside() {
        return shouldCheckInside;
    }

    public void setShouldCheckInside(boolean shouldCheckInside) {
        this.shouldCheckInside = shouldCheckInside;
    }

    public boolean isBurning() {
        return getEstimatedFieryness() > 0 && getEstimatedFieryness() < 4;
    }

    public double getBuildingRadiation() {//获得建筑辐射
        double value = 0;
//        double totalArea = 0;
        MrlBuilding b;

        for (int c = 0; c < connectedValues.size(); c++) {
            b = connectedBuilding.get(c);
            if (!b.isBurning()) {
                value += (connectedValues.get(c));

            }
        }
        return value * getEstimatedTemperature() / 1000;
    }

    public double getNeighbourRadiation() {//获得周围建筑辐射
        double value = 0;
//        double totalArea = 0;
        MrlBuilding b;
        int index;

        for (MrlBuilding building : connectedBuilding) {
            if (building.isBurning()) {
                index = building.getConnectedBuilding().indexOf(this);
                if (index >= 0) {
                    value += (building.getConnectedValues().get(index) * building.getEstimatedTemperature());
                }
            }
        }

        return value / 10000;
    }

    public boolean isOneEntranceOpen(MrlWorldHelper world) {//是否是一个开放的入口
        RoadHelper roadHelper = world.getHelper(RoadHelper.class);
//        Building building = world.getEntity(getID(), Building.class);
//        for (EntityID nID : building.getNeighboursByEdge()) {
//            StandardEntity entity = world.getEntity(nID);
//            if (entity instanceof Road) {
//                if (roadHelper.isOpenOrNotSeen(this.getID(), entity.getID())) {
//                    return true;
//                }
//            } else {
//                return true;
//            }
//        }
        for (Entrance entrance : getEntrances()) {
            Road road = world.getEntity(entrance.getID(), Road.class);
            for (Building building : entrance.getBuildings()) {
                if (road.getNeighbours().contains(building.getID())) {
                    if (roadHelper.isOpenOrNotSeen(building.getID(), entrance.getID())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    ///////////////////////////////////FIRE SIMULATOR PROPERTIES////////////////////////////////////火灾模拟器属性
    static final int FLOOR_HEIGHT = 3;//楼层
    static float RADIATION_COEFFICIENT = 0.011f;//辐射系数
    static final double STEFAN_BOLTZMANN_CONSTANT = 0.000000056704;//斯蒂芬·博茨曼常数

    private int startTime = -1;
    private float fuel;//汽油量
    private float initFuel = -1;//初始燃料
    private float volume;//体积
    private double energy;//能源
    private float prevBurned;//上一个已烧？ 预先着火值
    private float capacity;//容量
    private int waterQuantity;//水量
    private boolean wasEverWatered = false;//是否曾经浇水
    private boolean flammable = true;//易燃的

    public static float woodIgnition = 47;//木材着火点
    public static float steelIgnition = 47;//钢着火点
    public static float concreteIgnition = 47;//混凝土着火点
    public static float woodCapacity = 1.1f;//木材热容量
    public static float steelCapacity = 1.0f;//钢热容量
    public static float concreteCapacity = 1.5f;//混泥土热容量
    public static float woodEnergy = 2400;//木材能源
    public static float steelEnergy = 800;//钢铁能源
    public static float concreteEnergy = 350;//混泥土能源

    public void initSimulatorValues() {//初始化模拟器值
        volume = selfBuilding.getGroundArea() * selfBuilding.getFloors() * FLOOR_HEIGHT;//体积
        fuel = getInitialFuel();
        capacity = (volume * getThermoCapacity());
        energy = 0;
        initFuel = -1;
        prevBurned = 0;
    }

    public float getInitialFuel() {
        if (initFuel < 0) {
            initFuel = (getFuelDensity() * volume);
        }
        return initFuel;
    }

    private float getThermoCapacity() {//获得热容量
        switch (selfBuilding.getBuildingCode()) {
            case 0:
                return woodCapacity;
            case 1:
                return steelCapacity;
            default:
                return concreteCapacity;
        }
    }

    private float getFuelDensity() {//获得燃料能量密度
        switch (selfBuilding.getBuildingCode()) {
            case 0:
                return woodEnergy;
            case 1:
                return steelEnergy;
            default:
                return concreteEnergy;
        }
    }

    public float getIgnitionPoint() {//得到点火点
        switch (selfBuilding.getBuildingCode()) {
            case 0:
                return woodIgnition;
            case 1:
                return steelIgnition;
            default:
                return concreteIgnition;
        }
    }

    public float getConsume(double bRate) {//得到消费
        if (fuel == 0) {
            return 0;
        }
        float tf = (float) (getEstimatedTemperature() / 1000f);
        float lf = fuel / getInitialFuel();
        float f = (float) (tf * lf * bRate);
        if (f < 0.005f)
            f = 0.005f;
        return getInitialFuel() * f;
    }

    public double getEstimatedTemperature() {//得到估计温度
        double rv = energy / capacity;
        if (Double.isNaN(rv)) {
//            new RuntimeException().printStackTrace();
            return selfBuilding.isTemperatureDefined() ? selfBuilding.getTemperature() : 0;
        }
        if (rv == Double.NaN || rv == Double.POSITIVE_INFINITY || rv == Double.NEGATIVE_INFINITY)
            rv = Double.MAX_VALUE * 0.75;
        return rv;
    }

    public int getEstimatedFieryness() {//获得估计的炽热？ 火焰等级
        if (!isFlammable())
            return 0;
        if (getEstimatedTemperature() >= getIgnitionPoint()) {//得到估计温度大于着火点
            if (fuel >= getInitialFuel() * 0.66)
                return 1;   // burning, slightly damaged  燃烧，轻微损坏
            if (fuel >= getInitialFuel() * 0.33)
                return 2;   // burning, more damaged   燃烧，更损坏
            if (fuel > 0)
                return 3;    // burning, severly damaged   燃烧，严重损坏
        }
        if (fuel == getInitialFuel())
            if (wasEverWatered)
                return 4;   // not burnt, but watered-damaged  不燃烧，但浇水损坏
            else
                return 0;   // not burnt, no water damage
        if (fuel >= getInitialFuel() * 0.66)
            return 5;        // extinguished, slightly damaged  熄灭，轻微损坏
        if (fuel >= getInitialFuel() * 0.33)
            return 6;        // extinguished, more damaged  熄灭，更损坏
        if (fuel > 0)
            return 7;        // extinguished, severely damaged   熄灭，严重损坏
        return 8;           // completely burnt down   完全烧毁
    }

    public double getRadiationEnergy() {//获得辐射能
        double t = getEstimatedTemperature() + 293; // Assume ambient temperature is 293 Kelvin.
        double radEn = (t * t * t * t) * totalWallArea * RADIATION_COEFFICIENT * STEFAN_BOLTZMANN_CONSTANT;
        if (radEn == Double.NaN || radEn == Double.POSITIVE_INFINITY || radEn == Double.NEGATIVE_INFINITY)
            radEn = Double.MAX_VALUE * 0.75;
        if (radEn > getEnergy()) {
            radEn = getEnergy();
        }
        return radEn;
    }

    public void resetOldReachable(int resetTime) {//重置旧的可到达
        if (agentInfo.getTime() - lastUpdateTime > resetTime) {
            setReachable(true);
            setVisitable(true);
        }
    }

    public int getRealFieryness() {
        return selfBuilding.getFieryness();
    }

    public int getRealTemperature() {
        return selfBuilding.getTemperature();
    }

    public Building getSelfBuilding() {
        return selfBuilding;
    }

    public float getVolume() {
        return volume;
    }

    public float getCapacity() {
        return capacity;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double v) {
        energy = v;
    }

    public float getPrevBurned() {
        return prevBurned;
    }

    public void setPrevBurned(float consumed) {
        prevBurned = consumed;
    }

    public boolean isFlammable() {
        return flammable;
    }

    public void setFlammable(boolean flammable) {
        this.flammable = flammable;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public int getWaterQuantity() {
        return waterQuantity;
    }

    public void setWaterQuantity(int i) {
        if (i > waterQuantity) {
            wasEverWatered = true;
        }
        waterQuantity = i;
    }

    public void increaseWaterQuantity(int i) {
        waterQuantity += i;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setWasEverWatered(boolean wasEverWatered) {
        this.wasEverWatered = wasEverWatered;
    }

    public Set<EntityID> getCivilianPossibly() {
        return civilianPossibly;
    }

    public void addCivilianPossibly(EntityID civID) {
        civilianPossibly.add(civID);
    }

    public double getCivilianPossibleValue() {
        return civilianPossibleValue;
    }

    public Map<Edge, Pair<Point2D, Point2D>> getEdgeVisibleCenterPoints() {
        return edgeVisibleCenterPoints;
    }

    /**
     * find two point around center that is parallel with passable edges of this building with AGENT_SIZE range.
     */
    //在中心周围找到两个点，该点与该建筑物的可通过边缘相平行（AGENT_SIZE范围）。
    private void setEdgeVisibleCenterPoints() {
        Pair<Integer, Integer> location = worldInfo.getLocation(selfBuilding.getID());
        Point2D center = new Point2D(location.first(), location.second());
        for (Edge edge : selfBuilding.getEdges()) {
            if (edge.isPassable()) {
                Pair<Point2D, Point2D> twoPoints = Util.get2PointsAroundCenter(edge, center, MRLConstants.AGENT_SIZE);//Civilian Size
                edgeVisibleCenterPoints.put(edge, twoPoints);
            }
        }
    }

    public void addCenterVisitShapes(Polygon shape) {
        centerVisitShapes.add(shape);
    }

    public void addCenterVisitRoadShapes(MrlRoad mrlRoad, Polygon shape) {
        if (!centerVisitRoadShapes.containsKey(mrlRoad.getID())) {
            centerVisitRoadShapes.put(mrlRoad.getID(), new ArrayList<Polygon>());
        }
        centerVisitRoadShapes.get(mrlRoad.getID()).add(shape);
        mrlRoad.addBuildingVisitableParts(getID(), shape);
    }

    public List<Polygon> getCenterVisitShapes() {
        return centerVisitShapes;
    }

    public Map<EntityID, List<Point>> getCenterVisitRoadPoints() {
        return centerVisitRoadPoints;
    }

    public void addCenterVisitRoadPoints(MrlRoad mrlRoad, Point point) {
        if (!centerVisitRoadPoints.containsKey(mrlRoad.getID())) {
            centerVisitRoadPoints.put(mrlRoad.getID(), new ArrayList<Point>());
        }
        centerVisitRoadPoints.get(mrlRoad.getID()).add(point);
    }

    public Map<EntityID, List<Polygon>> getCenterVisitRoadShapes() {
        return centerVisitRoadShapes;
    }

    public Pair<Point2D, Point2D> getCenterPointsFrom(Edge edge) {
        return edgeVisibleCenterPoints.get(edge);
    }

    public boolean isVisitable() {
        return visitable;
    }

    public void setVisitable(boolean visitable) {
        this.visitable = visitable;
    }

    public boolean isPutOff() {
        return getEstimatedFieryness() > 4 && getEstimatedFieryness() < 8;
    }

    public boolean isBurned() {
        return getEstimatedFieryness() == 8;
    }

    public void setProbablyOnFire(boolean probablyOnFire) {
        this.probablyOnFire = probablyOnFire;
    }

    public boolean isProbablyOnFire() {
        return probablyOnFire;
    }

    @Override
    public String toString() {
        return "MrlBuilding[" + selfBuilding.getID().getValue() + "]";
    }

    public EntityID getID() {
        return selfBuilding.getID();
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited() {
        this.visited = true;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean reachable) {//设置可达
        lastUpdateTime = agentInfo.getTime();
        isReachable = reachable;
//        MrlPersonalData.VIEWER_DATA.setBlockedBuildings(world.getPlatoonAgent(), getID(), reachable);
    }

    public boolean intersectToLine2D(Line2D line) {
        for (Wall wall : getWalls()) {
            if (wall.getLine().intersectsLine(line))
                return true;
        }
        return false;
    }

//    public MrlBuilding buildingToMrlBuilding( Building building){
//
//        for (Property property:building.getProperties()){
//            this.
//        }
//    }


    /**
     * A set of containing civilians
     *一组平民
     * @return set of civilians
     */
    public Set<Civilian> getCivilians() {
        return civilians;
    }

    public void setSensed(int time) {
        sensed = true;
        sensedTime = time;
    }

    public boolean isSensed() {
        return sensed;
    }

    public int getSensedTime() {
        return sensedTime;
    }

    public void setCivilianPossibleValue(double civilianPossibleValue) {
        this.civilianPossibleValue = civilianPossibleValue;
    }

    public Set<EntityID> getVisibleFrom() {
        return visibleFrom;
    }

    public void setVisibleFrom(Set<EntityID> visibleFrom) {
        this.visibleFrom = visibleFrom;
        this.visibleFrom.add(getID());
    }

    public Set<EntityID> getObservableAreas() {
        return observableAreas;
    }

    public void setObservableAreas(Set<EntityID> observableAreas) {
        this.observableAreas = observableAreas;
    }

    public List<MrlRay> getLineOfSight() {
        return lineOfSight;
    }

    public void setLineOfSight(List<MrlRay> lineOfSight) {
        this.lineOfSight = lineOfSight;
    }

    public double getAdvantageRatio() {
        return advantageRatio;
    }

    public void setAdvantageRatio(double advantageRatio) {
        this.advantageRatio = advantageRatio;
    }

    public Set<EntityID> getExtinguishableFromAreas() {
        return extinguishableFromAreas;
    }

    public void setExtinguishableFromAreas(Set<EntityID> extinguishableFromAreas) {
        this.extinguishableFromAreas = extinguishableFromAreas;
    }

    public List<MrlBuilding> getBuildingsInExtinguishRange() {
        return buildingsInExtinguishRange;
    }

    public void setBuildingsInExtinguishRange(List<MrlBuilding> buildingsInExtinguishRange) {
        this.buildingsInExtinguishRange = buildingsInExtinguishRange;
    }

    public int getIgnitionTime() {
        return ignitionTime;
    }

    public void setIgnitionTime(int ignitionTime) {
        if (this.ignitionTime == -1) {
            this.ignitionTime = ignitionTime;
        }
    }

    public void updateValues(Building building) {//更新值
        switch (building.getFieryness()) {
            case 0:
                this.setFuel(this.getInitialFuel());
                if (getEstimatedTemperature() >= getIgnitionPoint()) {
                    setEnergy(getIgnitionPoint() / 2);
                }
                break;
            case 1:
                if (getFuel() < getInitialFuel() * 0.66) {
                    setFuel((float) (getInitialFuel() * 0.75));
                } else if (getFuel() == getInitialFuel()) {
                    setFuel((float) (getInitialFuel() * 0.90));
                }
                break;

            case 2:
                if (getFuel() < getInitialFuel() * 0.33
                        || getFuel() > getInitialFuel() * 0.66) {
                    setFuel((float) (getInitialFuel() * 0.50));
                }
                break;

            case 3:
                if (getFuel() < getInitialFuel() * 0.01
                        || getFuel() > getInitialFuel() * 0.33) {
                    setFuel((float) (getInitialFuel() * 0.15));
                }
                break;

            case 8:
                setFuel(0);
                break;
        }
    }
}
