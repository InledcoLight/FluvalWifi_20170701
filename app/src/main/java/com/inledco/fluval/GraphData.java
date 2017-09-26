// _______  _                          _______  _
//(  ____ \( \      |\     /||\     /|(  ___  )( \
//| (    \/| (      | )   ( || )   ( || (   ) || (
//| (__    | |      | |   | || |   | || (___) || |
//|  __)   | |      | |   | |( (   ) )|  ___  || |
//| (      | |      | |   | | \ \_/ / | (   ) || |
//| )      | (____/\| (___) |  \   /  | )   ( || (____/\
//|/       (_______/(_______)   \_/   |/     \|(_______/
//.---------------------.
//| Fluval Android App  |
//|Coded by Marcel Becks|
//.---------------------.

package com.inledco.fluval;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphData
{
    private String timeSunriseStart;
    private String timeSunriseStop;
    private String valueSunriseStart;
    private String valueSunriseStop;
    private String timeBreakStart;
    private String timeBreakStop;
    private String valueBreakStart;
    private String valueBreakStop;
    private String timeSunsetStart;
    private String timeSunsetStop;
    private String valueSunsetStart;
    private String valueSunsetStop;

    public GraphData ()
    {
        timeSunriseStart = "0000";
        timeSunriseStop = "0000";
        valueSunriseStart = "000";
        valueSunriseStop = "000";
        timeBreakStart = "0000";
        timeBreakStop = "0000";
        valueBreakStart = "000";
        valueBreakStop = "000";
        timeSunsetStart = "0000";
        timeSunsetStop = "0000";
        valueSunsetStart = "000";
        valueSunsetStop = "000";
    }

    @Override
    public String toString ()
    {
        String output = "Graph Data:" +
                        "\n" +
                        "timeSunriseStart" +
                        "[" +
                        timeSunriseStart +
                        "]" +
                        "\n" +
                        "timeSunriseStop" +
                        "[" +
                        timeSunriseStop +
                        "]" +
                        "\n" +
                        "valueSunriseStart" +
                        "[" +
                        valueSunriseStart +
                        "]" +
                        "\n" +
                        "valueSunriseStop" +
                        "[" +
                        valueSunriseStop +
                        "]" +
                        "\n" +
                        "timeBreakStart" +
                        "[" +
                        timeBreakStart +
                        "]" +
                        "\n" +
                        "timeBreakStop" +
                        "[" +
                        timeBreakStop +
                        "]" +
                        "\n" +
                        "valueBreakStart" +
                        "[" +
                        valueBreakStart +
                        "]" +
                        "\n" +
                        "valueBreakStop" +
                        "[" +
                        valueBreakStop +
                        "]" +
                        "\n" +
                        "timeSunsetStart" +
                        "[" +
                        timeSunsetStart +
                        "]" +
                        "\n" +
                        "timeSunsetStop" +
                        "[" +
                        timeSunsetStop +
                        "]" +
                        "\n" +
                        "valueSunsetStart" +
                        "[" +
                        valueSunsetStart +
                        "]" +
                        "\n" +
                        "valueSunsetStop" +
                        "[" +
                        valueSunsetStop +
                        "]" +
                        "\n";

        return output;
    }

    public String fillnumbers ( int number )
    {
        String output = "" + number;
        if ( number >= 100 )
        {
            output = "100";
        }
        else
        {
            for ( int i = 0; i < 4 - output.length(); i++ )
            {
                output = "0" + output;
            }
        }

        return output;
    }

    public String getHour ( String time )
    {
        return time.substring( 0, 2 );
    }

    public String getMin ( String time )
    {
        return time.substring( 2, 4 );
    }

    public String createRealTimeText ( String time, boolean isGerman )
    {
        return timeConverter( Integer.parseInt( getHour( time ) ), Integer.parseInt( getMin( time ) ), isGerman );
    }

    public String generateGraph ()
    {
        return timeSunriseStart +
               " " +
               timeSunriseStop +
               " " +
               valueSunriseStart +
               " " +
               valueSunriseStop +
               " " +
               timeBreakStart +
               " " +
               timeBreakStop +
               " " +
               valueBreakStart +
               " " +
               valueBreakStop +
               " " +
               timeSunsetStart +
               " " +
               timeSunsetStop +
               " " +
               valueSunsetStart +
               " " +
               valueSunsetStop;
    }

    public void loadGraph ( String graphData )
    {
        //		System.out.println(graphData);
        String[] splitGraphData = graphData.split( " " );
        timeSunriseStart = splitGraphData[0];
        timeSunriseStop = splitGraphData[1];
        valueSunriseStart = splitGraphData[2];
        valueSunriseStop = splitGraphData[3];
        timeBreakStart = splitGraphData[4];
        timeBreakStop = splitGraphData[5];
        valueBreakStart = splitGraphData[6];
        valueBreakStop = splitGraphData[7];
        timeSunsetStart = splitGraphData[8];
        timeSunsetStop = splitGraphData[9];
        valueSunsetStart = splitGraphData[10];
        valueSunsetStop = splitGraphData[11];
        //	System.out.println(toString());
    }

    private static String timeConverter ( int hh, int mm, boolean isGerman )
    {

        Date dateTemp = new Date();
        SimpleDateFormat sf = new SimpleDateFormat();
        String output;
        dateTemp.setHours( hh );
        dateTemp.setMinutes( mm );

        if ( isGerman )
        {
            sf = new SimpleDateFormat( "HH:mm" );
            output = sf.format( dateTemp ) + " h";
        }
        else
        {
            sf = new SimpleDateFormat( "hh:mm a" );
            output = sf.format( dateTemp );
        }

        return output;
    }

    public String getTimeSunriseStart ()
    {
        return timeSunriseStart;
    }

    public void setTimeSunriseStart ( String timeSunriseStart )
    {
        this.timeSunriseStart = timeSunriseStart;
    }

    public String getTimeSunriseStop ()
    {
        return timeSunriseStop;
    }

    public void setTimeSunriseStop ( String timeSunriseStop )
    {
        this.timeSunriseStop = timeSunriseStop;
    }

    public String getValueSunriseStart ()
    {
        return valueSunriseStart;
    }

    public void setValueSunriseStart ( String valueSunriseStart )
    {
        this.valueSunriseStart = valueSunriseStart;
    }

    public String getValueSunriseStop ()
    {
        return valueSunriseStop;
    }

    public void setValueSunriseStop ( String valueSunriseStop )
    {
        this.valueSunriseStop = valueSunriseStop;
    }

    public String getTimeBreakStart ()
    {
        return timeBreakStart;
    }

    public void setTimeBreakStart ( String timeBreakStart )
    {
        this.timeBreakStart = timeBreakStart;
    }

    public String getTimeBreakStop ()
    {
        return timeBreakStop;
    }

    public void setTimeBreakStop ( String timeBreakStop )
    {
        this.timeBreakStop = timeBreakStop;
    }

    public String getValueBreakStart ()
    {
        return valueBreakStart;
    }

    public void setValueBreakStart ( String valueBreakStart )
    {
        this.valueBreakStart = valueBreakStart;
    }

    public String getValueBreakStop ()
    {
        return valueBreakStop;
    }

    public void setValueBreakStop ( String valueBreakStop )
    {
        this.valueBreakStop = valueBreakStop;
    }

    public String getTimeSunsetStart ()
    {
        return timeSunsetStart;
    }

    public void setTimeSunsetStart ( String timeSunsetStart )
    {
        this.timeSunsetStart = timeSunsetStart;
    }

    public String getTimeSunsetStop ()
    {
        return timeSunsetStop;
    }

    public void setTimeSunsetStop ( String timeSunsetStop )
    {
        this.timeSunsetStop = timeSunsetStop;
    }

    public String getValueSunsetStart ()
    {
        return valueSunsetStart;
    }

    public void setValueSunsetStart ( String valueSunsetStart )
    {
        this.valueSunsetStart = valueSunsetStart;
    }

    public String getValueSunsetStop ()
    {
        return valueSunsetStop;
    }

    public void setValueSunsetStop ( String valueSunsetStop )
    {
        this.valueSunsetStop = valueSunsetStop;
    }
}
