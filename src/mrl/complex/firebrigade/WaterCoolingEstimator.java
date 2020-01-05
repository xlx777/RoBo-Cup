package mrl.complex.firebrigade;


import mrl.MRLConstants;
import mrl.world.entity.MrlBuilding;

/**
 * User: roohola
 * Date: 3/30/11
 * Time: 11:53 PM
 */
public class WaterCoolingEstimator {
    public static double WATER_COEFFICIENT = 20f;
    public static int FLOOR_HEIGHT = 7;

    protected static double getBuildingEnergy(int buildingCode, int groundArea, int floors, double temperature) {
        return temperature * getBuildingCapacity(buildingCode, groundArea, floors);//温度 * 热容量 = 该建筑物所具有的能量
    }

    protected static double getBuildingCapacity(int buildingCode, int groundArea, int floors) {//建筑物热容量
        double thermoCapacity;
        switch (buildingCode) {
            case 0:
                //木头
                thermoCapacity = 1.1;//热容量 一定量物质升高一度所需要的热量
                break;
            case 1:
                //钢铁
                thermoCapacity = 1.0;
                break;
            default:
                //混泥土
                thermoCapacity = 1.5;
                break;
        }
        return thermoCapacity * groundArea * floors * FLOOR_HEIGHT;//热熔量 （材质（物质）*（地区区域*楼层*7）（一定量））
    }
/*
将目标建筑物降温至目标温度所需要的水量
@param groudArea,目标建筑物的区域大小
@param floors,目标建筑物的楼层
@param buildingCode,目标建筑物的材质
@param temperature,目标建筑物的初始温度
@param finalTemperature,目标建筑物的目标温度
 */
    public static int getWaterNeeded(int groundArea, int floors, int buildingCode, double temperature, double finalTemperature) {
        int waterNeeded = 0;
        double currentTemperature = temperature;
        int step = 500;//一次灭火500水量
        while (true) {
            currentTemperature = waterCooling(groundArea, floors, buildingCode, currentTemperature, step);
            waterNeeded += step;
            if (currentTemperature <= finalTemperature) {//目标温度达到预定目标
                break;
            }
        }
        if (MRLConstants.DEBUG_WATER_COOLING) {
            System.out.println("water cooling predicts: " + waterNeeded);
        }
        return waterNeeded;
    }


    private static double waterCooling(int groundArea, int floors, int buildingCode, double temperature, int water) {
        if (water > 0) {
            double effect = water * WATER_COEFFICIENT;//目标水量的能量

            //(所具有的能量 - 加目标水量的能量) / 目标的热容量
            return (getBuildingEnergy(buildingCode, groundArea, floors, temperature) - effect) / getBuildingCapacity(buildingCode, groundArea, floors);
//            return (((groundArea*floors*temperature*1.1) - (water*WATER_COEFFICIENT))/(groundArea*floors*1.1));
        } else
            throw new RuntimeException("WTF water=" + water);
    }

    //    public static void main(String[] args) {
//        WaterCoolingEstimator estimator = new WaterCoolingEstimator();
//        for (int i = 10; i < 100; i++) {
//            System.out.println("c=" + (1000 * i) + "  " + estimator.getWaterNeeded(100, 1, 0, 10 * i, 30));
//        }
//    }
    /*
    将建筑物当前温度降温至初始化时温度所需要的水量
    @param building,目标建筑物
     */
    public static int waterNeededToExtinguish(MrlBuilding building) {
        return getWaterNeeded(building.getSelfBuilding().getGroundArea(), building.getSelfBuilding().getFloors(),
                building.getSelfBuilding().getBuildingCode(), building.getEstimatedTemperature(), 20);
    }

}
