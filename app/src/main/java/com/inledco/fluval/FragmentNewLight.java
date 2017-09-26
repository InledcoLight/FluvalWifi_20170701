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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;

public class FragmentNewLight extends Fragment implements InterfaceSendFromActivity, OnClickListener
{

    private InterfaceSendFromFragment iSendData;

    private Spinner spinSlots, spinProducts;
    private EditText textLightName;
    private RelativeLayout btnNewLight;
    private ImageView btnimgBackToLights;

    private GatewayLoader gatewayLoader;

    private boolean updateSlotData = false;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_new_light, container, false );

        spinSlots = (Spinner) root.findViewById( R.id.spinnerSlots );
        spinProducts = (Spinner) root.findViewById( R.id.SpinnerProducts );
        btnNewLight = (RelativeLayout) root.findViewById( R.id.buttonNewLightSave );
        btnNewLight.setOnClickListener( this );
        btnimgBackToLights = (ImageView) root.findViewById( R.id.imageViewBackToChannelMain );
        btnimgBackToLights.setOnClickListener( this );

        textLightName = (EditText) root.findViewById( R.id.editTextNewLightName );

        return root;
    }

    @Override
    public void onActivityCreated ( Bundle savedInstanceState )
    {
        Log.d( "MARCEL", "create fragment settings" );
        iSendData = (InterfaceSendFromFragment) getActivity();
        iSendData.sendTag( getTag() ); //�ber das Interface wird der MainAktivity mitgeteilt wo im Speicher sich dieses Fragment befindet
        gatewayLoader = ( (MainActivity) getActivity() ).gatewayLoader;
        ( (MainActivity) getActivity() ).displayBackStack();
        updateSpinners();

        super.onActivityCreated( savedInstanceState );
    }

    //Hier kommen die Daten der MainActivity an
    @Override
    public void sendActivityData ( ArrayList< String > data, String command )
    {

        if ( command.equals( "firesocketFinish" ) )
        {
            Log.d( "MARCEL", "firesocketFinish" );
            if ( updateSlotData )
            {
                updateSlotData = false;
//                ( (MainActivity) getActivity() ).sniffSlotData( spinSlots.getSelectedItemPosition() );
                ( (MainActivity) getActivity() ).getLightType( spinSlots.getSelectedItemPosition() );
            }
        }

        if ( command.equals( "sniffSlotDataFinish" ) )
        {
            Log.d( "MARCEL", "sniffSlotDataFinish" );
            FragmentManager fm = getActivity().getSupportFragmentManager();
            getActivity().getSupportFragmentManager()
                         .popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
            FragmentTransaction ft = fm.beginTransaction();
            //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            ft.replace( R.id.fragment_root, new FragmentLights(), "FragmentLights" );
            //ft.addToBackStack(null);
            ft.commit();
            ( (MainActivity) getActivity() ).print( "saved..." );
        }
    }

    @Override
    public void onClick ( View button )
    {

        switch ( button.getId() )
        {
            case R.id.imageViewBackToChannelMain:
                //			FragmentManager fm = getActivity().getSupportFragmentManager();
                //			fm.popBackStack();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                getActivity().getSupportFragmentManager()
                             .popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
                FragmentTransaction ft = fm.beginTransaction();
                //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                ft.replace( R.id.fragment_root, new FragmentLights(), "FragmentLights" );
                //ft.addToBackStack(null);
                ft.commit();

                break;

            case R.id.buttonNewLightSave:
                sendSaveNewLight();
                break;
        }
    }

    private void sendSaveNewLight ()
    {
        final ArrayList< String > sendNewLightSocketSendData = new ArrayList< String >();

        if ( gatewayLoader.getGatewaySettingsData()
                          .getSlots() == 2 )
        {
            sendNewLightSocketSendData.add( "light set name 0" +
                                            spinSlots.getSelectedItemPosition() +
                                            " " +
                                            textLightName.getText()
                                                         .toString() +
                                            " " );
            sendNewLightSocketSendData.add( "light set type 0" + spinSlots.getSelectedItemPosition() + " " + ( spinProducts.getSelectedItemPosition() + 12 ) );
        }
        else
        {
            sendNewLightSocketSendData.add( "light set name 0" +
                                            spinSlots.getSelectedItemPosition() +
                                            " " +
                                            textLightName.getText()
                                                         .toString() +
                                            " " );
            if ( spinProducts.getSelectedItemPosition() < 9 )
            {
                sendNewLightSocketSendData.add( "light set type 0" +
                                                spinSlots.getSelectedItemPosition() +
                                                " 0" +
                                                ( spinProducts.getSelectedItemPosition() + 1 ) +
                                                " " );
            }
            else
            {
                sendNewLightSocketSendData.add( "light set type 0" +
                                                spinSlots.getSelectedItemPosition() +
                                                " " +
                                                ( spinProducts.getSelectedItemPosition() + 1 ) +
                                                " " );
            }
        }

        updateSlotData = true;
//        ( (MainActivity) getActivity() ).print( "saving..." );
//        ( (MainActivity) getActivity() ).startFireSocket( sendNewLightSocketSendData );
        final ArrayList<String> rcvs = new ArrayList<>();
        TransmitListener listener = new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                rcvs.add( buffer );
                if ( rcvs.size() == sendNewLightSocketSendData.size() )
                {
                    ( (MainActivity) getActivity() ).getLightType( spinSlots.getSelectedItemPosition() );
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run ()
                        {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            fm.popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace( R.id.fragment_root, new FragmentLights(), "FragmentLights" );
                            ft.commit();
                            ( (MainActivity) getActivity() ).print( "saved..." );
                        }
                    } );
                }
                else
                {
                    ClientSocket.getInstance().sendDataWithString( sendNewLightSocketSendData.get( rcvs.size() ), true, this );
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
        };
        ClientSocket.getInstance().sendDataWithString( sendNewLightSocketSendData.get( 0 ), true, listener );
    }

    private void updateSpinners ()
    {

        ArrayList< String > slotsObjectList = new ArrayList< String >();
        ArrayList< String > productObjectList = new ArrayList< String >();

        for ( int i = 0;
              i <
              gatewayLoader.getGatewaySettingsData()
                           .getSlots();
              i++ )
        {
            slotsObjectList.add( "Slot " + ( i + 1 ) );
        }

        if ( gatewayLoader.getGatewaySettingsData()
                          .getSlots() == 2 )
        {
            for ( int i = 11; i < 13; i++ )
            {
                productObjectList.add( gatewayLoader.getLightDataDeviceList()
                                                    .getLightDataList()
                                                    .get( i )
                                                    .getBezeichnung() );
            }
        }
        else
        {
            for ( int i = 0;
                  i <
                  gatewayLoader.getLightDataDeviceList()
                               .getLightDataList()
                               .size();
                  i++ )
            {
                productObjectList.add( gatewayLoader.getLightDataDeviceList()
                                                    .getLightDataList()
                                                    .get( i )
                                                    .getBezeichnung() );
            }
        }

        ArrayAdapter adapterSpinnerSlots = new ArrayAdapter( getActivity().getApplicationContext(),
                                                             R.layout.spinner_item_layout,
                                                             R.id.SlotItem,
                                                             slotsObjectList );
        spinSlots.setAdapter( adapterSpinnerSlots );
        ArrayAdapter adapterSpinnerProducts = new ArrayAdapter( getActivity().getApplicationContext(),
                                                                R.layout.spinner_item_layout,
                                                                R.id.SlotItem,
                                                                productObjectList );
        spinProducts.setAdapter( adapterSpinnerProducts );
    }
}
