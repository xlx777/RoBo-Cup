package mrl.centralized;

import adf.agent.communication.MessageManager;
import adf.agent.communication.standard.bundle.centralized.CommandFire;
import adf.agent.develop.DevelopData;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.agent.module.ModuleManager;
import adf.agent.precompute.PrecomputeData;
import adf.component.centralized.CommandExecutor;
import adf.component.extaction.ExtAction;
import adf.component.module.algorithm.PathPlanning;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.Road;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.worldmodel.EntityID;

import static rescuecore2.standard.entities.StandardEntityURN.REFUGE;

public class MrlCommandExecutorFire extends CommandExecutor<CommandFire> {
    private static final int ACTION_UNKNOWN = -1;
    private static final int ACTION_REST = CommandFire.ACTION_REST;
    private static final int ACTION_MOVE = CommandFire.ACTION_MOVE;
    private static final int ACTION_EXTINGUISH = CommandFire.ACTION_EXTINGUISH;
    private static final int ACTION_REFILL = CommandFire.ACTION_REFILL;
    private static final int ACTION_AUTONOMY = CommandFire.ACTION_AUTONOMY;

    private PathPlanning pathPlanning;

    private ExtAction actionFireFighting;
    private ExtAction actionExtMove;

    private int maxWater;

    private int commandType;
    private EntityID target;
    private EntityID commanderID;

    public MrlCommandExecutorFire(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData) {
        super(ai, wi, si, moduleManager, developData);
    }

    @Override
    public CommandExecutor setCommand(CommandFire command) {
        return this;
    }

    public CommandExecutor precompute(PrecomputeData precomputeData) {
        super.precompute(precomputeData);
        return this;
    }

    public CommandExecutor resume(PrecomputeData precomputeData) {
        super.resume(precomputeData);
        return this;
    }

    public CommandExecutor preparate() {
        super.preparate();
        return this;
    }

    public CommandExecutor updateInfo(MessageManager messageManager){
        super.updateInfo(messageManager);
        return this;
    }

    @Override
    public CommandExecutor calc() {
        this.result = null;
        return this;
    }
}
