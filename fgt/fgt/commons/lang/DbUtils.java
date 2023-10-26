package fgt.commons.lang;

import java.sql.*;

public class DbUtils {

	public static void close(Connection conn) throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}

	public static void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

	public static void close(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
	}

	public static void close(Statement stmt, ResultSet rs) throws SQLException {
		close(stmt);
		close(rs);
	}

	public static void closeQuietly(Connection conn) {
		try {
			close(conn);
		} catch (SQLException e) {
		}
	}

	public static void closeQuietly(Connection conn, Statement stmt) {
		try {
			closeQuietly(stmt);
		} finally {
			closeQuietly(conn);
		}
	}

	public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {

		try {
			closeQuietly(rs);
		} finally {
			try {
				closeQuietly(stmt);
			} finally {
				closeQuietly(conn);
			}
		}
	}

	public static void closeQuietly(Connection conn, ResultSet rs) {

		try {
			closeQuietly(rs);
		} finally {
			closeQuietly(conn);
		}
	}

	public static void closeQuietly(ResultSet rs) {
		try {
			close(rs);
		} catch (SQLException e) {
		}
	}

	public static void closeQuietly(Statement stmt) {
		try {
			close(stmt);
		} catch (SQLException e) {
		}
	}

	public static void closeQuietly(Statement stmt, ResultSet rs) {
		try {
			closeQuietly(stmt);
		} finally {
			closeQuietly(rs);
		}
	}
	
	public static void close(Object[] objects)
    {
        for (Object object : objects) {
            if (object == null)
                continue;
            try
            {
                if(object instanceof Connection)
                    ((Connection)object).close();
                else if(object instanceof Statement)
                    ((Statement)object).close();
                else if(object instanceof PreparedStatement)
                    ((PreparedStatement)object).close();
                else if(object instanceof ResultSet)
                    ((ResultSet)object).close();
                else
                {
                    Thread.dumpStack();
                    throw new IllegalArgumentException("Illegal close connection type " + object.toString());
                }
            }
            catch (SQLException | IllegalArgumentException e)
            {
            }
        }
    }
}