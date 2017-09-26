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

import java.util.ArrayList;

public class Slot
{
    private int slotNr;
    private String name;
    private String type;
    private String productType;
    private String channelCount;
    private String lightProduct;

    private String RGBColor;
    private int RGBSplit[];
    private int RGBMaxValue;

    private String[] channelsID;
    private String[] channelsConnecting;
    private ArrayList< String > channelsActiveNamesList;
    private GraphData[] channelsGraphData;
    private String[] channelsLightPower;
    private String[] channelsLightColor;
    private boolean active;

    public Slot ( int slotNr )
    {

        this.slotNr = slotNr;
        name = "";
        channelsID = new String[5];
        channelsConnecting = new String[5];
        channelsGraphData = new GraphData[5];
        channelsLightPower = new String[5];
        channelsLightColor = new String[5];
        RGBColor = "";
        RGBMaxValue = 0;
        RGBSplit = new int[3];

        int pointer = 0;
        switch ( slotNr )
        {
            case 0:
                for ( int i = 0; i < 2; i++ )
                {
                    channelsID[pointer] = "0" + i;
                    pointer++;
                }
                break;

            case 1:
                for ( int i = 2; i < 4; i++ )
                {
                    channelsID[pointer] = "0" + i;
                    pointer++;
                }
                break;

            case 2:
                for ( int i = 10; i < 15; i++ )
                {
                    channelsID[pointer] = "" + i;
                    pointer++;
                }
                break;
        }

        for ( int i = 0; i < 5; i++ )
        {
            channelsGraphData[i] = new GraphData();
        }

        for ( int i = 0; i < 5; i++ )
        {
            channelsLightColor[i] = "000";
            channelsLightPower[i] = "000";
        }

        active = false;
    }

    public int getSlotNr ()
    {
        return slotNr;
    }

    public void setSlotNr ( int slotNr )
    {
        this.slotNr = slotNr;
    }

    public String getName ()
    {
        return name;
    }

    public void setName ( String name )
    {
        this.name = name;
    }

    public String getLightProduct ()
    {
        return lightProduct;
    }

    public void setLightProduct ( String lightProduct )
    {
        this.lightProduct = lightProduct;
    }

    public String[] getChannelsID ()
    {
        return channelsID;
    }

    public void setChannelsID ( String[] channelsID )
    {
        this.channelsID = channelsID;
    }

    public String[] getChannelsConnecting ()
    {
        return channelsConnecting;
    }

    public void setChannelsConnecting ( String[] channelsConnecting )
    {
        this.channelsConnecting = channelsConnecting;
    }

    public GraphData[] getChannelsGraphData ()
    {
        return channelsGraphData;
    }

    public void setChannelsGraphData ( String channelsGraphData, int index )
    {
        this.channelsGraphData[index].loadGraph( channelsGraphData );
    }

    public String[] getChannelsLightPower ()
    {
        return channelsLightPower;
    }

    public void setChannelsLightPower ( String channelsLightPower, int index )
    {
        this.channelsLightPower[index] = fillnumbers( Integer.parseInt( channelsLightPower ) );
    }

    public String[] getChannelsLightColor ()
    {
        return channelsLightColor;
    }

    public void setChannelsLightColor ( String[] channelsLightColor )
    {
        this.channelsLightColor = channelsLightColor;
    }

    public boolean isActive ()
    {
        return active;
    }

    public void setActive ( boolean active )
    {
        this.active = active;
    }

    public String getType ()
    {
        return type;
    }

    public void setType ( String type )
    {
        this.type = type;
    }

    public String getProductType ()
    {
        return productType;
    }

    public void setProductType ( String productType )
    {
        this.productType = productType;
    }

    public String getChannelCount ()
    {
        return channelCount;
    }

    public void setChannelCount ( String channelCount )
    {
        this.channelCount = channelCount;
    }

    public ArrayList< String > getChannelsActiveNamesList ()
    {
        return channelsActiveNamesList;
    }

    public void setChannelsActiveNamesList ( ArrayList< String > channelsActiveNamesList )
    {
        this.channelsActiveNamesList = new ArrayList< String >();
        this.channelsActiveNamesList.addAll( channelsActiveNamesList );
    }

    public void setChannelsGraphData ( GraphData[] channelsGraphData )
    {
        this.channelsGraphData = channelsGraphData;
    }

    public void setChannelsLightPower ( String[] channelsLightPower )
    {
        this.channelsLightPower = channelsLightPower;
    }

    public String getRGBColor ()
    {
        return RGBColor;
    }

    public void setRGBColor ( String rGBColor )
    {
        RGBColor = rGBColor;
    }

    public int[] getRGBSplit ()
    {
        return RGBSplit;
    }

    public void setRGBSplit ( int[] rGBSplit )
    {
        RGBSplit = rGBSplit;
    }

    public int getRGBMaxValue ()
    {
        return RGBMaxValue;
    }

    public void setRGBMaxValue ( int rGBMaxValue )
    {
        RGBMaxValue = rGBMaxValue;
    }

    public String fillnumbers ( int number )
    {
        String output = "" + number;
        if ( number > 100 )
        {
            output = "100";
        }
        for ( int i = 0; i < 4 - output.length(); i++ )
        {
            output = "0" + output;
        }

        return output;
    }
}

