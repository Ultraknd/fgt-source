package fgt.gameserver.skills.funcs;

import fgt.gameserver.enums.skills.Stats;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.Formulas;
import fgt.gameserver.skills.L2Skill;
import fgt.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMoveSpeed extends Func
{
	private static final FuncMoveSpeed INSTANCE = new FuncMoveSpeed();
	
	private FuncMoveSpeed()
	{
		super(null, Stats.RUN_SPEED, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.DEX_BONUS[effector.getStatus().getDEX()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}