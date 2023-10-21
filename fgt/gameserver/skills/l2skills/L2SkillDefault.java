package fgt.gameserver.skills.l2skills;

import fgt.commons.data.StatSet;

import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.network.serverpackets.ActionFailed;
import fgt.gameserver.skills.L2Skill;

public class L2SkillDefault extends L2Skill
{
	public L2SkillDefault(StatSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(Creature caster, WorldObject[] targets)
	{
		caster.sendPacket(ActionFailed.STATIC_PACKET);
		caster.sendMessage("Skill " + getId() + " [" + getSkillType() + "] isn't implemented.");
	}
}