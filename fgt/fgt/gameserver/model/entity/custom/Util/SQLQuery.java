package fgt.gameserver.model.entity.custom.Util;

import java.sql.Connection;

public interface SQLQuery {
    public void execute(Connection con);
}
