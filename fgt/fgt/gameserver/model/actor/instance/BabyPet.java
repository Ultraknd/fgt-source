package fgt.gameserver.model.actor.instance;

import java.util.concurrent.Future;

import fgt.commons.math.MathUtil;
import fgt.commons.pool.ThreadPool;
import fgt.commons.random.Rnd;

import fgt.gameserver.data.SkillTable;
import fgt.gameserver.enums.actors.NpcSkillType;
import fgt.gameserver.enums.skills.SkillTargetType;
import fgt.gameserver.enums.skills.SkillType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.model.holder.IntIntHolder;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;
import fgt.gameserver.skills.L2Skill;

/**
 * A BabyPet can heal his owner. It got 2 heal power, weak or strong.
 * <ul>
 * <li>If the owner's HP is more than 80%, do nothing.</li>
 * <li>If the owner's HP is under 15%, have 75% chances of using a strong heal.</li>
 * <li>Otherwise, have 25% chances for weak heal.</li>
 * </ul>
 */
public final class BabyPet extends Pet
{
	protected IntIntHolder _majorHeal = null;
	protected IntIntHolder _minorHeal = null;
	
	private Future<?> _castTask;
	
	public BabyPet(int objectId, NpcTemplate template, Player owner, ItemInstance control)
	{
		super(objectId, template, owner, control);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		double healPower = 0;
		int skillLevel;
		
		for (L2Skill skill : getTemplate().getSkills(NpcSkillType.HEAL))
		{
			if (skill.getTargetType() != SkillTargetType.OWNER_PET || skill.getSkillType() != SkillType.HEAL)
				continue;
			
			// The skill level is calculated on the fly. Template got an skill level of 1.
			skillLevel = getSkillLevel(skill.getId());
			if (skillLevel <= 0)
				continue;
			
			if (healPower == 0)
			{
				// set both heal types to the same skill
				_majorHeal = new IntIntHolder(skill.getId(), skillLevel);
				_minorHeal = _majorHeal;
				
				healPower = skill.getPower();
			}
			else
			{
				// another heal skill found - search for most powerful
				if (skill.getPower() > healPower)
					_majorHeal = new IntIntHolder(skill.getId(), skillLevel);
				else
					_minorHeal = new IntIntHolder(skill.getId(), skillLevel);
			}
		}
		startCastTask();
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		stopCastTask();
		
		return true;
	}
	
	@Override
	public synchronized void unSummon(Player owner)
	{
		stopCastTask();
		
		super.unSummon(owner);
	}
	
	@Override
	public void doRevive()
	{
		super.doRevive();
		
		startCastTask();
	}
	
	@Override
	public final int getSkillLevel(int skillId)
	{
		// Unknown skill. Return 0.
		if (getSkill(skillId) == null)
			return 0;
		
		// Baby pet levels increase the skill level by 1 per 6 levels.
		final int level = 1 + getStatus().getLevel() / 6;
		
		// Validate skill level.
		return MathUtil.limit(level, 1, SkillTable.getInstance().getMaxLevel(skillId));
	}
	
	private final void startCastTask()
	{
		if (_majorHeal != null && _castTask == null && !isDead())
			_castTask = ThreadPool.scheduleAtFixedRate(this::castSkill, 3000, 1000);
	}
	
	private final void stopCastTask()
	{
		if (_castTask != null)
		{
			_castTask.cancel(false);
			_castTask = null;
		}
	}
	
	private void castSkill()
	{
		final Player owner = getOwner();
		if (owner == null || owner.isDead() || owner.isInvul())
			return;
		
		L2Skill skill = null;
		
		final double hpRatio = owner.getStatus().getHpRatio();
		if (hpRatio < 0.15)
		{
			skill = _majorHeal.getSkill();
			if (isSkillDisabled(skill) || getStatus().getMp() < skill.getMpConsume() || Rnd.get(100) > 75)
				skill = null;
		}
		else if (_majorHeal.getSkill() != _minorHeal.getSkill() && hpRatio < 0.8)
		{
			skill = _minorHeal.getSkill();
			if (isSkillDisabled(skill) || getStatus().getMp() < skill.getMpConsume() || Rnd.get(100) > 25)
				skill = null;
		}
		
		if (skill != null)
		{
			// pet not following and owner outside cast range
			if (!getAI().getFollowStatus() && !isIn3DRadius(getOwner(), skill.getCastRange()))
				return;
			
			getAI().tryToCast(getOwner(), skill);
			
			getOwner().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(skill));
		}
	}
}