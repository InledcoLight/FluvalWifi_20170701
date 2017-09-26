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

public class GatewaySettingsData
{
    private String type;
    private String name;
    private int slots;
    private String activeMode;
    private String activeCloudMode;

    public GatewaySettingsData ()
    {
        activeCloudMode = "00";
    }

    public String getType ()
    {
        return type;
    }

    public void setType ( String type )
    {
        this.type = type;
    }

    public String getActiveMode ()
    {
        return activeMode;
    }

    public void setActiveMode ( String activeMode )
    {
        this.activeMode = activeMode;
    }

    public String getActiveCloudMode ()
    {
        return activeCloudMode;
    }

    public void setActiveCloudMode ( String activeCloudMode )
    {
        this.activeCloudMode = activeCloudMode;
    }

    public String getName ()
    {
        return name;
    }

    public void setName ( String name )
    {
        this.name = name;
    }

    public int getSlots ()
    {
        return slots;
    }

    public void setSlots ( int slots )
    {
        this.slots = slots;
    }

    public void parseGetwayGetInfo ( String data )
    {

        if ( data.length() > 2 )
        {
            setType( data.substring( 0, 3 ) );
            setName( data.substring( 6, data.length() ) );
            if ( getType().equals( "2CH" ) )
            {
                setSlots( 2 );
                setActiveMode( data.substring( 4, 5 ) );
            }
            else
            {
                if ( getType().equals( "5CH" ) )
                {
                    setSlots( 3 );
                    setActiveMode( data.substring( 4, 5 ) );
                }
            }
        }
    }

    public boolean isGatewayInfoData ( String data )
    {
        if ( data.length() > 2 )
        {
            if ( data.substring( 0, 3 )
                     .equals( "2CH" ) ||
                 data.substring( 0, 3 )
                     .equals( "5CH" ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString ()
    {
        String out = "GatewayDataInfo:\n" +
                     "Type: " +
                     getType() +
                     "\n" +
                     "Name: " +
                     getName() +
                     "\n" +
                     "Slots: " +
                     getSlots() +
                     "\n" +
                     "ActiveMode: " +
                     getActiveMode() +
                     "\n" +
                     "CloudMode: " +
                     getActiveCloudMode() +
                     "\n";

        return out;
    }
}
