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

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.ArrayList;

public class FragmentChannelTimeSettings extends Fragment implements InterfaceSendFromActivity, OnClickListener, OnSeekBarChangeListener
{
    private static final String TAG = "FragmentChannelTimeSett";
    private InterfaceSendFromFragment iSendData;

    private TextView textTimeLogo, textChannel, textStartTime, textStopTime, textChannelLightValue, textChannelDesc;
    private RelativeLayout btnsetStartTime, btnsetStopTime, btnSaveTimeSettings;
    private SeekBar seekBarChannelSetLightValue;
    private ImageView btnBackToChannelMain;

    private String dayTime;
    private String slot;
    private String gatewayLoaderLoadData;
    private boolean isGerman;
    private boolean isColorChannel;

    private String timeStart, timeStop;
    private String channel;
    private GraphData graphData;
    private GatewayLoader gatewayLoader;
    private String[] splitData;
    private String timePickerChooser;

    public FragmentChannelTimeSettings ()
    {
    }

//    public FragmentChannelTimeSettings ( String dayTime, String slot, String gatewayLoaderLoadData, boolean isColorChannel )
//    {
//        this.dayTime = dayTime;
//        this.slot = slot;
//        this.gatewayLoaderLoadData = gatewayLoaderLoadData;
//        this.isColorChannel = isColorChannel;
//
//        this.splitData = gatewayLoaderLoadData.split( ";" );
//        graphData = new GraphData();
//    }

    public static FragmentChannelTimeSettings newInstance ( String dayTime, String slot, String gatewayLoaderLoadData, boolean isColorChannel )
    {
        FragmentChannelTimeSettings frag = new FragmentChannelTimeSettings();
        Bundle bundle = new Bundle();
        bundle.putString( "daytime", dayTime );
        bundle.putString( "slot", slot );
        bundle.putString( "gateway", gatewayLoaderLoadData );
        bundle.putBoolean( "color", isColorChannel );
        frag.setArguments( bundle );
        return frag;
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_channel_time_settings, container, false );

        textTimeLogo = (TextView) root.findViewById( R.id.textViewChannelTimeSettingsMainLogo );
        textChannel = (TextView) root.findViewById( R.id.textViewChannelTimeSettingsChannel );
        textStartTime = (TextView) root.findViewById( R.id.textViewChannelTimeSettingsStartTime );
        textStopTime = (TextView) root.findViewById( R.id.textViewChannelTimeSettingsStopTime );
        textChannelDesc = (TextView) root.findViewById( R.id.textViewChannelDesc );

        textChannelLightValue = (TextView) root.findViewById( R.id.textViewChannelTimeSettingsLightValue );
        btnsetStartTime = (RelativeLayout) root.findViewById( R.id.btnChannelTimeSettingsSetStartTime );
        btnsetStartTime.setOnClickListener( this );
        btnsetStopTime = (RelativeLayout) root.findViewById( R.id.btnChannelTimeSettingsSetStopTime );
        btnsetStopTime.setOnClickListener( this );
        btnSaveTimeSettings = (RelativeLayout) root.findViewById( R.id.btnChannelTimeSettingsSaveTimeData );
        btnSaveTimeSettings.setOnClickListener( this );
        seekBarChannelSetLightValue = (SeekBar) root.findViewById( R.id.seekBarChannelTimeSettingsLightValue );
        seekBarChannelSetLightValue.setOnSeekBarChangeListener( this );
        btnBackToChannelMain = (ImageView) root.findViewById( R.id.imageViewBackToChannelMain );
        btnBackToChannelMain.setOnClickListener( this );

