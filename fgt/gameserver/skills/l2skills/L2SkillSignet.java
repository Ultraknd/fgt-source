package fgt.gameserver.skills.l2skills;

import fgt.commons.data.StatSet;

import fgt.gameserver.data.xml.NpcData;
import fgt.gameserver.enums.skills.SkillTargetType;
import fgt.gameserver.idfactory.IdFactory;
import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.instance.EffectPoint;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.model.location.Location;
import fgt.gameserver.skills.L2Skill;

public final class L2SkillSignet extends L2Skill
{
	public final int effectNpcId;
	public final int effectId;
	
	public L2SkillSignet(StatSet set)
	{
		super(set);
		effectNpcId = set.getInteger("effectNpcId", -1);
		effectId = set.getInteger("effectId", -1);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		if (caster.isAlikeDead())
			return;
		
		final NpcTemplate template = NpcData.getInstance().getTemplate(effectNpcId);
		if (template == null)
			return;
		
		final EffectPoint effectPoint = new EffectPoint(IdFactory.getInstance().getNextId(), template, caster);
		effectPoint.getStatus().setMaxHpMp();
		
		Location worldPosition = null;
		if (caster instanceof Player && getTargetType() == SkillTargetType.GROUND)
			worldPosition = ((Player) caster).getCast().getSignetLocation();
		
		getEffects(caster, effectPoint);
		
		effectPoint.setInvul(true);
		effectPoint.spawnMe((worldPosition != null) ? worldPosition : caster.getPosition());
	}
}