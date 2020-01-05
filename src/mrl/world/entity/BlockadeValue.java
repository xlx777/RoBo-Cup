package mrl.world.entity;

/**
 * Created By Mahdi Taherian
 * Date: 6/3/12
 * Time: 2:15 PM
 */
public enum BlockadeValue {//路障价值
    //means these blockades blocked road or blocked passable edge.. 这些障碍物阻塞了道路或可通行路障的边缘
    VERY_IMPORTANT,
    //means these blockades with another blockades blocked road  这些路障与另一个路障阻塞了道路
    IMPORTANT_WITH_HIGH_REPAIR_COST,
    IMPORTANT_WITH_LOW_REPAIR_COST,
    //these blockades do not blocked any route but will slow down agents which want to pass road
    //这些路障不会封锁任何路线，但会减慢想要通过道路的人员的速度
    ORNERY,
    //these blockade do not create any problem for passing road 这些路障不会给过路造成任何问题
    WORTHLESS,
}
