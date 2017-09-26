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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.inledco.fluval.ColorPickerView.OnColorChangedListener;

import java.util.ArrayList;

public class FragmentColorPicker extends Fragment implements InterfaceSendFromActivity, OnClickListener, OnColorChangedListener
{

    private InterfaceSendFromFragment iSendData;
    private ColorPickerView colorPicker;

    private Button btnCancel, btnSetColor;
    private ImageView btnBackToChannelMain;

    private GatewayLoader gatewayLoader;

    private String slot;
    private String[] channels;

    public FragmentColorPicker ()
    {
    }

    public static FragmentColorPicker newInstance ( String slot, String[] channels )
    {
        FragmentColorPicker frag = new FragmentColorPicker();
        Bundle bundle = new Bundle();
        bundle.putString( "slot", slot );
        bundle.putStringArray( "channels", channels );
        frag.setArguments( bundle );
        return frag;
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_color_picker, container, false );
        colorPicker = (ColorPickerView) root.findViewById( R.id.colorPickerView );
        colorPicker.setOnColorChangedListener( this );

        btnCancel = (Button) root.findViewById( R.id.buttonColorPickerCancel );
        btnSetColor = (Button) root.findViewById( R.id.buttonColorPickerSet );
        btnBackToChannelMain = (ImageView) root.findViewById( R.id.imageViewBackToChannelMain );

        btnCancel.setOnClickListener( this );
        btnSetColor.setOnClickListener( this );
        btnBackToChannelMain.setOnClickListener( this );

        Bundle bundle = getArguments();
        slot = bundle.getString( "slot" );
        channels = bundle.getStringArray( "channels" );

        return root;
    }

    @Override
    public void onActivityCreated ( Bundle savedInstanceState )
    {
        Log.d( "MARCEL", "create fragment settings" );
        iSendData = (InterfaceSendFromFragment) getActivity();
        iSendData.sendTag( getTag() ); //�ber das Interface wird der MainAktivity mitgeteilt wo im Speicher sich dieses Fragment befindet
        super.onActivityCreated( savedInstanceState );
        gatewayLoader = ( (MainActivity) getActivity() ).gatewayLoader;
        updateColorPickerGui();
    }

    private void updateColorPickerGui ()
    {
        colorPicker.setColor( Color.rgb( gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[0],
                                         gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[1],
                                         gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[2] ) );
        directRgbLightControl( Color.rgb( gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[0],
                                          gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[1],
                                          gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[2] ) );
    }

    private String createRGBData ( int color )
    {

        int b = ( color ) & 0xFF;
        int g = ( color >> 8 ) & 0xFF;
        int r = ( color >> 16 ) & 0xFF;
        int a = ( color >> 24 ) & 0xFF;

        return fillRgbNumbers( r ) + " " + fillRgbNumbers( g ) + " " + fillRgbNumbers( b );
    }

    private void directRgbLightControl ( int color )
    {
        int b = ( color ) & 0xFF;
        int g = ( color >> 8 ) & 0xFF;
        int r = ( color >> 16 ) & 0xFF;
        int a = ( color >> 24 ) & 0xFF;

        ArrayList< String > sendRGBDirectLightCommands = new ArrayList< String >();
        sendRGBDirectLightCommands.add( "channel set light " +
                                        gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( channels[1] )] +
                                        " " +
                                        rgbToProzent( r ) );
        sendRGBDirectLightCommands.add( "channel set light " +
                                        gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( channels[2] )] +
                                        " " +
                                        rgbToProzent( g ) );
        sendRGBDirectLightCommands.add( "channel set light " +
                                        gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( channels[3] )] +
                                        " " +
                                        rgbToProzent( b ) );
//        ( (MainActivity) getActivity() ).startFireSocket( sendRGBDirectLightCommands );
        ((MainActivity) getActivity()).sendDataArray( sendRGBDirectLightCommands );
    }

    public String fillRgbNumbers ( int number )
    {
        String output = "" + number;
        if ( output.length() < 3 )
        {
            for ( int i = 0; i < 4 - output.length(); i++ )
            {
                output = "0" + output;
            }
        }

        return output;
    }

    private String rgbToProzent ( int color )
    {

        Double colP = (double) ( ( color * 100 ) / 255 );
        int colInt = (int) Math.round( colP );

        return fillRgbNumbers( colInt );
    }

    //Hier kommen die Daten der MainActivity an
    @Override
    public void sendActivityData ( ArrayList< String > data, String command )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick ( View button )
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        switch ( button.getId() )
        {
            case R.id.buttonColorPickerCancel:
                directRgbLightControl( Color.rgb( gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[0],
                                                  gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[1],
                                                  gatewayLoader.getSlots()[Integer.parseInt( slot )].getRGBSplit()[2] ) );
                fm.popBackStack();
                break;

            case R.id.buttonColorPickerSet:
                ArrayList< String > sendsetColorSocketData = new ArrayList< String >();
                sendsetColorSocketData.add( "light set color " + slot + " " + createRGBData( colorPicker.getColor() ) );
                Log.d( "MARCEL", sendsetColorSocketData.get( 0 ) );
                gatewayLoader.updateRGBColorData( Integer.parseInt( slot ), createRGBData( colorPicker.getColor() ) );
//                ( (MainActivity) getActivity() ).startFireSocket( sendsetColorSocketData );
                ((MainActivity) getActivity()).sendDataArray( sendsetColorSocketData );
                ( (MainActivity) getActivity() ).print( "saved new color..." );
                fm.popBackStack();
                break;

            case R.id.imageViewBackToChannelMain:
                fm.popBackStack();
                break;
        }
    }

    @Override
    public void onColorChanged ( int newColor )
    {
        directRgbLightControl( newColor );
    }
}
