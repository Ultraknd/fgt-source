package fgt.gameserver.handler.skillhandlers;

import fgt.gameserver.enums.items.ShotType;
import fgt.gameserver.enums.skills.SkillType;
import fgt.gameserver.enums.skills.Stats;
import fgt.gameserver.handler.ISkillHandler;
import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class ManaHeal implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.MANAHEAL,
		SkillType.MANARECHARGE
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature))
				continue;
			
			final Creature target = ((Creature) obj);
			if (!target.canBeHealed())
				continue;
			
			double mp = skill.getPower();
			
			if (skill.getSkillType() == SkillType.MANAHEAL_PERCENT)
				mp = target.getStatus().getMaxMp() * mp / 100.0;
			else
				mp = (skill.getSkillType() == SkillType.MANARECHARGE) ? target.getStatus().calcStat(Stats.RECHARGE_MP_RATE, mp, null, null) : mp;
			
			mp = target.getStatus().addMp(mp);
			
			if (activeChar instanceof Player && activeChar != target)
				target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_MP_RESTORED_BY_S1).addCharName(activeChar).addNumber((int) mp));
			else
				target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED).addNumber((int) mp));
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = activeChar.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(activeChar);
		}
		
		if (!skill.isPotion())
			activeChar.setChargedShot(activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOT) ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}