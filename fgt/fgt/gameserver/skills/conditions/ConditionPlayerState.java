package fgt.gameserver.skills.conditions;

import fgt.gameserver.enums.skills.PlayerState;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;

public class ConditionPlayerState extends Condition
{
	private final PlayerState _check;
	private final boolean _required;
	
	public ConditionPlayerState(PlayerState check, boolean required)
	{
		_check = check;
		_required = required;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		final Player player = (effector instanceof Player) ? (Player) effector : null;
		
		switch (_check)
		{
			case RESTING:
				return (player == null) ? !_required : player.isSitting() == _required;
			
			case MOVING:
				return effector.isMoving() == _required;
			
			case RUNNING:
				return effector.isMoving() == _required && effector.isRunning() == _required;
			
			case RIDING:
				return effector.isRiding() == _required;
			
			case FLYING:
				return effector.isFlying() == _required;
			
			case BEHIND:
				return effector.isBehind(effected) == _required;
			
			case FRONT:
				return effector.isInFrontOf(effected) == _required;
			
			case OLYMPIAD:
				return (player == null) ? !_required : player.isInOlympiadMode() == _required;
		}
		return !_required;
	}
}