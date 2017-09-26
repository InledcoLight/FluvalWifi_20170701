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

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.inledco.fluval.CircularSeekBar.OnCircularSeekBarChangeListener;

import java.util.ArrayList;

public class FragmentDirectLightControl extends Fragment implements InterfaceSendFromActivity,
                                                                    OnTouchListener,
                                                                    OnDragListener,
                                                                    OnCircularSeekBarChangeListener,
                                                                    OnClickListener
{

    private InterfaceSendFromFragment iSendData;
    private String channelPointer;
    private String slot;
    private int lightPower;
    private GatewayLoader gatewayLoader;

    private boolean firstLoad = false;

    private TextView textChannelName, textDirectLightPower;
    private Button btnOn, btnOff;
    private CircularSeekBar circularSeekBar;
    private ImageView btnBackToChannelMain;
    private boolean canupdate = false;

    private boolean lock;
    private CountDownTimer mCountDownTimer;

    public FragmentDirectLightControl ()
    {

    }

    public static FragmentDirectLightControl newInstance ( String slot, String channelPointer )
    {
        FragmentDirectLightControl frag = new FragmentDirectLightControl();
        Bundle bundle = new Bundle();
        bundle.putString( "slot", slot );
        bundle.putString( "channelpointer", channelPointer );
        frag.setArguments( bundle );
        return frag;
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_light_direct_control, container, false );

        textChannelName = (TextView) root.findViewById( R.id.textViewChannelDesc );
        textDirectLightPower = (TextView) root.findViewById( R.id.textViewDirectLightPower );
        btnOn = (Button) root.findViewById( R.id.buttonDirectLightOn );
        btnOn.setOnClickListener( this );
        btnOff = (Button) root.findViewById( R.id.buttonDirectLightOff );
        btnOff.setOnClickListener( this );
        circularSeekBar = (CircularSeekBar) root.findViewById( R.id.circularSeekBar1 );
        circularSeekBar.setOnSeekBarChangeListener( this );

        btnBackToChannelMain = (ImageView) root.findViewById( R.id.imageViewBackToChannelMain );
        btnBackToChannelMain.setOnClickListener( this );

        Bundle bundle = getArguments();
        slot = bundle.getString( "slot" );
        channelPointer = bundle.getString( "channelpointer" );

        mCountDownTimer = new CountDownTimer( 32, 1 ) {
            @Override
            public void onTick ( long millisUntilFinished )
            {

            }

            @Override
            public void onFinish ()
            {
                lock = false;
            }
        };

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

        updateDirectLightGui( true );
        firstLoad = true;
    }

    private void updateDirectLightGui ( boolean toZero )
    {
        if ( !( gatewayLoader.getGatewaySettingsData()
                             .getActiveMode()
                             .equals( "M" ) ) )
        {

        }
        else
        {
            lightPower = Integer.parseInt( ( gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsLightPower()[Integer.parseInt( channelPointer )] ) );
            btnOn.setEnabled( false );
            btnOff.setEnabled( true );
        }
        circularSeekBar.setProgress( lightPower );
        textDirectLightPower.setText( lightPower + "%" );
        gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsLightPower()[Integer.parseInt( channelPointer )] = gatewayLoader.fillnumbers( lightPower );
    }

    private void updateDirectLight ()
    {
        if ( lock )
        {
            return;
        }
        lightPower = circularSeekBar.getProgress();
        textDirectLightPower.setText( "" + lightPower + "%" );
        gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsLightPower()[Integer.parseInt( channelPointer )] = gatewayLoader.fillnumbers( lightPower );
        if ( circularSeekBar.getProgress() < 100 )
        {
            lock = true;
            mCountDownTimer.start();
            ( (MainActivity) getActivity() ).SendSocketData( "channel set light " +
                                                             gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( channelPointer )] +
                                                             " " +
                                                             gatewayLoader.fillnumbers( lightPower ), null );
        }
    }

    private void updateDirectLightOff ()
    {
        lightPower = 0;
        textDirectLightPower.setText( "" + lightPower + "%" );
        circularSeekBar.setProgress( lightPower );
        gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsLightPower()[Integer.parseInt( channelPointer )] = gatewayLoader.fillnumbers( lightPower );
        ( (MainActivity) getActivity() )
        .SendSocketData( "channel set light " +
                         gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( channelPointer )] +
                         " " +
                         gatewayLoader.fillnumbers( lightPower ), null );
    }

    private void updateDirectLightOn ()
    {
        lightPower = 100;
        textDirectLightPower.setText( "" + lightPower + "%" );
        circularSeekBar.setProgress( lightPower );
        gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsLightPower()[Integer.parseInt( channelPointer )] = gatewayLoader.fillnumbers( lightPower );
        ( (MainActivity) getActivity() ).SendSocketData( "channel set light " +
                                                         gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsID()[Integer.parseInt( channelPointer )] +
                                                         " " +
                                                         gatewayLoader.fillnumbers( lightPower ), null );
    }

    //Hier kommen die Daten der MainActivity an
    @Override
    public void sendActivityData ( ArrayList< String > data, String command )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProgressChanged ( CircularSeekBar circularSeekBar, int progress, boolean fromUser )
    {

        if ( firstLoad )
        {
            if ( !btnOn.isEnabled() )
            {
                circularSeekBar.setEnabled( true );
                lightPower = circularSeekBar.getProgress();
                textDirectLightPower.setText( "" + lightPower + "%" );
                updateDirectLight();
            }
            else
            {
                circularSeekBar.setEnabled( false );
            }
        }
        //		btnOn.setEnabled(false);
        //		btnOff.setEnabled(true);

    }

    @Override
    public void onStopTrackingTouch ( CircularSeekBar seekBar )
    {
        // TODO Auto-generated method stub
        mCountDownTimer.cancel();
        lock = false;
        updateDirectLight();
    }

    @Override
    public void onStartTrackingTouch ( CircularSeekBar seekBar )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onDrag ( View v, DragEvent event )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onTouch ( View v, MotionEvent event )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClick ( View button )
    {
        switch ( button.getId() )
        {
            case R.id.imageViewBackToChannelMain:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                break;

            case R.id.buttonDirectLightOn:
                canupdate = true;
                updateDirectLightOn();
                btnOff.setEnabled( true );
                btnOn.setEnabled( false );
                break;
            case R.id.buttonDirectLightOff:
                canupdate = false;
                btnOff.setEnabled( false );
                btnOn.setEnabled( true );
                updateDirectLightOff();
//                updateDirectLightOff();
//                updateDirectLightOff();
                break;
        }
    }
}