        Bundle bundle = getArguments();
        dayTime = bundle.getString( "daytime" );
        slot = bundle.getString( "slot" );
        gatewayLoaderLoadData = bundle.getString( "gateway" );
        isColorChannel = bundle.getBoolean( "color" );
        splitData = gatewayLoaderLoadData.split( ";" );
        graphData = new GraphData();

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
        isGerman = ( (MainActivity) getActivity() ).isGerman;
        textGuiUpdate();
    }

    private void textGuiUpdate ()
    {
        graphData.loadGraph( gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] )].generateGraph() );
        channel = gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( splitData[1] )];

        if ( dayTime.equals( "Sunrise" ) )
        {
            textTimeLogo.setText( R.string.channel_main_sunrise );
            textChannel.setText( channel );
            textChannelDesc.setText( getString( R.string.channel_time_settings_desc_sunrise ) );
            textStartTime.setText( graphData.createRealTimeText( graphData.getTimeSunriseStart(), isGerman ) );
            textStopTime.setText( graphData.createRealTimeText( graphData.getTimeSunriseStop(), isGerman ) );
            timeStart = graphData.getTimeSunriseStart();
            timeStop = graphData.getTimeSunriseStop();
            seekBarChannelSetLightValue.setProgress( Integer.parseInt( graphData.getValueSunriseStop() ) );
            textChannelLightValue.setText( seekBarChannelSetLightValue.getProgress() + "%" );
        }

        if ( dayTime.equals( "Break" ) )
        {
            textTimeLogo.setText( R.string.channel_main_break );
            textChannel.setText( channel );
            textChannelDesc.setText( getString( R.string.channel_time_settings_desc_break ) );
            textStartTime.setText( graphData.createRealTimeText( graphData.getTimeBreakStart(), isGerman ) );
            textStopTime.setText( graphData.createRealTimeText( graphData.getTimeBreakStop(), isGerman ) );
            timeStart = graphData.getTimeBreakStart();
            timeStop = graphData.getTimeBreakStop();
            seekBarChannelSetLightValue.setProgress( Integer.parseInt( graphData.getValueBreakStop() ) );
            textChannelLightValue.setText( seekBarChannelSetLightValue.getProgress() + "%" );
        }

        if ( dayTime.equals( "Sunset" ) )
        {
            textTimeLogo.setText( R.string.channel_main_sunset );
            textChannel.setText( channel );
            textChannelDesc.setText( getString( R.string.channel_time_settings_desc_sunset ) );
            textStartTime.setText( graphData.createRealTimeText( graphData.getTimeSunsetStart(), isGerman ) );
            textStopTime.setText( graphData.createRealTimeText( graphData.getTimeSunsetStop(), isGerman ) );
            timeStart = graphData.getTimeSunsetStart();
            timeStop = graphData.getTimeSunsetStop();
            seekBarChannelSetLightValue.setProgress( Integer.parseInt( graphData.getValueSunsetStop() ) );
            textChannelLightValue.setText( seekBarChannelSetLightValue.getProgress() + "%" );
        }
    }

    //Hier kommen die Daten der MainActivity an
    @Override
    public void sendActivityData ( ArrayList< String > data, String command )
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if ( command.equals( "firesocketFinish" ) )
        {
            Log.d( "MARCEL", "firesocketFinish" );
            ( (MainActivity) getActivity() ).print( "saved..." );
            fm.popBackStack();
        }

        if ( command.equals( "sniffSlotDataFinish" ) )
        {
            Log.d( "MARCEL", "sniffSlotDataFinish" );
        }
    }

    @Override
    public void onProgressChanged ( SeekBar seekBar, int progress, boolean fromUser )
    {

        textChannelLightValue.setText( seekBar.getProgress() + "%" );

        if ( dayTime.equals( "Sunrise" ) )
        {
            graphData.setValueSunriseStop( graphData.fillnumbers( seekBar.getProgress() ) );
            graphData.setValueBreakStart( graphData.fillnumbers( seekBar.getProgress() ) );
        }
        if ( dayTime.equals( "Break" ) )
        {
            graphData.setValueBreakStop( graphData.fillnumbers( seekBar.getProgress() ) );
            graphData.setValueSunsetStart( graphData.fillnumbers( seekBar.getProgress() ) );
        }
        if ( dayTime.equals( "Sunset" ) )
        {
            graphData.setValueSunriseStart( graphData.fillnumbers( seekBar.getProgress() ) );
            graphData.setValueSunsetStop( graphData.fillnumbers( seekBar.getProgress() ) );
        }
    }

    @Override
    public void onStartTrackingTouch ( SeekBar seekBar )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch ( SeekBar seekBar )
    {
        // TODO Auto-generated method stub

    }

    //Buttons vom Layout wurden angeklickt
    @Override
    public void onClick ( View button )
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        switch ( button.getId() )
        {
            case R.id.btnChannelTimeSettingsSetStartTime:
                timePickerChooser = "Start";
                showTimePickerDialog( "Start:",
                                      Integer.parseInt( graphData.getHour( timeStart ) ),
                                      Integer.parseInt( graphData.getMin( timeStart ) ),
                                      false,
                                      mOnTimeSetListener );
                break;
            case R.id.btnChannelTimeSettingsSetStopTime:
                timePickerChooser = "Stop";
                showTimePickerDialog( "Stop:",
                                      Integer.parseInt( graphData.getHour( timeStop ) ),
                                      Integer.parseInt( graphData.getMin( timeStop ) ),
                                      false,
                                      mOnTimeSetListener );
                break;
            case R.id.btnChannelTimeSettingsSaveTimeData:
                saveTimeSettings();
                break;
            case R.id.imageViewBackToChannelMain:
                fm.popBackStack();
                break;
        }
    }

    //von hier aus wird Gespeichert im Loader sowie im Gateway
    private void saveTimeSettings ()
    {

        if ( dayTime.equals( "Sunrise" ) )
        {
            if ( Integer.parseInt( graphData.getTimeSunriseStart() ) > Integer.parseInt( graphData.getTimeSunriseStop() ) )
            {
                showDialog( getString( R.string.time_validate_1 ) );
                return;
            }

            if ( Integer.parseInt( graphData.getTimeSunriseStop() ) > Integer.parseInt( graphData.getTimeBreakStart() ) )
            {
                showDialog( getString( R.string.time_validate_2 ) );
                return;
            }

            if ( Integer.parseInt( graphData.getTimeSunriseStop() ) == Integer.parseInt( graphData.getTimeSunriseStart() ) )
            {
                showDialog( getString( R.string.time_validate_10 ) );
                return;
            }

            if ( Integer.parseInt( graphData.getValueSunriseStop() ) < Integer.parseInt( graphData.getValueSunriseStart() ) )
            {
                showDialog( getString( R.string.value_validate_1 ) );
                return;
            }
        }

        if ( dayTime.equals( "Break" ) )
        {

            Log.d( "MARCEL_NEU_DEBUG", graphData.getTimeBreakStart() + "||" + graphData.getTimeBreakStop() );

            if ( Integer.parseInt( graphData.getTimeBreakStart() ) > Integer.parseInt( graphData.getTimeBreakStop() ) )
            {
                showDialog( getString( R.string.time_validate_3 ) );
                return;
            }

            if ( Integer.parseInt( graphData.getTimeBreakStart() ) == Integer.parseInt( graphData.getTimeBreakStop() ) )
            {
                showDialog( getString( R.string.time_validate_10 ) );
                return;
            }

            if ( Integer.parseInt( graphData.getTimeBreakStop() ) > Integer.parseInt( graphData.getTimeSunsetStart() ) )
            {
                showDialog( getString( R.string.time_validate_5 ) );
                return;
            }
        }

        if ( dayTime.equals( "Sunset" ) )
        {

            if ( Integer.parseInt( graphData.getTimeSunsetStart() ) > Integer.parseInt( graphData.getTimeSunsetStop() ) )
            {
                showDialog( getString( R.string.time_validate_6 ) );
                return;
            }

            if ( Integer.parseInt( graphData.getTimeSunsetStart() ) == Integer.parseInt( graphData.getTimeSunsetStop() ) )
            {
                showDialog( getString( R.string.time_validate_10 ) );
                return;
            }

            if ( Integer.parseInt( graphData.getTimeSunsetStart() ) < Integer.parseInt( graphData.getTimeBreakStop() ) )
            {
                //					 showDialog(getString(R.string.time_validate_7));
                //						return;
            }

            if ( Integer.parseInt( graphData.getTimeSunsetStop() ) > Integer.parseInt( graphData.getTimeSunriseStart() ) )
            {
                //					 showDialog(getString(R.string.time_validate_8));
                //						return;
            }

            if ( Integer.parseInt( graphData.getValueSunriseStop() ) < Integer.parseInt( graphData.getValueSunriseStart() ) )
            {
                showDialog( getString( R.string.value_validate_1 ) );
                return;
            }

            if ( Integer.parseInt( graphData.getTimeSunsetStart() ) < Integer.parseInt( graphData.getTimeSunriseStart() ) )
            {
                showDialog( getString( R.string.time_validate_11 ) );
                return;
            }
        }

        ArrayList< String > sendSaveSocketCommand = new ArrayList< String >();
        if ( isColorChannel )
        {
            //ist ein color channel
            gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] )].loadGraph( graphData.generateGraph() );
            gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] ) +
                                                                                      1].loadGraph( graphData.generateGraph() );
            gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] ) +
                                                                                      2].loadGraph( graphData.generateGraph() );
            sendSaveSocketCommand.add( "channel set graph " +
                                       gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( splitData[1] )] +
                                       " " +
                                       gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] )].generateGraph() );
            sendSaveSocketCommand.add( "channel set graph " +
                                       gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( splitData[1] ) + 1] +
                                       " " +
                                       gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] ) +
                                                                                                                 1].generateGraph() );
            sendSaveSocketCommand.add( "channel set graph " +
                                       gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( splitData[1] ) + 2] +
                                       " " +
                                       gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] ) +
                                                                                                                 2].generateGraph() );
            Log.d( "MARCEL",
                   "SAVE TIME SETTINGS: " + sendSaveSocketCommand.get( 0 ) + "\n" + sendSaveSocketCommand.get( 1 ) + "\n" + sendSaveSocketCommand.get( 2 ) );
            ( (MainActivity) getActivity() ).print( "saving..." );
