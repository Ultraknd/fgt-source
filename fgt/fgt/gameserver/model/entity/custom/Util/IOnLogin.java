package fgt.gameserver.model.entity.custom.Util;


import fgt.gameserver.model.actor.Player;

public abstract interface IOnLogin
{
    public abstract void intoTheGame(Player paramL2PcInstance);
}
