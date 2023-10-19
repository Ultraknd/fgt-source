package fgt.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.location.Location;
import fgt.gameserver.model.zone.type.DerbyTrackZone;
import fgt.gameserver.model.zone.type.PeaceZone;
import fgt.gameserver.model.zone.type.TownZone;
import fgt.gameserver.model.zone.type.subtype.ZoneType;
import fgt.gameserver.skills.L2Skill;

public final class WorldRegion
{
	private final Map<Integer, WorldObject> _objects = new ConcurrentHashMap<>();
	
	private final List<WorldRegion> _surroundingRegions = new ArrayList<>();
	private final List<ZoneType> _zones = new ArrayList<>();
	
	private final int _tileX;
	private final int _tileY;
	
	private final AtomicBoolean _isActive = new AtomicBoolean();
	private final AtomicInteger _playersCount = new AtomicInteger();
	
	public WorldRegion(int x, int y)
	{
		_tileX = x;
		_tileY = y;
	}
	
	@Override
	public String toString()
	{
		return "WorldRegion " + _tileX + "_" + _tileY + ", _active=" + _isActive.get() + ", _playersCount=" + _playersCount.get() + "]";
	}
	
	public Collection<WorldObject> getObjects()
	{
		return _objects.values();
	}
	
	public void addSurroundingRegion(WorldRegion region)
	{
		_surroundingRegions.add(region);
	}
	
	public List<WorldRegion> getSurroundingRegions()
	{
		return _surroundingRegions;
	}
	
	public List<ZoneType> getZones()
	{
		return _zones;
	}
	
	public void addZone(ZoneType zone)
	{
		_zones.add(zone);
	}
	
	public void removeZone(ZoneType zone)
	{
		_zones.remove(zone);
	}
	
	public void revalidateZones(Creature character)
	{
		// Do NOT update the world region while the character is still in the process of teleporting
		if (character.isTeleporting())
			return;
		
		_zones.forEach(z -> z.revalidateInZone(character));
	}
	
	public void removeFromZones(Creature character)
	{
		_zones.forEach(z -> z.removeCharacter(character));
	}
	
	public boolean containsZone(int zoneId)
	{
		for (ZoneType z : _zones)
		{
			if (z.getId() == zoneId)
				return true;
		}
		return false;
	}
	
	public boolean checkEffectRangeInsidePeaceZone(L2Skill skill, Location loc)
	{
		final int range = skill.getEffectRange();
		final int up = loc.getY() + range;
		final int down = loc.getY() - range;
		final int left = loc.getX() + range;
		final int right = loc.getX() - range;
		
		for (ZoneType e : _zones)
		{
			if ((e instanceof TownZone && ((TownZone) e).isPeaceZone()) || e instanceof DerbyTrackZone || e instanceof PeaceZone)
			{
				if (e.isInsideZone(loc.getX(), up, loc.getZ()))
					return false;
				
				if (e.isInsideZone(loc.getX(), down, loc.getZ()))
					return false;
				
				if (e.isInsideZone(left, loc.getY(), loc.getZ()))
					return false;
				
				if (e.isInsideZone(right, loc.getY(), loc.getZ()))
					return false;
				
				if (e.isInsideZone(loc.getX(), loc.getY(), loc.getZ()))
					return false;
			}
		}
		return true;
	}
	
	public boolean isActive()
	{
		return _isActive.get();
	}
	
	public int getPlayersCount()
	{
		return _playersCount.get();
	}
	
	/**
	 * Check if neighbors (including self) aren't inhabited.
	 * @return true if the above condition is met.
	 */
	public boolean isEmptyNeighborhood()
	{
		for (WorldRegion neighbor : _surroundingRegions)
		{
			if (neighbor.getPlayersCount() != 0)
				return false;
		}
		return true;
	}
	
	/**
	 * This function turns this region's AI on or off.
	 * @param value : if true, activate hp/mp regen and random animation. If false, clean aggro/attack list, set objects on IDLE and drop their AI tasks.
	 */
	public void setActive(boolean value)
	{
		if (!_isActive.compareAndSet(!value, value))
			return;
		
		for (WorldObject object : _objects.values())
		{
			if (value)
				object.onActiveRegion();
			else
				object.onInactiveRegion();
		}
	}
	
	/**
	 * Put the given object into WorldRegion objects map. If it's a player, increment the counter (used for region activation/desactivation).
	 * @param object : The object to register into this region.
	 */
	public void addVisibleObject(WorldObject object)
	{
		if (object == null)
			return;
		
		_objects.put(object.getObjectId(), object);
		
		if (object instanceof Player)
			_playersCount.incrementAndGet();
	}
	
	/**
	 * Remove the given object from WorldRegion objects map. If it's a player, decrement the counter (used for region activation/desactivation).
	 * @param object : The object to remove from this region.
	 */
	public void removeVisibleObject(WorldObject object)
	{
		if (object == null)
			return;
		
		_objects.remove(object.getObjectId());
		
		if (object instanceof Player)
			_playersCount.decrementAndGet();
	}
}