package fgt.commons.logging.formatter;

import java.util.logging.LogRecord;

import fgt.commons.lang.StringUtil;
import fgt.commons.logging.MasterFormatter;

public class ChatLogFormatter extends MasterFormatter
{
	@Override
	public String format(LogRecord record)
	{
		final StringBuilder sb = new StringBuilder();
		
		StringUtil.append(sb, "[", getFormatedDate(record.getMillis()), "] ");
		
		for (Object p : record.getParameters())
		{
			if (p == null)
				continue;
			
			StringUtil.append(sb, p, " ");
		}
		
		StringUtil.append(sb, record.getMessage(), CRLF);
		
		return sb.toString();
	}
}