package fgt.gameserver.skills.funcs;

import fgt.gameserver.enums.skills.Stats;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.Formulas;
import fgt.gameserver.skills.L2Skill;
import fgt.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncMaxCpMul extends Func
{
	private static final FuncMaxCpMul INSTANCE = new FuncMaxCpMul();
	
	private FuncMaxCpMul()
	{
		super(null, Stats.MAX_CP, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		return value * Formulas.CON_BONUS[effector.getStatus().getCON()];
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}