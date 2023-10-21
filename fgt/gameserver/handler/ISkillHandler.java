package fgt.gameserver.handler;

import fgt.commons.logging.CLogger;

import fgt.gameserver.enums.skills.SkillType;
import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.skills.L2Skill;

public interface ISkillHandler
{
	public static final CLogger LOGGER = new CLogger(ISkillHandler.class.getName());
	
	/**
	 * The worker method called by a {@link Creature} when using a {@link L2Skill}.
	 * @param creature : The Creature who uses that L2Skill.
	 * @param skill : The L2Skill object itself.
	 * @param targets : The eventual targets.
	 */
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets);
	
	/**
	 * @return all known {@link SkillType}s.
	 */
	public SkillType[] getSkillIds();
}