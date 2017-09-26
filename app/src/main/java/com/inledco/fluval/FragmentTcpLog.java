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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentTcpLog extends Fragment implements InterfaceSendFromActivity, OnClickListener
{

    private InterfaceSendFromFragment iSendData;

    private TextView incommingData;
    private ScrollView svIncomming;
    private Button btnSendData;
    private EditText textSendData;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_tcp_log, container, false );

        incommingData = (TextView) root.findViewById( R.id.textViewIncommingData );
        svIncomming = (ScrollView) root.findViewById( R.id.ScrollViewIncomming );
        btnSendData = (Button) root.findViewById( R.id.buttonSendData );
        btnSendData.setOnClickListener( this );
        textSendData = (EditText) root.findViewById( R.id.editTextSendData );
        return root;
    }

    @Override
    public void onActivityCreated ( Bundle savedInstanceState )
    {
        Log.d( "MARCEL", "create fragment settings" );
        iSendData = (InterfaceSendFromFragment) getActivity();
        iSendData.sendTag( getTag() ); //�ber das Interface wird der MainAktivity mitgeteilt wo im Speicher sich dieses Fragment befindet
        super.onActivityCreated( savedInstanceState );

        incommingData.setText( "" );
        updateText( ( (MainActivity) getActivity() ).tcpLog );
        ( (MainActivity) getActivity() ).tcplogActive = true;
    }

    //Hier kommen die Daten der MainActivity an
    @Override
    public void sendActivityData ( ArrayList< String > data, String command )
    {

        incommingData.setText( "" );
        updateText( ( (MainActivity) getActivity() ).tcpLog );
    }

    private void updateText ( String test )
    {
        svIncomming.scrollTo( 0, svIncomming.getBottom() );
        incommingData.setText( incommingData.getText()
                                            .toString() + test );
    }

    @Override
    public void onDestroy ()
    {
        ( (MainActivity) getActivity() ).tcplogActive = false;
        super.onDestroy();
    }

    @Override
    public void onClick ( View button )
    {

        if ( button.getId() == R.id.buttonSendData )
        {
            ( (MainActivity) getActivity() ).SendSocketData( textSendData.getText()
                                                                         .toString(), null );
        }
    }
}
