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

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentSettings extends Fragment implements InterfaceSendFromActivity, OnClickListener, OnCheckedChangeListener
{

    private InterfaceSendFromFragment iSendData;

    private RelativeLayout btnChangePassword, btnResetGateway, btnRenameGateway;
    private Switch switchCloud;

    private Button btnTcpLogger;
    private GatewayLoader gatewayLoader;
    private boolean loaded = false;

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View root = inflater.inflate( R.layout.frag_settings, container, false );

        btnChangePassword = (RelativeLayout) root.findViewById( R.id.btnChangePassword );
        btnChangePassword.setOnClickListener( this );
        btnResetGateway = (RelativeLayout) root.findViewById( R.id.btn2ResetGateway );
        btnResetGateway.setOnClickListener( this );
        btnRenameGateway = (RelativeLayout) root.findViewById( R.id.btnRenameGateway );
        btnRenameGateway.setOnClickListener( this );
        switchCloud = (Switch) root.findViewById( R.id.switchCloud );
        switchCloud.setOnCheckedChangeListener( this );

        btnTcpLogger = (Button) root.findViewById( R.id.buttonTcpLogger );
        btnTcpLogger.setOnClickListener( this );

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
        updateGui();
        loaded = true;
    }

    private void updateGui ()
    {
        if ( gatewayLoader.getGatewaySettingsData()
                          .getActiveCloudMode()
                          .equals( "01" ) )
        {
            switchCloud.setChecked( true );
        }
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
        switch ( button.getId() )
        {
            case R.id.btnChangePassword:
                dialogChangePassword();
                break;
            case R.id.btn2ResetGateway:
                dialogResetGateway();
                break;
            case R.id.btnRenameGateway:
                dialogRenameGateway();
                break;
            case R.id.buttonTcpLogger:

                FragmentManager fm = getActivity().getSupportFragmentManager();
                getActivity().getSupportFragmentManager()
                             .popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
                FragmentTransaction ft = fm.beginTransaction();
                //	ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
                ft.replace( R.id.fragment_root, new FragmentTcpLog(), "FragmentTcpLog" );
                ft.addToBackStack( null );
                ft.commit();

                break;
        }
    }

    private void dialogResetGateway ()
    {
        final Dialog dialog = new Dialog( getActivity() );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setContentView( R.layout.dialog_reset_gateway );

        Button btnCancel = (Button) dialog.findViewById( R.id.buttonCancel );
        Button btnReset = (Button) dialog.findViewById( R.id.buttonNewPassword );

        btnCancel.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {
                dialog.dismiss();
            }
        } );

        btnReset.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {

                ArrayList< String > newPasswordSocketCommand = new ArrayList< String >();
                newPasswordSocketCommand.add( "gateway factory reset" );
//                ( (MainActivity) getActivity() ).startFireSocket( newPasswordSocketCommand );
//                ClientSocket.getInstance().sendDataArray( newPasswordSocketCommand );
                ((MainActivity)getActivity()).SendSocketData( newPasswordSocketCommand.get( 0 ), null );
                Toast.makeText( getContext(), "gateway reset...", Toast.LENGTH_SHORT )
                     .show();
                dialog.dismiss();
                //Todo auf r�ck antwort des servers warten also auf ein ok und dann erst den dialog beenden
            }
        } );

        dialog.show();
    }

    private void dialogChangePassword ()
    {
        final Dialog dialog = new Dialog( getActivity() );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setContentView( R.layout.dialog_change_password );

        Button btnCancel = (Button) dialog.findViewById( R.id.buttonCancel );
        Button btnChangePassword = (Button) dialog.findViewById( R.id.buttonNewPassword );

        btnCancel.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {
                dialog.dismiss();
            }
        } );

        btnChangePassword.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {
                EditText editNewPassword = (EditText) dialog.findViewById( R.id.editTextNewPassword );
                ArrayList< String > newPasswordSocketCommand = new ArrayList< String >();
                newPasswordSocketCommand.add( "login change " +
                                              editNewPassword.getText()
                                                             .toString() );
//                ( (MainActivity) getActivity() ).startFireSocket( newPasswordSocketCommand );
//                ClientSocket.getInstance().sendDataArray( newPasswordSocketCommand );
                ((MainActivity)getActivity()).SendSocketData( newPasswordSocketCommand.get( 0 ), new TransmitListener() {
                    @Override
                    public void onReceive ( String buffer )
                    {
                        if ( "OK".equals( buffer ) )
                        {
                            getActivity().runOnUiThread( new Runnable() {
                                @Override
                                public void run ()
                                {
                                    Toast.makeText( getContext(), "password changed", Toast.LENGTH_SHORT )
                                         .show();
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
                dialog.dismiss();
                //Todo auf r�ck antwort des servers warten also auf ein ok und dann erst den dialog beenden
            }
        } );

        dialog.show();
    }

    private void dialogRenameGateway ()
    {
        final Dialog dialog = new Dialog( getActivity() );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setContentView( R.layout.dialog_rename_gateway );

        Button btnCancel = (Button) dialog.findViewById( R.id.buttonCancel );
        Button btnRenameGateway = (Button) dialog.findViewById( R.id.buttonRenameGateway );

        btnCancel.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {
                dialog.dismiss();
            }
        } );

        btnRenameGateway.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick ( View v )
            {
                EditText editRenameGateway = (EditText) dialog.findViewById( R.id.editTextNewPassword );
                ArrayList< String > renameGatewaySocketCommand = new ArrayList< String >();
                renameGatewaySocketCommand.add( "gateway set name " +
                                                editRenameGateway.getText()
                                                                 .toString()
                                                                 .trim() );
//                ( (MainActivity) getActivity() ).startFireSocket( renameGatewaySocketCommand );
//                ClientSocket.getInstance().sendDataArray( renameGatewaySocketCommand );
                ((MainActivity)getActivity()).SendSocketData( renameGatewaySocketCommand.get( 0 ), new TransmitListener() {
                    @Override
                    public void onReceive ( String buffer )
                    {
                        if ( "OK".equals( buffer ) )
                        {
                            getActivity().runOnUiThread( new Runnable() {
                                @Override
                                public void run ()
                                {
                                    Toast.makeText( getContext(), "gateway renamed", Toast.LENGTH_SHORT )
                                         .show();
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
                dialog.dismiss();
                //Todo auf r�ck antwort des servers warten also auf ein ok und dann erst den dialog beenden
            }
        } );

        dialog.show();
    }

    @Override
    public void onCheckedChanged ( CompoundButton buttonView, boolean isChecked )
    {
        ArrayList< String > sendcloudSocketCommand = new ArrayList< String >();
        if ( loaded )
        {
            if ( isChecked )
            {
                sendcloudSocketCommand.add( "cloud set" );
//                ( (MainActivity) getActivity() ).startFireSocket( sendcloudSocketCommand );
                ((MainActivity) getActivity()).sendDataArray( sendcloudSocketCommand );
                gatewayLoader.getGatewaySettingsData()
                             .setActiveCloudMode( "01" );
            }
            else
            {
                sendcloudSocketCommand.add( "cloud reset" );
//                ( (MainActivity) getActivity() ).startFireSocket( sendcloudSocketCommand );
                ((MainActivity) getActivity()).sendDataArray( sendcloudSocketCommand );
                gatewayLoader.getGatewaySettingsData()
                             .setActiveCloudMode( "00" );
            }
        }
    }
}
