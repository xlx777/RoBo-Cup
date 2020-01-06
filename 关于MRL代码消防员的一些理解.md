# 消防员的理解部分

关于MRl代码中目录  mrl.complex.firebrigade 下的理解部分：

该目录下包含了三种关于消防员对target的选择方法：

### 1.默认的基于方向的目标选择方法：

在目录 mrl.complex.firebrigade.directionbased  下的四个类是实现基于方向的目标选择的实现类

MrlFireBrigadeDirectionManager  类中实现了一些对于方向目标选择的实现接口 ，里面有一些对于聚类中的计算还没有完全理解。（具体按什么值排序还没了解后面再更新）

其中  DefaultFireBrigadeTargetSelector  是  DirectionBasedTargetSelector14  的抽象父类。

其中DirectionBasedTargetSelector14类是实现基于方向的目标选择的类，里面提供了一个目标选择的接口，selectTarget(Cluster targetCluster)    我们可以通过调用这个方法实现基于方向的目标选择。

### 2.基于距离的目标选择方法

实现类 mrl.complex.firebrigade.DistanceBasedTargetSelector

这个探测是用来实现对受伤的人的选择的。这个类提供了的一个寻找救护队目标的接口  findBestVictim（Map<EntityID, AmbulanceTarget> targetsMap, Collection<StandardEntity> elements）我们可以通过调用这个方法实现救护队目标的添加。

基于距离的目标选择主要是用来计算在聚类中寻找最适合的最应该去救的人。（判断条件基于距离和道路是否通过，是否值得救护队去救）。

其中 mrl.complex.firebrigade.ZJUBaseBuildingCostComputer这个类提供了一些关于很多权值的计算接口，可以去了解一波。

### 3.基于探索的目标选择方法

实现类 mrl.complex.firebrigade.TargetBaseExploreManager

这个选择方法是用来判断当一个消防员不能扑灭火区时，可以通过这个类中提供的isTimeToExplore方法来判断是否呼叫其他消防员前来。



FireBrigadeUtilities类提供方法

该类中提供了一些关于火势预测的判断，里面提供了大量的关于消防队计算的接口但是大多数都没有被调用，我们可以尝试去调用。