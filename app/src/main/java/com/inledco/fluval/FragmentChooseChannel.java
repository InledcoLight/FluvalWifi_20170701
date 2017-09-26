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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentChooseChannel extends Fragment implements InterfaceSendFromActivity, OnItemClickListener, OnClickListener
{

    private InterfaceSendFromFragment iSendData;
    private TextView textLightName, textLightNameLogo, textLightProduct, textLightSlot;
    private ListView lvChannels;
    private ImageView btnBackToLights;

    private GatewayLoader gatewayLoader;

    private String slot;
    private String name;

    public FragmentChooseChannel ()
    {
    }

    //    public FragmentChooseChannel ( String Slot, String Name )
    //    {
    //        this.slot = Slot;
    //        this.name = Name;
    //    }

    public static FragmentChooseChannel newInstance ( String slot, String name )
    {
        FragmentChooseChannel frag = new FragmentChooseChannel();
        Bundle bundle = new Bundle();
        bundle.putString( "slot", slot );
        bundle.putString( "name", name );
        frag.setArguments( bundle );
        return frag;
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_choose_channel, container, false );

        textLightName = (TextView) root.findViewById( R.id.textViewLightChooseLightName );
        textLightNameLogo = (TextView) root.findViewById( R.id.textViewChannelTimeSettingsMainLogo );
        textLightProduct = (TextView) root.findViewById( R.id.textViewLightChooseProduct );
        textLightSlot = (TextView) root.findViewById( R.id.textViewLightChooseSlot );
        lvChannels = (ListView) root.findViewById( R.id.listViewChooseChannel );
        lvChannels.setOnItemClickListener( this );
        btnBackToLights = (ImageView) root.findViewById( R.id.imageViewBackToChannelMain );
        btnBackToLights.setOnClickListener( this );

        Bundle bundle = getArguments();
        slot = bundle.getString( "slot" );
        name = bundle.getString( "name" );

        return root;
    }

    @Override
    public void onActivityCreated ( Bundle savedInstanceState )
    {
        Log.d( "MARCEL", "create fragment settings" );
        iSendData = (InterfaceSendFromFragment) getActivity();
        iSendData.sendTag( getTag() ); //�ber das Interface wird der MainAktivity mitgeteilt wo im Speicher sich dieses Fragment befindet
        gatewayLoader = ( (MainActivity) getActivity() ).gatewayLoader;

        updateTextGui();
        updateChannelListView();

        super.onActivityCreated( savedInstanceState );
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

    private void updateTextGui ()
    {
        textLightNameLogo.setText( name );
        textLightName.setText( name );
        textLightSlot.setText( "0" + ( Integer.parseInt( slot ) + 1 ) );

        if ( gatewayLoader.getSlots()[Integer.parseInt( slot )].getLightProduct()
                                                               .equals( "Hagen 2CH Type" ) )
        {
            textLightProduct.setText( "Fluval 2.0" );
        }
        else
        {
            textLightProduct.setText( gatewayLoader.getSlots()[Integer.parseInt( slot )].getLightProduct() );
        }
    }

    private void updateChannelListView ()
    {

        try
        {

            ArrayList< String > channelsList = new ArrayList< String >();

            for ( int i = 0;
                  i <
                  gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsActiveNamesList()
                                                                    .size();
                  i++ )
            {
                String[] splitData = gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsActiveNamesList()
                                                                                       .get( i )
                                                                                       .split( ";" );
                channelsList.add( splitData[0] );
            }

            ArrayAdapter< String > channelsListViewAdapter = new ArrayAdapter< String >( getActivity(),
                                                                                         R.layout.customlistviewlights,
                                                                                         R.id.textViewLightName,
                                                                                         channelsList );
            lvChannels.setAdapter( channelsListViewAdapter );
        }
        catch ( Exception e )
        {
            // TODO: handle exception
        }
    }

    @Override
    public void onItemClick ( AdapterView< ? > parent, View view, int position, long id )
    {

        //((MainActivity)getActivity()).print(gatewayLoader.getSlots()[Integer.parseInt(slot)].getChannelsActiveNamesList().get(position));
        //TODO DIESEN WERT IN DIE CHANNEL MAIN �BERGEBEN UND DORT PARSEN HINTER DEN CHANNEL NAMEN SIND DIE INDEXE F�R DAS CHANNEL ARRAY IM SLOT DES GATEWAYLOADERS :)
        FragmentManager fm = getActivity().getSupportFragmentManager();
        //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fm.beginTransaction();
        //	ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace( R.id.fragment_root,
                    FragmentChannelMain.newInstance( slot,
                                                     gatewayLoader.getSlots()[Integer.parseInt( slot )].getChannelsActiveNamesList()
                                                                                                       .get( position ) ),
                    "FragmentChannelMain" );
        ft.addToBackStack( null );
        ft.commit();
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

            default:
                break;
        }
    }
}
