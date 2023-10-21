package fgt.loginserver;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import fgt.commons.mmocore.IAcceptFilter;
import fgt.commons.mmocore.IClientFactory;
import fgt.commons.mmocore.IMMOExecutor;
import fgt.commons.mmocore.MMOConnection;
import fgt.commons.mmocore.ReceivablePacket;

import fgt.loginserver.data.manager.IpBanManager;
import fgt.loginserver.network.LoginClient;
import fgt.loginserver.network.serverpackets.Init;
import fgt.util.IPv4Filter;

public class SelectorHelper implements IMMOExecutor<LoginClient>, IClientFactory<LoginClient>, IAcceptFilter
{
	private final ThreadPoolExecutor _generalPacketsThreadPool;
	
	private final IPv4Filter _ipv4filter;
	
	public SelectorHelper()
	{
		_generalPacketsThreadPool = new ThreadPoolExecutor(4, 6, 15L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		_ipv4filter = new IPv4Filter();
	}
	
	@Override
	public boolean accept(Socket socket)
	{
		return _ipv4filter.accept(socket) && !IpBanManager.getInstance().isBannedAddress(socket.getInetAddress());
	}
	
	@Override
	public LoginClient create(MMOConnection<LoginClient> con)
	{
		LoginClient client = new LoginClient(con);
		client.sendPacket(new Init(client));
		return client;
	}
	
	@Override
	public void execute(ReceivablePacket<LoginClient> packet)
	{
		_generalPacketsThreadPool.execute(packet);
	}
}