//            ( (MainActivity) getActivity() ).startFireSocket( sendSaveSocketCommand );
            ((MainActivity) getActivity()).sendDataArray( sendSaveSocketCommand );
        }
        else
        {
            //ist kein color channel
            gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] )].loadGraph( graphData.generateGraph() );
            sendSaveSocketCommand.add( "channel set graph " +
                                       gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( splitData[1] )] +
                                       " " +
                                       gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsGraphData()[Integer.parseInt( splitData[1] )].generateGraph() );
//            Log.d( "MARCEL", "SAVE TIME SETTINGS: " + sendSaveSocketCommand.get( 0 ) );
//            ( (MainActivity) getActivity() ).print( "saving..." );
//            ( (MainActivity) getActivity() ).startFireSocket( sendSaveSocketCommand );
//            ClientSocket.getInstance().sendDataArray( sendSaveSocketCommand );
            ((MainActivity)getActivity()).SendSocketData( sendSaveSocketCommand.get( 0 ), new TransmitListener() {
                @Override
                public void onReceive ( String buffer )
                {
                    if ( "OK".equals( buffer ) )
                    {
                        getActivity().runOnUiThread( new Runnable() {
                            @Override
                            public void run ()
                            {
                                ( (MainActivity) getActivity() ).print( "saved..." );
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        } );
                    }
                }

                @Override
                public void onReceiveTimeout ()
                {

                }

                @Override
                public void onReceiveError ()
                {

                }
            } );
        }
    }

    private OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener()
    {

        @Override
        public void onTimeSet ( TimePicker view, int hourOfDay, int minute )
        {

            if ( timePickerChooser.equals( "Start" ) )
            {
                timeStart = timeConvert( hourOfDay, minute );
            }

            if ( timePickerChooser.equals( "Stop" ) )
            {
                timeStop = timeConvert( hourOfDay, minute );
            }

            textStartTime.setText( graphData.createRealTimeText( timeStart, isGerman ) );
            textStopTime.setText( graphData.createRealTimeText( timeStop, isGerman ) );

            if ( dayTime.equals( "Sunrise" ) )
            {
                graphData.setTimeSunriseStart( timeStart );
                graphData.setTimeSunriseStop( timeStop );
            }
            if ( dayTime.equals( "Break" ) )
            {
                graphData.setTimeBreakStart( timeStart );
                graphData.setTimeBreakStop( timeStop );
            }
            if ( dayTime.equals( "Sunset" ) )
            {
                graphData.setTimeSunsetStart( timeStart );
                graphData.setTimeSunsetStop( timeStop );
            }
        }
    };

    private TimePickerDialog showTimePickerDialog ( String title, int initialHour, int initialMinutes, boolean is24Hour, OnTimeSetListener listener )
    {
        TimePickerDialog dialog = new TimePickerDialog( getActivity(), listener, initialHour, initialMinutes, is24Hour );
        dialog.setTitle( title );
        dialog.show();

        return dialog;
    }

    private String timeConvert ( int hour, int min )
    {
        String outputTime = "";

        if ( hour < 10 )
        {
            outputTime += "0" + hour;
        }
        else
        {
            outputTime += hour;
        }

        if ( min < 10 )
        {
            outputTime += "0" + min;
        }
        else
        {
            outputTime += min;
        }

        return outputTime;
    }

    private void showDialog ( String text )
    {
        AlertDialog alertDialog = new AlertDialog.Builder( getActivity() ).create();
        alertDialog.setTitle( "Error" );
        alertDialog.setMessage( text );
        alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
        {
            public void onClick ( DialogInterface dialog, int which )
            {

                dialog.dismiss();
            }
        } );
        alertDialog.show();
    }

    private static String changeTimeHelper ( String hh, String mm, int inc_mm, int dec_mm )
    {

        Time time = new Time( Integer.parseInt( hh ), Integer.parseInt( mm ) - dec_mm, 0 );
        return time.toLocaleString()
                   .toString()
                   .substring( 11, 16 );
    }
}
