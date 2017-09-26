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
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

public class FragmentChannelMain extends Fragment implements InterfaceSendFromActivity, OnClickListener
{
    private static final String TAG = "FragmentChannelMain";
    private InterfaceSendFromFragment iSendData;
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private String slot;
    private String gatewayLoaderLoadData;
    private boolean isColorChannel = false;
    private String[] splitData;
    private GatewayLoader gatewayLoader;
    private boolean isGerman;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private ImageView btnBackToChannelChoose, btnGotoDirectLightControl;
    private TextView textLightChannelMainLogo, textSunriseTimeInfo, textBreakTimeInfo, textSunsetTimeInfo, textNightTimeInfo;
    private RelativeLayout btnSunrise, btnBreak, btnSunset;
    private ImageView btnBackToChooseChannel, btnDirectControlOrColorPicker;
    private LinearLayout chart;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public FragmentChannelMain ()
    {
    }

//    public FragmentChannelMain ( String slot, String gatewayLoaderLoadData )
//    {
//        this.slot = slot;
//        this.gatewayLoaderLoadData = gatewayLoaderLoadData;
//        this.splitData = gatewayLoaderLoadData.split( ";" );
//
//        if ( splitData[0].equals( "Color Channel" ) )
//        {
//            isColorChannel = true;
//        }
//    }

    public static FragmentChannelMain newInstance ( String slot, String gatewayLoaderLoadData )
    {
        FragmentChannelMain frag = new FragmentChannelMain();
        Bundle bundle = new Bundle();
        bundle.putString( "slot", slot );
        bundle.putString( "gateway", gatewayLoaderLoadData );
        frag.setArguments( bundle );
        return frag;
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_channel_main, container, false );

        btnBackToChannelChoose = (ImageView) root.findViewById( R.id.imageViewBackToChannelMain );
        btnBackToChannelChoose.setOnClickListener( this );
        btnDirectControlOrColorPicker = (ImageView) root.findViewById( R.id.imageViewDirectControlOrColorPicker );
        btnDirectControlOrColorPicker.setOnClickListener( this );
        textLightChannelMainLogo = (TextView) root.findViewById( R.id.textViewChannelTimeSettingsMainLogo );
        textSunriseTimeInfo = (TextView) root.findViewById( R.id.TextViewSunriseTimeInfo );
        textBreakTimeInfo = (TextView) root.findViewById( R.id.TextViewBreakTimeInfo );
        textSunsetTimeInfo = (TextView) root.findViewById( R.id.TextViewSunsetTimeInfo );
        textNightTimeInfo = (TextView) root.findViewById( R.id.TextViewNightTimeInfo );
        chart = (LinearLayout) root.findViewById( R.id.Chart );

        btnSunrise = (RelativeLayout) root.findViewById( R.id.btnSunrise );
        btnSunrise.setOnClickListener( this );
        btnBreak = (RelativeLayout) root.findViewById( R.id.btnBreak );
        btnBreak.setOnClickListener( this );
        btnSunset = (RelativeLayout) root.findViewById( R.id.btnSunset );
        btnSunset.setOnClickListener( this );

