package mrl.world.helper.info;

import javolution.util.FastSet;
import mrl.world.MrlWorldHelper;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.worldmodel.EntityID;

import java.util.*;

/**
 * User: mrl
 * Date: Dec 3, 2010
 * Time: 2:04:46 PM
 */
public class CivilianInfo {//市民信息

    protected MrlWorldHelper world;
    private final Set<EntityID> possibleBuildings = new FastSet<EntityID>();//可能存在的建筑物
    private List<Pair> heardPositions = new ArrayList<Pair>();//听到的位置
    private boolean isFound = false;
    int voiceRange;//声音范围

    public CivilianInfo(MrlWorldHelper world) {
        this.world = world;
        voiceRange = world.getVoiceRange();
    }

    public void updatePossibleBuilding() {//沒有使用
        if (isFound) {
            return;
        }

        Set<EntityID> possibleList = new HashSet<EntityID>();
        for (Pair pair : heardPositions) {
            if (possibleBuildings.isEmpty()) {
                possibleBuildings.addAll(getGuessedBuildings(pair));
            } else {
                ArrayList<EntityID> toRemove = new ArrayList<EntityID>();//去除
                possibleList.addAll(getGuessedBuildings(pair));//获取猜测的建筑物
                for (EntityID building : possibleBuildings) {
                    if (!possibleList.contains(building) && world.getVisitedBuildings().contains(building)) {
                        toRemove.add(building);
                    }
                }
                possibleBuildings.removeAll(toRemove);
            }
        }
        heardPositions.clear();
    }

    public void updatePossibleBuilding(ArrayList<EntityID> possibleList) {
        if (isFound) {
            return;
        }
        if (possibleBuildings.isEmpty()) {
            possibleBuildings.addAll(possibleList);
        } else {
            ArrayList<EntityID> toRemove = new ArrayList<EntityID>();
            for (EntityID building : possibleBuildings) {
                if (!possibleList.contains(building) && world.getVisitedBuildings().contains(building)) {//搜寻过的建筑物
                    toRemove.add(building);
                }
            }
            possibleBuildings.removeAll(toRemove);
        }
        heardPositions.clear();
    }

    private ArrayList<EntityID> getGuessedBuildings(Pair pair) {//获取猜测可能有人的建筑物
        ArrayList<EntityID> builds = new ArrayList<EntityID>();
        Collection<StandardEntity> ens = world.getObjectsInRange((Integer) pair.first(), (Integer) pair.second(), (int) (voiceRange * 1.3));
        for (StandardEntity entity : ens) {
            if (entity instanceof Building) {
                builds.add(entity.getID());
            }
        }
        return builds;
    }

    public List<Pair> getHeardPositions() {
        return heardPositions;
    }//得到听到位置

    public Set<EntityID> getPossibleBuildings() {
        return possibleBuildings;
    }


    public void clearPossibleBuildings() {
        isFound = true;
        possibleBuildings.clear();
    }
}