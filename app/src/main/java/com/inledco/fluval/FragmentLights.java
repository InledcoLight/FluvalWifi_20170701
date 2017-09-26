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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentLights extends Fragment implements InterfaceSendFromActivity, OnClickListener, OnCheckedChangeListener, OnItemClickListener
{

    private InterfaceSendFromFragment iSendData;

    private ImageView btnNewLight;
    private ListView lvLightsChoose;
    private Switch switchAutomaticMode;
    private TextView textMainLogo;

    private boolean fragmentLoaded = false;
    private GatewayLoader gatewayLoader;

    private ArrayList< String > connectedLightsListSlot = new ArrayList< String >();
    private ArrayList< String > connectedLightsList = new ArrayList< String >();

    private int longClickedlightPointer = 0;
    private ArrayAdapter< String > lightsListViewAdapter;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_lights, container, false );

        btnNewLight = (ImageView) root.findViewById( R.id.imageViewAddLight );
        btnNewLight.setOnClickListener( this );
        lvLightsChoose = (ListView) root.findViewById( R.id.listViewChooseLights );
        lvLightsChoose.setOnItemClickListener( this );
        registerForContextMenu( lvLightsChoose );
        switchAutomaticMode = (Switch) root.findViewById( R.id.switchAutomaticMode );
        switchAutomaticMode.setOnCheckedChangeListener( this );
        textMainLogo = (TextView) root.findViewById( R.id.textLightsLogo );

        return root;
    }

    @Override
    public void onCreateContextMenu ( ContextMenu menu, View v, ContextMenuInfo menuInfo )
    {
        super.onCreateContextMenu( menu, v, menuInfo );
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle( connectedLightsList.get( info.position ) + getResources().getString( R.string.deleteme ) + "?" );
        longClickedlightPointer = info.position;
        menu.add( 0, v.getId(), 0, getResources().getString( R.string.deleteme ) );
        menu.add( 0, v.getId(), 0, "cancel" );
    }

    @Override
    public boolean onContextItemSelected ( MenuItem item )
    {
        if ( item.getTitle() == getResources().getString( R.string.deleteme ) )
        {
            gatewayLoader.getSlots()[Integer.parseInt( connectedLightsListSlot.get( longClickedlightPointer ) )].setType( "-1" );
            gatewayLoader.getSlots()[Integer.parseInt( connectedLightsListSlot.get( longClickedlightPointer ) )].setActive( false );
            ArrayList< String > sendRemoveLightSocketCommand = new ArrayList< String >();
            sendRemoveLightSocketCommand.add( "light set type " + connectedLightsListSlot.get( longClickedlightPointer ) + " -1" );
//            ( (MainActivity) getActivity() ).startFireSocket( sendRemoveLightSocketCommand );
            ( (MainActivity) getActivity() ).SendSocketData( "light set type " + connectedLightsListSlot.get( longClickedlightPointer ) + " -1", new TransmitListener() {
                @Override
                public void onReceive ( String buffer )
                {
                    if ( "OK".equals( buffer ) )
                    {
                        connectedLightsListSlot.remove( longClickedlightPointer );
                        connectedLightsList.remove( longClickedlightPointer );
                        getActivity().runOnUiThread( new Runnable() {
                            @Override
                            public void run ()
                            {
                                lightsListViewAdapter.notifyDataSetChanged();
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
            ( (MainActivity) getActivity() ).print( connectedLightsList.get( longClickedlightPointer ) + " " + getResources().getString( R.string.delete ) );
//            connectedLightsListSlot.remove( longClickedlightPointer );
//            connectedLightsList.remove( longClickedlightPointer );
//            lightsListViewAdapter.notifyDataSetChanged();
        }
        else
        {
            if ( item.getTitle() == "cancel" )
            {

            }
            else
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityCreated ( Bundle savedInstanceState )
    {
        Log.d( "MARCEL", "create fragment settings" );
        iSendData = (InterfaceSendFromFragment) getActivity();
        iSendData.sendTag( getTag() ); //�ber das Interface wird der MainAktivity mitgeteilt wo im Speicher sich dieses Fragment befindet
        gatewayLoader = ( (MainActivity) getActivity() ).gatewayLoader;
        super.onActivityCreated( savedInstanceState );

        updateAutomaticMode();
        updateLightsListView();

        textMainLogo.setText( getString( R.string.frag_light_title ) +
                              " | " +
                              gatewayLoader.getGatewaySettingsData()
                                           .getName() );

        fragmentLoaded = true;
    }

    //Hier kommen die Daten der MainActivity an
    @Override
    public void sendActivityData ( ArrayList< String > data, String command )
    {

        if ( command.equals( "firesocketFinish" ) )
        {
            Log.d( "MARCEL", "firesocketFinish" );
        }

        if ( command.equals( "sniffSlotDataFinish" ) )
        {
            Log.d( "MARCEL", "sniffSlotDataFinish" );
        }
    }

    private void updateAutomaticMode ()
    {

        if ( gatewayLoader.getGatewaySettingsData()
                          .getActiveMode()
                          .equals( "A" ) )
        {
            switchAutomaticMode.setChecked( true );
        }
        else
        {
            if ( gatewayLoader.getGatewaySettingsData()
                              .getActiveMode()
                              .equals( "M" ) )
            {
                switchAutomaticMode.setChecked( false );
            }
        }
    }

    @Override
    public void onResume ()
    {
        super.onResume();
        updateLightsListView();
    }

    private void updateLightsListView ()
    {

        try
        {

            connectedLightsList = new ArrayList< String >();

            for ( int i = 0;
                  i <
                  gatewayLoader.getGatewaySettingsData()
                               .getSlots();
                  i++ )
            {
                if ( gatewayLoader.getSlots()[i].isActive() )
                {
                    connectedLightsListSlot.add( "0" + gatewayLoader.getSlots()[i].getSlotNr() );
                    connectedLightsList.add( gatewayLoader.getSlots()[i].getName() );
                }
            }

            lightsListViewAdapter = new ArrayAdapter< String >( getActivity(), R.layout.customlistviewlights, R.id.textViewLightName, connectedLightsList );
            lvLightsChoose.setAdapter( lightsListViewAdapter );
        }
        catch ( Exception e )
        {
            // TODO: handle exception
        }
    }

    @Override
    public void onClick ( View button )
    {

        switch ( button.getId() )
        {
            case R.id.imageViewAddLight:

                FragmentManager fm = getActivity().getSupportFragmentManager();
                getActivity().getSupportFragmentManager()
                             .popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
                FragmentTransaction ft = fm.beginTransaction();
                //	ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
                ft.replace( R.id.fragment_root, new FragmentNewLight(), "FragmentNewLight" );
                ft.addToBackStack( null );
                ft.commit();
                fragmentLoaded = false;
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged ( CompoundButton buttonView, boolean isChecked )
    {
        if ( fragmentLoaded )
        {
            if ( buttonView.getId() == R.id.switchAutomaticMode )
            {
                if ( switchAutomaticMode.isChecked() )
                {
                    gatewayLoader.getGatewaySettingsData()
                                 .setActiveMode( "A" );
                    ( (MainActivity) getActivity() ).SendSocketData( "gateway set mode A", null );
                    Log.d( "MARCEL", "GATEWAY AUTOMATIC MODE ON" );
                }
                else
                {
                    gatewayLoader.getGatewaySettingsData()
                                 .setActiveMode( "M" );
                    ( (MainActivity) getActivity() ).SendSocketData( "gateway set mode M", null );
                    Log.d( "MARCEL", "GATEWAY AUTOMATIC MODE OFF" );
                }
            }
        }
    }

    @Override
    public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
    {
        //((MainActivity)getActivity()).print(parent.getItemAtPosition(position).toString() + " | SlotNr: "+connectedLightsListSlot.get(position));
        FragmentManager fm = getActivity().getSupportFragmentManager();
        getActivity().getSupportFragmentManager()
                     .popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
        FragmentTransaction ft = fm.beginTransaction();
        //	ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace( R.id.fragment_root,
                    FragmentChooseChannel.newInstance( connectedLightsListSlot.get( position ),
                                                       parent.getItemAtPosition( position )
                                                             .toString() ),
                    "FragmentChooseChannel" );
        ft.addToBackStack( null );
        ft.commit();
        fragmentLoaded = false;
    }
}
