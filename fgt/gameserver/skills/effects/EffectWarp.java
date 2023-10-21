package fgt.gameserver.skills.effects;

import fgt.commons.math.MathUtil;

import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.enums.skills.FlyType;
import fgt.gameserver.geoengine.GeoEngine;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.location.Location;
import fgt.gameserver.network.serverpackets.FlyToLocation;
import fgt.gameserver.network.serverpackets.ValidateLocation;
import fgt.gameserver.skills.AbstractEffect;
import fgt.gameserver.skills.L2Skill;

public class EffectWarp extends AbstractEffect
{
	public EffectWarp(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.WARP;
	}
	
	@Override
	public boolean onStart()
	{
		final Creature actor = (isSelfEffect()) ? getEffector() : getEffected();
		
		if (actor.isMovementDisabled())
			return false;
		
		final double angle = MathUtil.convertHeadingToDegree(actor.getHeading());
		final double radian = Math.toRadians(angle);
		final double course = Math.toRadians(getSkill().getFlyCourse());
		
		final int x1 = (int) (Math.cos(Math.PI + radian + course) * getSkill().getFlyRadius());
		final int y1 = (int) (Math.sin(Math.PI + radian + course) * getSkill().getFlyRadius());
		
		int x = actor.getX() + x1;
		int y = actor.getY() + y1;
		int z = actor.getZ();
		
		final Location loc = GeoEngine.getInstance().getValidLocation(actor, x, y, z);
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		
		// TODO: check if this AI intention is retail-like.
		actor.getAI().tryToIdle();
		
		actor.broadcastPacket(new FlyToLocation(actor, x, y, z, FlyType.DUMMY));
		actor.getAttack().stop();
		actor.getCast().stop();
		
		actor.setXYZ(x, y, z);
		actor.broadcastPacket(new ValidateLocation(actor));
		
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}