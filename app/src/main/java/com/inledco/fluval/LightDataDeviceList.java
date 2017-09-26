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

public class LightDataDeviceList
{
    private ArrayList< LightData > lightDataList = new ArrayList< LightData >();

    public LightDataDeviceList ( boolean isGerman )
    {

        String hardcodedListEng[] = { "SunStrip 35 Fresh;Leuchte;01;D;Light Channel;R;G;B;-",
                                      "SunStrip 35 Reptile;Leuchte;02;G;Light Channel;R;G;B;UVA",
                                      "SunStrip 70 Fresh;Leuchte;03;D;Light Channel;R;G;B;-",
                                      "SunStrip 70 Marine;Leuchte;04;E;Light Channel;R;G;B;Light Channel Blue",
                                      "SunStrip 160 Marine;Leuchte;05;E;Light Channel;R;G;B;Light Channel Blue",
                                      "SunStrip 20 Day;Leuchte;06;A;Light Channel;-;-;-;-",
                                      "SunStrip 20 Moon;Leuchte;07;A;Light Channel;-;-;-;-",
                                      "UnderWater Tube Day;Leuchte;08;A;Light Channel;-;-;-;-",
                                      "UnderWater Tube RGB;Leuchte;09;C;R;G;B;-;-",
                                      "UnderWater Tube Moon;Leuchte;10;A;Light Channel;-;-;-;-",
                                      "Heatstrip;Heizung;11;F;Ein/Aus;-;-;-;-",
                                      "Hagen 1 CH Typ;Leuchte;12;A;Ch 1 Daylight;-;-;-;-",
                                      "Hagen 2 CH Typ;Leuchte;13;B;Ch 1 Daylight;Ch 2 Night /Blue light;-;-;-" };

        String hardcodedListGer[] = { "SunStrip 35 Fresh;Leuchte;01;D;Leucht Kanal;R;G;B;-",
                                      "SunStrip 35 Reptile;Leuchte;02;G;Leucht Kanal;R;G;B;UVA",
                                      "SunStrip 70 Fresh;Leuchte;03;D;Leucht Kanal;R;G;B;-",
                                      "SunStrip 70 Marine;Leuchte;04;E;Leucht Kanal;R;G;B;Light Channel Blue",
                                      "SunStrip 160 Marine;Leuchte;05;E;Leucht Kanal;R;G;B;Light Channel Blue",
                                      "SunStrip 20 Day;Leuchte;06;A;Leucht Kanal;-;-;-;-",
                                      "SunStrip 20 Moon;Leuchte;07;A;Leucht Kanal;-;-;-;-",
                                      "UnderWater Tube Day;Leuchte;08;A;Leucht Kanal;-;-;-;-",
                                      "UnderWater Tube RGB;Leuchte;09;C;R;G;B;-;-",
                                      "UnderWater Tube Moon;Leuchte;10;A;Leucht Kanal;-;-;-;-",
                                      "Heatstrip;Heizung;11;F;Ein/Aus;-;-;-;-",
                                      "Hagen 1 CH Typ;Leuchte;12;A;Ch 1 Tageslicht;-;-;-;-",
                                      "Hagen 2 CH Typ;Leuchte;13;B;Ch 2 Nachtlicht/blaues Licht;;-;-;-" };

        if ( isGerman )
        {
            for ( int i = 0; i < hardcodedListGer.length; i++ )
            {
                lightDataList.add( parseCsvtoLightData( hardcodedListGer[i] ) );
            }
        }
        else
        {
            for ( int i = 0; i < hardcodedListEng.length; i++ )
            {
                lightDataList.add( parseCsvtoLightData( hardcodedListEng[i] ) );
            }
        }
    }

    private LightData parseCsvtoLightData ( String data )
    {
        LightData lightData = new LightData();
        String TypWert[][] = { { "A", "1" }, { "B", "2" }, { "C", "3" }, { "D", "4" }, { "E", "5" }, { "F", "1" }, { "G", "5" } };

        String[] splitData = data.split( ";" );
        lightData.setBezeichnung( splitData[0] );
        lightData.setTyp( splitData[1] );
        lightData.setAppIdentity( splitData[2] );
        lightData.setGerateklasse( splitData[3] );

        for ( int i = 0; i < TypWert.length; i++ )
        {
            if ( splitData[3].equals( TypWert[i][0] ) )
            {
                lightData.setAnzahlChannel( Integer.parseInt( TypWert[i][1] ) );
                break;
            }
        }

        //Channel Namene werden ausgelesen somit kann man sp�ter festellen ob welche Belegt sind oder nicht;

        String[] channelNames = new String[5];
        for ( int i = 0; i < channelNames.length; i++ )
        {
            channelNames[i] = splitData[4 + i];
        }
        lightData.setChannelNames( channelNames );

        //		System.out.print(
        //				"\n-------CREATE DEVICE LIST-------: \n" + " Bezeichnung: "
        //						+ lightData.getBezeichnung() + "\n Typ: "
        //						+ lightData.getTyp() + "\n Ger�teklasse: "
        //						+ lightData.getGer�teklasse() + "\n Channel Anzahl: "
        //						+ lightData.getAnzahlChannel());
        //
        //		System.out.print("\n Channel Names: ");
        //		for (int i = 0; i < channelNames.length; i++) {
        //			System.out.print(lightData.getChannelNames()[i] + " ,");
        //		}
        //		System.out.println();
        return lightData;
    }

    public ArrayList< LightData > getLightDataList ()
    {
        return lightDataList;
    }
}
