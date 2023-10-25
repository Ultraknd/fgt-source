package fgt.gameserver.skills.funcs;

import fgt.gameserver.enums.skills.Stats;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Summon;
import fgt.gameserver.skills.Formulas;
import fgt.gameserver.skills.L2Skill;
import fgt.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncAtkCritical extends Func
{
	private static final FuncAtkCritical INSTANCE = new FuncAtkCritical();
	
	private FuncAtkCritical()
	{
		super(null, Stats.CRITICAL_RATE, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		if (!(effector instanceof Summon))
			value *= Formulas.DEX_BONUS[effector.getStatus().getDEX()];
		
		return value * 10;
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}