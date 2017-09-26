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

public class LightData
{
    private String bezeichnung;
    private String typ;
    private String appIdentity;
    private String gerateklasse;
    private String kanalbezeichnung;
    private int anzahlChannel;
    private String[] channelNames;

    public LightData ()
    {

    }

    public LightData ( String bezeichnung, String typ, String appIdentity, String gerateklasse, String kanalbezeichnung, int anzahlChannel, int channelNames )
    {
        this.bezeichnung = bezeichnung;
        this.typ = typ;
        this.appIdentity = appIdentity;
        this.gerateklasse = gerateklasse;
        this.anzahlChannel = anzahlChannel;
        this.channelNames = this.channelNames;
    }

    public String getBezeichnung ()
    {
        return bezeichnung;
    }

    public void setBezeichnung ( String bezeichnung )
    {
        this.bezeichnung = bezeichnung;
    }

    public String getTyp ()
    {
        return typ;
    }

    public void setTyp ( String typ )
    {
        this.typ = typ;
    }

    public String getGerateklasse ()
    {
        return gerateklasse;
    }

    public void setGerateklasse ( String gerateklasse )
    {
        this.gerateklasse = gerateklasse;
    }

    public int getAnzahlChannel ()
    {
        return anzahlChannel;
    }

    public void setAnzahlChannel ( int anzahlChannel )
    {
        this.anzahlChannel = anzahlChannel;
    }

    public String getAppIdentity ()
    {
        return appIdentity;
    }

    public void setAppIdentity ( String appIdentity )
    {
        this.appIdentity = appIdentity;
    }

    public String[] getChannelNames ()
    {
        return channelNames;
    }

    public void setChannelNames ( String[] channelNames )
    {
        this.channelNames = channelNames;
    }
}
