package fgt.gameserver.skills.effects;

import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.ChanceCondition;
import fgt.gameserver.skills.IChanceSkillTrigger;
import fgt.gameserver.skills.L2Skill;

public class EffectChanceSkillTrigger extends AbstractEffect implements IChanceSkillTrigger
{
	private final int _triggeredId;
	private final int _triggeredLevel;
	private final ChanceCondition _chanceCondition;
	
	public EffectChanceSkillTrigger(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
		
		_triggeredId = template.getTriggeredId();
		_triggeredLevel = template.getTriggeredLevel();
		_chanceCondition = template.getChanceCondition();
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CHANCE_SKILL_TRIGGER;
	}
	
	@Override
	public boolean onStart()
	{
		getEffected().addChanceTrigger(this);
		getEffected().onStartChanceEffect();
		return super.onStart();
	}
	
	@Override
	public boolean onActionTime()
	{
		getEffected().onActionTimeChanceEffect();
		return false;
	}
	
	@Override
	public void onExit()
	{
		// trigger only if effect in use and successfully ticked to the end
		if (getInUse() && getCount() == 0)
			getEffected().onExitChanceEffect();
		getEffected().removeChanceEffect(this);
		super.onExit();
	}
	
	@Override
	public int getTriggeredChanceId()
	{
		return _triggeredId;
	}
	
	@Override
	public int getTriggeredChanceLevel()
	{
		return _triggeredLevel;
	}
	
	@Override
	public boolean triggersChanceSkill()
	{
		return _triggeredId > 1;
	}
	
	@Override
	public ChanceCondition getTriggeredChanceCondition()
	{
		return _chanceCondition;
	}
}