        Bundle bundle = getArguments();
        slot = bundle.getString( "slot" );
        gatewayLoaderLoadData = bundle.getString( "gateway" );
        this.splitData = gatewayLoaderLoadData.split( ";" );
        if ( splitData[0].equals( "Color Channel" ) )
        {
            isColorChannel = true;
        }

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
        changeGuiDirectControlOrColorPicker();
        updateTimeGui( Integer.parseInt( slot ), Integer.parseInt( splitData[1] ) );
        drawNormalChart( Integer.parseInt( slot ), Integer.parseInt( splitData[1] ) );
    }

    //Hier kommen die Daten der MainActivity an
    @Override
    public void sendActivityData ( ArrayList< String > data, String command )
    {
        // TODO Auto-generated method stub

    }

    private void changeGuiDirectControlOrColorPicker ()
    {
        if ( isColorChannel )
        {
            btnDirectControlOrColorPicker.setImageDrawable( getResources().getDrawable( R.drawable.color_picker_icon ) );
        }
        else
        {
            if ( gatewayLoader.getGatewaySettingsData()
                              .getActiveMode()
                              .equals( "M" ) )
            {
                btnDirectControlOrColorPicker.setImageDrawable( getResources().getDrawable( R.drawable.hand ) );
            }
            else
            {
                btnDirectControlOrColorPicker.setVisibility( ImageView.GONE );
            }
        }
    }

    private void updateTimeGui ( int slot, int graphPointer )
    {

        textLightChannelMainLogo.setText( splitData[0] );
        textSunsetTimeInfo.setText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].createRealTimeText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].getTimeSunsetStart(),
                                                                                                                            isGerman ) +
                                    "-" +
                                    gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].createRealTimeText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].getTimeSunsetStop(),
                                                                                                                            isGerman ) );
        textSunriseTimeInfo.setText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].createRealTimeText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].getTimeSunriseStart(),
                                                                                                                             isGerman ) +
                                     "-" +
                                     gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].createRealTimeText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].getTimeSunriseStop(),
                                                                                                                             isGerman ) );
        textBreakTimeInfo.setText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].createRealTimeText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].getTimeBreakStart(),
                                                                                                                           isGerman ) +
                                   "-" +
                                   gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].createRealTimeText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].getTimeBreakStop(),
                                                                                                                           isGerman ) );
        textNightTimeInfo.setText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].createRealTimeText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].getTimeSunsetStop(),
                                                                                                                           isGerman ) +
                                   "-" +
                                   gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].createRealTimeText( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].getTimeSunriseStart(),
                                                                                                                           isGerman ) );
    }

    private String rgbGraphDataValueCalc ( String graphValue, String RgbMaxValue )
    {

        Double rgbP = Double.parseDouble( RgbMaxValue ) * 100 / 255;
        Double graphDataValue = ( rgbP / 100 ) * Double.parseDouble( graphValue );
        int result = (int) Math.round( graphDataValue );
        return fillnumbers( result );
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

    private void drawNormalChart ( int slot, int graphPointer )
    {
        GraphData graphData = new GraphData();
        graphData.loadGraph( gatewayLoader.getSlots()[slot].getChannelsGraphData()[graphPointer].generateGraph() );

        //Patchen der GraphData f�r den RGB Channel mit den Daten des ColorPickers
        if ( isColorChannel )
        {
            graphData.setValueSunriseStart( rgbGraphDataValueCalc( graphData.getValueSunriseStart(), "" + gatewayLoader.getSlots()[slot].getRGBMaxValue() ) );
            graphData.setValueSunriseStop( rgbGraphDataValueCalc( graphData.getValueSunriseStop(), "" + gatewayLoader.getSlots()[slot].getRGBMaxValue() ) );

            graphData.setValueBreakStart( rgbGraphDataValueCalc( graphData.getValueBreakStart(), "" + gatewayLoader.getSlots()[slot].getRGBMaxValue() ) );
            graphData.setValueBreakStop( rgbGraphDataValueCalc( graphData.getValueBreakStop(), "" + gatewayLoader.getSlots()[slot].getRGBMaxValue() ) );

            graphData.setValueSunsetStart( rgbGraphDataValueCalc( graphData.getValueSunsetStart(), "" + gatewayLoader.getSlots()[slot].getRGBMaxValue() ) );
            graphData.setValueSunsetStop( rgbGraphDataValueCalc( graphData.getValueSunsetStop(), "" + gatewayLoader.getSlots()[slot].getRGBMaxValue() ) );
        }

        XYSeries seriesStart = new XYSeries( "" );
        XYSeries seriesSunrise = new XYSeries( "Sunrise" );
        XYSeries seriesBreak = new XYSeries( "Break" );
        XYSeries seriesIdle1 = new XYSeries( "" );
        XYSeries seriesSunset = new XYSeries( "Sunset" );
        XYSeries seriesIdle2 = new XYSeries( "" );
        XYSeries seriesEnd = new XYSeries( "" );

        XYSeriesRenderer rendererSunrise = new XYSeriesRenderer();
        rendererSunrise.setLineWidth( 5 );
        rendererSunrise.setColor( Color.rgb( 0, 96, 128 ) );
        rendererSunrise.setPointStrokeWidth( 6 );

        XYSeriesRenderer rendererBreak = new XYSeriesRenderer();
        rendererBreak.setLineWidth( 5 );
        rendererBreak.setColor( Color.rgb( 255, 255, 0 ) );
        rendererBreak.setPointStrokeWidth( 6 );

        XYSeriesRenderer rendererIdle1 = new XYSeriesRenderer();
        rendererIdle1.setLineWidth( 5 );
        rendererIdle1.setColor( Color.rgb( 160, 32, 240 ) );
        rendererIdle1.setPointStrokeWidth( 6 );

        XYSeriesRenderer rendererSunset = new XYSeriesRenderer();
        rendererSunset.setLineWidth( 5 );
        rendererSunset.setColor( Color.rgb( 255, 0, 0 ) );
        rendererSunset.setPointStrokeWidth( 6 );

        XYSeriesRenderer rendererIdle2 = new XYSeriesRenderer();
        rendererIdle2.setLineWidth( 5 );
        rendererIdle2.setColor( Color.rgb( 160, 32, 240 ) );
        rendererIdle2.setPointStrokeWidth( 6 );

        XYSeriesRenderer rendererStart = new XYSeriesRenderer();
        rendererStart.setLineWidth( 5 );
        rendererStart.setColor( Color.rgb( 0, 80, 255 ) );
        rendererStart.setPointStrokeWidth( 6 );

        XYSeriesRenderer rendererEnd = new XYSeriesRenderer();
        rendererEnd.setLineWidth( 5 );
        rendererEnd.setColor( Color.rgb( 0, 80, 255 ) );
        rendererEnd.setPointStrokeWidth( 6 );

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer( rendererStart );
        mRenderer.addSeriesRenderer( rendererSunrise );
        mRenderer.addSeriesRenderer( rendererIdle2 );
        mRenderer.addSeriesRenderer( rendererBreak );
        mRenderer.addSeriesRenderer( rendererIdle1 );
        mRenderer.addSeriesRenderer( rendererSunset );
        mRenderer.addSeriesRenderer( rendererEnd );

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        dataset.addSeries( seriesStart );
        dataset.addSeries( seriesSunrise );
        dataset.addSeries( seriesIdle2 );
        dataset.addSeries( seriesBreak );
        dataset.addSeries( seriesIdle1 );
        dataset.addSeries( seriesSunset );
        dataset.addSeries( seriesEnd );

        mRenderer.setMarginsColor( Color.argb( 0x00, 0xff, 0x00, 0x00 ) );
        mRenderer.setPanEnabled( false, false );
        mRenderer.setYAxisMax( 100 );
        mRenderer.setYAxisMin( 0 );
        mRenderer.setXAxisMax( 23 );
        mRenderer.setXAxisMin( 0 );
        mRenderer.setShowGrid( false );
        mRenderer.setZoomEnabled( false, false );
        mRenderer.setXLabels( 24 );

        GraphicalView chartView = ChartFactory.getLineChartView( getActivity(), dataset, mRenderer );

        //Line f�r Line wird der Graph gezeichnet dabei werden die Werte aus der GraphData Klasse benutzt
        seriesStart.add( 0, Integer.parseInt( graphData.getValueSunriseStart() ) );
        seriesStart.add( setDiagramTime( graphData.getHour( graphData.getTimeSunriseStart() ), graphData.getMin( graphData.getTimeSunriseStart() ) ),
                         Integer.parseInt( graphData.getValueSunriseStart() ) );
        seriesSunrise.add( setDiagramTime( graphData.getHour( graphData.getTimeSunriseStart() ), graphData.getMin( graphData.getTimeSunriseStart() ) ),
                           Integer.parseInt( graphData.getValueSunriseStart() ) );
        seriesSunrise.add( setDiagramTime( graphData.getHour( graphData.getTimeSunriseStop() ), graphData.getMin( graphData.getTimeSunriseStop() ) ),
                           Integer.parseInt( graphData.getValueSunriseStop() ) );
        seriesIdle2.add( setDiagramTime( graphData.getHour( graphData.getTimeSunriseStop() ), graphData.getMin( graphData.getTimeSunriseStop() ) ),
                         Integer.parseInt( graphData.getValueSunriseStop() ) );
        seriesIdle2.add( setDiagramTime( graphData.getHour( graphData.getTimeBreakStart() ), graphData.getMin( graphData.getTimeBreakStart() ) ),
                         Integer.parseInt( graphData.getValueSunriseStop() ) );
        seriesBreak.add( setDiagramTime( graphData.getHour( graphData.getTimeBreakStart() ), graphData.getMin( graphData.getTimeBreakStart() ) ),
                         Integer.parseInt( graphData.getValueSunriseStop() ) );
        seriesBreak.add( setDiagramTime( graphData.getHour( graphData.getTimeBreakStart() ), graphData.getMin( graphData.getTimeBreakStart() ) ),
                         Integer.parseInt( graphData.getValueBreakStop() ) );
        seriesBreak.add( setDiagramTime( graphData.getHour( graphData.getTimeBreakStop() ), graphData.getMin( graphData.getTimeBreakStop() ) ),
                         Integer.parseInt( graphData.getValueBreakStop() ) );
        seriesBreak.add( setDiagramTime( graphData.getHour( graphData.getTimeBreakStop() ), graphData.getMin( graphData.getTimeBreakStop() ) ),
                         Integer.parseInt( graphData.getValueSunriseStop() ) );
        seriesIdle1.add( setDiagramTime( graphData.getHour( graphData.getTimeBreakStop() ), graphData.getMin( graphData.getTimeBreakStop() ) ),
                         Integer.parseInt( graphData.getValueSunriseStop() ) );
        seriesIdle1.add( setDiagramTime( graphData.getHour( graphData.getTimeSunsetStart() ), graphData.getMin( graphData.getTimeSunsetStart() ) ),
                         Integer.parseInt( graphData.getValueSunriseStop() ) );
        seriesSunset.add( setDiagramTime( graphData.getHour( graphData.getTimeSunsetStart() ), graphData.getMin( graphData.getTimeSunsetStart() ) ),
                          Integer.parseInt( graphData.getValueSunriseStop() ) );
        seriesSunset.add( setDiagramTime( graphData.getHour( graphData.getTimeSunsetStop() ), graphData.getMin( graphData.getTimeSunsetStop() ) ),
                          Integer.parseInt( graphData.getValueSunsetStop() ) );
        seriesEnd.add( setDiagramTime( graphData.getHour( graphData.getTimeSunsetStop() ), graphData.getMin( graphData.getTimeSunsetStop() ) ),
                       Integer.parseInt( graphData.getValueSunsetStop() ) );
        seriesEnd.add( 24, Integer.parseInt( graphData.getValueSunsetStop() ) );
        chart.addView( chartView, 0 );
    }

    //----------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick ( View button )
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch ( button.getId() )
        {
            case R.id.btnSunrise:
                //			ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
                ft.replace( R.id.fragment_root,
                            FragmentChannelTimeSettings.newInstance( "Sunrise", slot, gatewayLoaderLoadData, isColorChannel ),
                            "channel_time_settings" );
                ft.addToBackStack( null );
                ft.commit();
                break;
            case R.id.btnBreak:
                //			ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
                ft.replace( R.id.fragment_root,
                            FragmentChannelTimeSettings.newInstance( "Break", slot, gatewayLoaderLoadData, isColorChannel ),
                            "channel_time_settings" );
                ft.addToBackStack( null );
                ft.commit();
                break;
            case R.id.btnSunset:
                //			ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
                ft.replace( R.id.fragment_root,
                            FragmentChannelTimeSettings.newInstance( "Sunset", slot, gatewayLoaderLoadData, isColorChannel ),
                            "channel_time_settings" );
                ft.addToBackStack( null );
                ft.commit();
                break;
            case R.id.imageViewBackToChannelMain:
                fm.popBackStack();
                break;
            case R.id.imageViewDirectControlOrColorPicker:
                //			ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
                if ( isColorChannel )
                {
                    ft.replace( R.id.fragment_root, FragmentColorPicker.newInstance( slot, splitData ), "FragmentColorPicker" );
                    ft.addToBackStack( null );
                    ft.commit();
                }
                else
                {
                    ft.replace( R.id.fragment_root, FragmentDirectLightControl.newInstance( slot, splitData[1] ), "FragmentDirectLightControl" );
                    ft.addToBackStack( null );
                    ft.commit();
                }

                break;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------
    //Umrechnung von den Stunden ins Graphen Time Format
    public double setDiagramTime ( String hh, String mm )
    {
        int hour = Integer.parseInt( hh );
        int min = Integer.parseInt( mm );

        return hour + ( ( ( 100.0 / 60 ) * min ) / 100 );
    }
}
