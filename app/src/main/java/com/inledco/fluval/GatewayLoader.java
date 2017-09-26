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

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class GatewayLoader
{
	private Slot[] slots;
    private LightDataDeviceList lightDataDeviceList;
    private ArrayList< String > SendSocketFireCommands;
    private GatewaySettingsData gatewaySettingsData;

    public GatewayLoader ( boolean isGerman )
    {
        lightDataDeviceList = new LightDataDeviceList( isGerman );
        slots = new Slot[3];
        for ( int i = 0; i < slots.length; i++ )
        {
            slots[i] = new Slot( i );
        }
        SendSocketFireCommands = new ArrayList< String >();
        gatewaySettingsData = new GatewaySettingsData();
    }

    public Slot[] getSlots ()
    {
        return slots;
    }

    public void setSlots ( Slot[] slots )
    {
        this.slots = slots;
    }

    public LightDataDeviceList getLightDataDeviceList ()
    {
        return lightDataDeviceList;
    }

    public void setLightDataDeviceList ( LightDataDeviceList lightDataDeviceList )
    {
        this.lightDataDeviceList = lightDataDeviceList;
    }

    public ArrayList< String > getSendSocketFireCommands ()
    {
        return SendSocketFireCommands;
    }

    public void setSendSocketFireCommands ( ArrayList< String > sendSocketFireCommands )
    {
        SendSocketFireCommands = sendSocketFireCommands;
    }

    public GatewaySettingsData getGatewaySettingsData ()
    {
        return gatewaySettingsData;
    }

    public void setGatewaySettingsData ( GatewaySettingsData gatewaySettingsData )
    {
        this.gatewaySettingsData = gatewaySettingsData;
    }

    // -------------------------------------------------------------------------------------------------
    public void createLoadSlotTypeSocketCommands ( int slot )
    {
        System.out.println( "---[createLoadSlotTypeSocketCommands" + " Slot: " + slot + "]---" );

        SendSocketFireCommands = new ArrayList< String >();
        SendSocketFireCommands.add( "light get type 0" + slot );
        SendSocketFireCommands.add( "light get name 0" + slot );

        for ( int i = 0; i < SendSocketFireCommands.size(); i++ )
        {
            System.out.println( i + 1 + ":" + SendSocketFireCommands.get( i ) );
        }
    }

    public void buildSlotData ( int slot, String data )
    {

        SendSocketFireCommands = new ArrayList< String >();

        slots[slot].setSlotNr( slot );
        slots[slot].setType( data );

        try
        {

            if ( !( slots[slot].getType()
                               .equals( "-1" ) ) )
            {

                slots[slot].setActive( true );

                slots[slot].setProductType( lightDataDeviceList.getLightDataList()
                                                               .get( Integer.parseInt( slots[slot].getType() ) - 1 )
                                                               .getTyp() );
                slots[slot].setChannelCount( ( "" +
                                               lightDataDeviceList.getLightDataList()
                                                                  .get( Integer.parseInt( slots[slot].getType() ) - 1 )
                                                                  .getAnzahlChannel() ) );
                slots[slot].setLightProduct( ( "" +
                                               lightDataDeviceList.getLightDataList()
                                                                  .get( Integer.parseInt( slots[slot].getType() ) - 1 )
                                                                  .getBezeichnung() ) );
                slots[slot].setChannelsConnecting( lightDataDeviceList.getLightDataList()
                                                                      .get( Integer.parseInt( slots[slot].getType() ) - 1 )
                                                                      .getChannelNames() );

                for ( int i = 0; i < 5; i++ )
                {
                    if ( !( slots[slot].getChannelsConnecting()[i].equals( "-" ) ) )
                    {
                        SendSocketFireCommands.add( "channel get graph " + slots[slot].getChannelsID()[i] );
                    }
                }

                for ( int i = 0; i < 5; i++ )
                {
                    if ( !( slots[slot].getChannelsConnecting()[i].equals( "-" ) ) )
                    {
                        SendSocketFireCommands.add( "channel get light " + slots[slot].getChannelsID()[i] );
                    }
                }

                ArrayList< String > connectingListTemp = new ArrayList< String >();

                for ( int i = 0; i < 5; i++ )
                {
                    if ( !( slots[slot].getChannelsConnecting()[i].equals( "-" ) ) )
                    {
                        if ( !( slots[slot].getChannelsConnecting()[i].equals( "R" ) ) )
                        {
                            connectingListTemp.add( slots[slot].getChannelsConnecting()[i] + ";" + i );
                        }
                        else
                        {
                            i++;
                            i++;
                            connectingListTemp.add( "Color Channel" + ";" + ( i - 2 ) + ";" + ( i - 1 ) + ";" + i );
                        }
                    }
                }
                slots[slot].setChannelsActiveNamesList( connectingListTemp );
            }
            else
            {
                slots[slot].setActive( false );
            }
        }
        catch ( Exception e )
        {
            slots[slot].setActive( false );
        }
    }

    // -------------------------------------------------------------------------------------------------
    public void buildSlotData ( int slot,

                                ArrayList< String > incommingSocketDataList )
    {

        SendSocketFireCommands = new ArrayList< String >();

        slots[slot].setSlotNr( slot );
        slots[slot].setType( incommingSocketDataList.get( 0 ) );
        slots[slot].setName( incommingSocketDataList.get( 1 ) );

        try
        {

            if ( !( slots[slot].getType()
                               .equals( "-1" ) ) )
            {

                slots[slot].setActive( true );

                slots[slot].setProductType( lightDataDeviceList.getLightDataList()
                                                               .get( Integer.parseInt( slots[slot].getType() ) - 1 )
                                                               .getTyp() );
                slots[slot].setChannelCount( ( "" +
                                               lightDataDeviceList.getLightDataList()
                                                                  .get( Integer.parseInt( slots[slot].getType() ) - 1 )
                                                                  .getAnzahlChannel() ) );
                slots[slot].setLightProduct( ( "" +
                                               lightDataDeviceList.getLightDataList()
                                                                  .get( Integer.parseInt( slots[slot].getType() ) - 1 )
                                                                  .getBezeichnung() ) );
                slots[slot].setChannelsConnecting( lightDataDeviceList.getLightDataList()
                                                                      .get( Integer.parseInt( slots[slot].getType() ) - 1 )
                                                                      .getChannelNames() );

                for ( int i = 0; i < 5; i++ )
                {
                    if ( !( slots[slot].getChannelsConnecting()[i].equals( "-" ) ) )
                    {
                        SendSocketFireCommands.add( "channel get graph " + slots[slot].getChannelsID()[i] );
                    }
                }

                for ( int i = 0; i < 5; i++ )
                {
                    if ( !( slots[slot].getChannelsConnecting()[i].equals( "-" ) ) )
                    {
                        SendSocketFireCommands.add( "channel get light " + slots[slot].getChannelsID()[i] );
                    }
                    else
                    {

                    }
                }

                ArrayList< String > connectingListTemp = new ArrayList< String >();

                for ( int i = 0; i < 5; i++ )
                {
                    if ( !( slots[slot].getChannelsConnecting()[i].equals( "-" ) ) )
                    {
                        if ( !( slots[slot].getChannelsConnecting()[i].equals( "R" ) ) )
                        {
                            connectingListTemp.add( slots[slot].getChannelsConnecting()[i] + ";" + i );
                        }
                        else
                        {
                            i++;
                            i++;
                            connectingListTemp.add( "Color Channel" + ";" + ( i - 2 ) + ";" + ( i - 1 ) + ";" + i );
                        }
                    }
                }
                slots[slot].setChannelsActiveNamesList( connectingListTemp );

                Log.d( "MARCEL", "---[buildSlotData]---" );
                Log.d( "MARCEL", "Type: " + slots[slot].getType() );
                Log.d( "MARCEL", "Name: " + slots[slot].getName() );
                Log.d( "MARCEL", "ProductType: " + slots[slot].getProductType() );
                Log.d( "MARCEL", "ChannelCount: " + slots[slot].getChannelCount() );
                Log.d( "MARCEL", "ProductName: " + slots[slot].getLightProduct() );
                Log.d( "MARCEL", "Channel Names: " + Arrays.toString( slots[slot].getChannelsConnecting() ) );
                Log.d( "MARCEL", "ChannelList Names: " );
                for ( int i = 0;
                      i <
                      slots[slot].getChannelsActiveNamesList()
                                 .size();
                      i++ )
                {
                    Log.d( "MARCEL",
                           slots[slot].getChannelsActiveNamesList()
                                      .get( i ) + ", " );
                }
                Log.d( "MARCEL", "" );

                Log.d( "MARCEL", "---SendSocketCommands---" );
                for ( int i = 0; i < SendSocketFireCommands.size(); i++ )
                {
                    Log.d( "MARCEL", SendSocketFireCommands.get( i ) );
                }
            }
            else
            {
                slots[slot].setActive( false );
            }
        }
        catch ( Exception e )
        {
            slots[slot].setActive( false );
        }
    }

    // -------------------------------------------------------------------------------------------------
    public void updateSlotData ( int slot,

                                 ArrayList< String > incommingSocketDataList )
    {

        try
        {

            Log.d( "MARCEL", "---[updateSlotData]---" );
            ArrayList< String > incommingSocketDataListTemp = new ArrayList< String >();
            incommingSocketDataListTemp.addAll( incommingSocketDataList );

            if ( slots[slot].isActive() )
            {

                for ( int i = 0; i < 5; i++ )
                {
                    if ( !( slots[slot].getChannelsConnecting()[i].equals( "-" ) ) )
                    {
                        slots[slot].setChannelsGraphData( incommingSocketDataListTemp.get( 0 ), i );
                        incommingSocketDataListTemp.remove( 0 );
                        Log.d( "MARCEL", slots[slot].getChannelsGraphData()[i].toString() );
                    }
                }

                Log.d( "MARCEL", "[Light Data]" );

                for ( int i = 0; i < 5; i++ )
                {
                    if ( !( slots[slot].getChannelsConnecting()[i].equals( "-" ) ) )
                    {
                        slots[slot].setChannelsLightPower( incommingSocketDataListTemp.get( 0 ), i );
                        incommingSocketDataListTemp.remove( 0 );
                        Log.d( "MARCEL", "Light " + i + ": " + slots[slot].getChannelsLightPower()[i] );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            // TODO: handle exception
        }
    }

    public ArrayList< String > buildGetLightColorSocketCommand ( int slot )
    {
        ArrayList< String > socketCommmands = new ArrayList< String >();

        Log.d( "MARCEL", "---buildGetLightColorSocketCommand---" );
        socketCommmands.add( "light get color 0" + slot );
        Log.d( "MARCEL", socketCommmands.get( 0 ) );

        return socketCommmands;
    }

    public void updateRGBColorData ( int slot, String rgbData )
    {
        String rgbColor = rgbData;
        String[] rgbColorSplit = rgbColor.split( " " );
        String[] rgbColorSplitSortTemp = rgbColor.split( " " );

        slots[slot].setRGBColor( rgbColor );
        slots[slot].getRGBSplit()[0] = Integer.parseInt( rgbColorSplit[0] );
        slots[slot].getRGBSplit()[1] = Integer.parseInt( rgbColorSplit[1] );
        slots[slot].getRGBSplit()[2] = Integer.parseInt( rgbColorSplit[2] );
        Arrays.sort( rgbColorSplitSortTemp );
        slots[slot].setRGBMaxValue( Integer.parseInt( rgbColorSplitSortTemp[rgbColorSplitSortTemp.length - 1] ) );

        Log.d( "MARCEL", "---updateRGBColorData---" );
        Log.d( "MARCEL", "RGBColor: " + slots[slot].getRGBColor() );
        Log.d( "MARCEL", "RGBColor_Split: " + Arrays.toString( slots[slot].getRGBSplit() ) );
        Log.d( "MARCEL", "RGBColor_SplitSortTemp: " + Arrays.toString( rgbColorSplitSortTemp ) );
        Log.d( "MARCEL", "RGBColor_MAX_VALUE: " + slots[slot].getRGBMaxValue() );
    }

    public String fillnumbers ( int number )
    {
        String output = "" + number;

        for ( int i = 0; i < 4 - output.length(); i++ )
        {
            output = "0" + output;
        }

		if ( number >= 100 )
		{
			output = "100";
		}

        return output;
    }
}
