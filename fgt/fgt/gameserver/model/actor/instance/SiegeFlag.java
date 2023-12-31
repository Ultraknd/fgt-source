package fgt.gameserver.model.actor.instance;

import java.util.concurrent.Future;

import fgt.commons.pool.ThreadPool;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.model.pledge.Clan;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;
import fgt.gameserver.skills.L2Skill;

public class SiegeFlag extends Npc
{
	private final Clan _clan;
	
	private Future<?> _task;
	
	public SiegeFlag(Clan clan, int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		_clan = clan;
		_clan.setFlag(this);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		// Reset clan flag to null.
		_clan.setFlag(null);
		
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		// Stop the task.
		if (_task != null)
		{
			_task.cancel(false);
			_task = null;
		}
		
		super.deleteMe();
	}
	
	@Override
	public void onInteract(Player player)
	{
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, L2Skill skill)
	{
		// Any SiegeSummon can hurt SiegeFlag (excepted Swoop Cannon - anti-infantery summon).
		if (attacker instanceof SiegeSummon && ((SiegeSummon) attacker).getNpcId() == SiegeSummon.SWOOP_CANNON_ID)
			return;
		
		// Send warning to owners of headquarters that theirs base is under attack.
		if (_task == null)
		{
			_task = ThreadPool.schedule(() -> _task = null, 30000);
			
			_clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.BASE_UNDER_ATTACK));
		}
		super.reduceCurrentHp(damage, attacker, skill);
	}
	
	@Override
	public void addFuncsToNewCharacter()
	{
	}
	
	@Override
	public boolean canBeHealed()
	{
		return false;
	}
}