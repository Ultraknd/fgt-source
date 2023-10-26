package fgt.commons.lang;

import fgt.commons.logging.CLogger;

import java.util.regex.Pattern;



public class Strings
{
    protected static final CLogger _log = new CLogger(Strings.class.getSimpleName());
	
    private static final char hex[] =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String bytesToString(byte[] b)
    {
        String ret = "";
        for (byte element : b)
        {
            ret += String.valueOf(hex[(element & 0xF0) >> 4]);
            ret += String.valueOf(hex[element & 0x0F]);
        }
        return ret;
    }

    public static String addSlashes(String s)
    {
        if (s == null)
        {
            return "";
        }
        s = s.replace("\\", "\\\\");
        s = s.replace("\"", "\\\"");
        s = s.replace("@", "\\@");
        s = s.replace("'", "\\'");
        return s;
    }

    public static String addSkobka(String s)
    {
        if (s == null)
        {
            return "";
        }
        s = s.replace("{", "");
        s = s.replace("}", "");
        return s;
    }

    public static String stripSlashes(String s)
    {
        if (s == null)
        {
            return "";
        }
        s = s.replace("\\'", "'");
        s = s.replace("\\\\", "\\");
        return s;
    }

    public static Boolean parseBoolean(Object x)
    {
        if (x == null)
        {
            return false;
        }
        if (x instanceof Number)
        {
            return ((Number) x).intValue() > 0;
        }
        if (x instanceof Boolean)
        {
            return (Boolean) x;
        }
        if (x instanceof Double)
        {
            return Math.abs((Double) x) < 0.00001;
        }
        return !String.valueOf(x).isEmpty();
    }
    private static String[] tr;
    private static String[] trb;
    private static String[] trcode;

    public static String translit(String s)
    {
        for (int i = 0; i < tr.length; i += 2)
        {
            s = s.replace(tr[i], tr[i + 1]);
        }
        return s;
    }

    public static String fromTranslit(String s, int type)
    {
        if (type == 1)
        {
            for (int i = 0; i < trb.length; i += 2)
            {
                s = s.replace(trb[i], trb[i + 1]);
            }
        }
        else if (type == 2)
        {
            for (int i = 0; i < trcode.length; i += 2)
            {
                s = s.replace(trcode[i], trcode[i + 1]);
            }
        }
        return s;
    }

    public static String replace(String str, String regex, int flags, String replace)
    {
        return Pattern.compile(regex, flags).matcher(str).replaceAll(replace);
    }

    public static boolean matches(String str, String regex, int flags)
    {
        return Pattern.compile(regex, flags).matcher(str).matches();
    }

	public static String bbParse(String s)
	{
		if(s == null)
			return null;

		s = s.replace("\r", "");
		s = s.replaceAll("(\\s|\"|\'|\\(|^|\n)\\*(.*?)\\*(\\s|\"|\'|\\)|\\?|\\.|!|:|;|,|$|\n)", "$1<font color=\"LEVEL\">$2</font>$3"); // *S1*
		s = s.replaceAll("(\\s|\"|\'|\\(|^|\n)\\$(.*?)\\$(\\s|\"|\'|\\)|\\?|\\.|!|:|;|,|$|\n)", "$1<font color=\"00FFFF\">$2</font>$3");// $S1$
		s = replace(s, "^!(.*?)$", Pattern.MULTILINE, "<font color=\"FFFFFF\">$1</font>\n\n");
		s = s.replaceAll("%%\\s*\n", "<br1>");
		s = s.replaceAll("\n\n+", "<br>");
		s = replace(s, "\\[([^\\]\\|]*?)\\|([^\\]]*?)\\]", Pattern.DOTALL, "<br1><a action=\"bypass -h $1\">$2</a>");
		s = s.replaceAll(" @", "\" msg=\"");

		return s;
	}

    /**
     * *
     * Склеивалка для строк
     *
     * @param glueStr - строка разделитель, может быть пустой строкой или null
     * @param strings - массив из строк которые надо склеить
     * @param startIdx - начальный индекс, если указать отрицательный то он
     * отнимется от количества строк
     * @param maxCount - мескимум элементов, если 0 - вернутся пустая строка,
     * если отрицательный то учитыватся не будет
     * @return 
     */
    public static String joinStrings(String glueStr, String[] strings, int startIdx, int maxCount)
    {
        String result = "";
        if (startIdx < 0)
        {
            startIdx += strings.length;
            if (startIdx < 0)
            {
                return result;
            }
        }
        while (startIdx < strings.length && maxCount != 0)
        {
            if (!result.isEmpty() && glueStr != null && !glueStr.isEmpty())
            {
                result += glueStr;
            }
            result += strings[startIdx++];
            maxCount--;
        }
        return result;
    }

    /**
     * *
     * Склеивалка для строк
     *
     * @param glueStr - строка разделитель, может быть пустой строкой или null
     * @param strings - массив из строк которые надо склеить
     * @param startIdx - начальный индекс, если указать отрицательный то он
     * отнимется от количества строк
     * @return 
     */
    public static String joinStrings(String glueStr, String[] strings, int startIdx)
    {
        return joinStrings(glueStr, strings, startIdx, -1);
    }

    /**
     * *
     * Склеивалка для строк
     *
     * @param glueStr - строка разделитель, может быть пустой строкой или null
     * @param strings - массив из строк которые надо склеить
     * @return 
     */
    public static String joinStrings(String glueStr, String[] strings)
    {
        return joinStrings(glueStr, strings, 0);
    }

    public static String stripToSingleLine(String s)
    {
        if (s.isEmpty())
        {
            return s;
        }
        s = s.replaceAll("\\\\n", "\n");
        int i = s.indexOf("\n");
        if (i > -1)
        {
            s = s.substring(0, i);
        }
        return s;
    }

	public static String htmlButton(String value, String action, int width)
	{
		return htmlButton(value, action, width, 22);
	}

	public static String htmlButton(String value, String action, int width, int height)
	{
		return String.format("<button value=\"%s\" action=\"%s\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=%d height=%d fore=\"L2UI_CT1.Button_DF_Small\">", value, action, width, height);
	}
}
