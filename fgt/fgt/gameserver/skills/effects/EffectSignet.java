package fgt.gameserver.skills.effects;

import java.util.List;

import fgt.gameserver.data.SkillTable;
import fgt.gameserver.enums.ZoneId;
import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.instance.Door;
import fgt.gameserver.model.actor.instance.EffectPoint;
import fgt.gameserver.network.serverpackets.MagicSkillLaunched;
import fgt.gameserver.network.serverpackets.MagicSkillUse;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;
import fgt.gameserver.skills.l2skills.L2SkillSignet;

public class EffectSignet extends AbstractEffect
{
	private EffectPoint _actor;
	
	public EffectSignet(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SIGNET_EFFECT;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(_skill instanceof L2SkillSignet))
			return false;
		
		_actor = (EffectPoint) getEffected();
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		final List<Creature> list = _actor.getKnownTypeInRadius(Creature.class, _skill.getSkillRadius(), creature -> !creature.isDead() && !(creature instanceof Door) && !creature.isInsideZone(ZoneId.PEACE));
		if (list.isEmpty())
			return true;
		
		final L2Skill signetSkill = SkillTable.getInstance().getInfo(((L2SkillSignet) _skill).effectId, _skill.getLevel());
		final Creature[] targets = list.toArray(new Creature[list.size()]);
		for (Creature creature : targets)
		{
			signetSkill.getEffects(_actor, creature);
			_actor.broadcastPacket(new MagicSkillUse(_actor, creature, signetSkill.getId(), signetSkill.getLevel(), 0, 0));
		}
		_actor.broadcastPacket(new MagicSkillLaunched(_actor, signetSkill, targets));
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (_actor != null)
			_actor.deleteMe();
	}
}