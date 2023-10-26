package fgt.gameserver.model.entity.custom.Util.task;

import fgt.commons.logging.CLogger;
import fgt.commons.pool.ConnectionPool;
import fgt.gameserver.GameServer;
import fgt.gameserver.model.entity.custom.Util.SQLQuery;
import fgt.gameserver.taskmanager.ExclusiveTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


public final class SQLQueue extends ExclusiveTask {

    private static SQLQueue _instance;
    private static final CLogger _log = new CLogger(GameServer.class.getName());
    private ArrayList<SQLQuery> _query = new ArrayList<>();

    public static SQLQueue getInstance() {
        if (_instance == null) {
            _instance = new SQLQueue();
        }

        return _instance;
    }

    private SQLQueue() {
        schedule(60000);
        _log.info("SQLQueue: started");
    }

    protected Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }

    public synchronized void run() {
        flush();
    }

    private SQLQuery getNextQuery() {
        synchronized (_query.getClass()) {
            if (_query.isEmpty()) {
                return null;
            }
            return _query.remove(0);
        }
    }
    private boolean _running = false;

    private void flush() {
        Connection con = null;
        if (_running) {
            return;
        }
        try {
            _running = true;
            con = getConnection();
            for (SQLQuery q; (q = getNextQuery()) != null;) {
                try {
                    q.execute(con);
                } catch (Exception e) {
                    _log.error("SQLQueue: Error executing " + q.getClass().getSimpleName(), e);
                }
            }
        } catch (SQLException e) {
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
            _running = false;
        }

    }

    @Override
    protected void onElapsed() {
        flush();
        schedule(60000);
    }

    public void add(SQLQuery q) {
        synchronized (_query.getClass()) {
            _query.add(q);
        }
    }
}
