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
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class FragmentLogin extends Fragment implements InterfaceSendFromActivity, OnClickListener
{

    private InterfaceSendFromFragment iSendData;
    private Button btnLogin;
    private EditText inputIp, inputPassword;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_login, container, false );

        btnLogin = (Button) root.findViewById( R.id.buttonLogin );
        btnLogin.setOnClickListener( this );
        inputIp = (EditText) root.findViewById( R.id.editTextIp );
        inputPassword = (EditText) root.findViewById( R.id.EditTextPassword );

        return root;
    }

    @Override
    public void onActivityCreated ( Bundle savedInstanceState )
    {
        Log.d( "MARCEL", "create fragment settings" );
        iSendData = (InterfaceSendFromFragment) getActivity();
        iSendData.sendTag( getTag() ); //�ber das Interface wird der MainAktivity mitgeteilt wo im Speicher sich dieses Fragment befindet
        super.onActivityCreated( savedInstanceState );

        updateGuiFields();
    }

    //Hier kommen die Daten der MainActivity an
    @Override
    public void sendActivityData ( ArrayList< String > data, String command )
    {

        if ( command.equals( "login fail" ) )
        {
            btnLogin.setEnabled( true );
        }
    }

    private void updateGuiFields ()
    {
        if ( checkConnectionWifi() )
        {
            inputIp.setText( getIpAddr() );
            //	inputIp.setText("192.168.0.12");
        }
        else
        {
            inputIp.setText( "0.0.0.0" );
        }
        inputPassword.setText( "0000" );
    }

    //Diese Methode Snifft die Gateway IP des Accesspoints
    //-----------------------------------------------------------------------------------
    public boolean checkConnectionWifi ()
    {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService( getActivity().CONNECTIVITY_SERVICE );
        NetworkInfo mWifi = connManager.getNetworkInfo( ConnectivityManager.TYPE_WIFI );
        String status;
        if ( mWifi.isConnected() )
        {

            return true;
        }
        else
        {
            return false;
        }
    }

    //-----------------------------------------------------------------------------------
    public String getIpAddr ()
    {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService( getActivity().WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        String ipString = String.format( "%d.%d.%d.1", ( ip & 0xff ), ( ip >> 8 & 0xff ), ( ip >> 16 & 0xff ), ( ip >> 24 & 0xff ) );

        return ipString;
    }

    @Override
    public void onClick ( View button )
    {
        if ( button.getId() == R.id.buttonLogin )
        {

            if ( !( inputIp.getText()
                           .toString()
                           .substring( 0, 3 )
                           .equals( "172" ) ) )
            {
                showDialog( getString( R.string.wrong_wifi ) );
                return;
            }

//            btnLogin.setEnabled( false );
            if ( inputPassword.getText()
                              .toString()
                              .equals( "0000" ) )
            {
                ( (MainActivity) getActivity() ).connectToGateway( inputIp.getText()
                                                                          .toString(),
                                                                   inputPassword.getText()
                                                                                .toString() );
            }
            else
            {
                ( (MainActivity) getActivity() ).connectToGateway( inputIp.getText()
                                                                          .toString(),
                                                                   inputPassword.getText()
                                                                                .toString() );
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
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

    //-------------------------------------------------------------------------------------------------------------------------------------------
}
