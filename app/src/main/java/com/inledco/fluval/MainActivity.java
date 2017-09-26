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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends FragmentActivity implements InterfaceSendFromFragment, OnClickListener
{
    private static final String TAG = "MainActivity";
    private InterfaceSendFromActivity isendFromActivity;

//    private ClientSocket clientSocket;
    private KeepAliveTimer keepAliveTimer;
    private String Ip = "192.168.5.104";
    //private String Ip = "172.31.153.1";
    private final static String Port = "4000";
//    private ClientSocketConnectionTask clientSocketConnectionTask;

    private boolean connectionOk;
    private boolean loginOk = false;
    private boolean checkPassword = false;
    private String loginPassword = "";

    private ArrayList< String > fireSocketCommandList;
    private ArrayList< String > receiveSockeList;
    private boolean fireSocketActive = false;
    private int GatewayLoaderStepsCounter = 0;

    private String ActiveWorkingSocketProcess = "";

    private boolean sniffSlotDataActive = false;
    private int sniffSlotDataActive_SlotVar = 0;

    private boolean sniffGatewaySettingsData = false;

    public GatewayLoader gatewayLoader;

    private String sendFireCommandTag = "";

//    private LoginTimer loginTimer;
//    private SniffGatewayDataTimer sniffGatewayDataTimer;

    private ActionBar actionBar;

    private UpdateTimeTimer updateTimeTimer;

    public boolean isGerman;

    private boolean firstLoadComplete = false;

    public String tcpLog;
    public boolean tcplogActive = false;
    private boolean log_active = true;

//    private ConnectionTimeOutBomb connectionTimeOutBomb;
//    private SocketConnectorTimer socketConnectorTimer;
    private int socketConnectorTimerTimeout = 0;
    private ProgressDialog loadingDialog;
    private final MyLifecycleHandler mCallbacks = new MyLifecycleHandler();

    private boolean resumeExit = false;
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private RelativeLayout mainMenu, btnLights, btnSettings;
    private boolean MenuLightSettingSwitcher = false;
    private ImageView imgLights, imgSettings;
    private TextView textLightsMenu, textSettingsMenu, textTime;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        getApplication().registerActivityLifecycleCallbacks( mCallbacks );
//        actionBar = getActionBar();
//        actionBar.hide();

        if ( log_active )
        {

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File( sdCard.getAbsolutePath() );
            File file = new File( sdCard.getAbsolutePath() + "/fluval_log.txt" );
            boolean deleted = file.delete();

            appendLog( "Fluval Log Data" );
            appendLog( "+++++++++++++++" );
            appendLog( " " );
        }
        isGerman = isGerman();
        gatewayLoader = new GatewayLoader( isGerman );
        //	createEmuLoaderData(); //Nur zum Testen und Entwickeln der Gui damit der Gatewayloader Daten bekommt

        FragmentManager fm = getSupportFragmentManager();
        //	getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fm.beginTransaction();
        //	ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
        //	ft.replace(R.id.fragment_root ,new FragmentSplash(),"FragmentSplash");
        //	ft.replace(R.id.fragment_root ,new FragmentTcpLog(),"Test");
        ft.replace( R.id.fragment_root, new FragmentLogin(), "FragmentLogin" );
        //	ft.replace(R.id.fragment_root ,new FragmentLights(),"FragmentLights");
        //	ft.replace(R.id.fragment_root ,new FragmentDirectLightControl("00","0"),"Test");
        //	ft.replace(R.id.fragment_root ,new FragmentChannelTimeSettings("Sunrise","00",gatewayLoader.getSlots()[0].getChannelsActiveNamesList().get(0),false),"Test");
        //	ft.replace(R.id.fragment_root ,new FragmentChannelMain("00",gatewayLoader.getSlots()[0].getChannelsActiveNamesList().get(0)),"Test");
        //	ft.replace(R.id.fragment_root ,new FragmentColorPicker(),"Test");
        //	ft.addToBackStack(null);

        ft.commit();

        getDeviceSuperInfo();

        keepAliveTimer = new KeepAliveTimer( 10000, 1000 );
//        loginTimer = new LoginTimer( 800, 1 );
//        sniffGatewayDataTimer = new SniffGatewayDataTimer( 1500, 1 );
        updateTimeTimer = new UpdateTimeTimer( 1000, 1000 );
//        connectionTimeOutBomb = new ConnectionTimeOutBomb( 4000, 4000 );
//        socketConnectorTimer = new SocketConnectorTimer( 1000, 1000 );

        socketConnectorTimerTimeout = 10;

        //connectToGateway(Ip, "0000"); //Test Connection hier f�r ist sp�ter der Loginscreen verantworlich :)

        imgLights = (ImageView) findViewById( R.id.imageLights );
        imgSettings = (ImageView) findViewById( R.id.imageSettings );
        imgLights.setOnClickListener( this );
        imgSettings.setOnClickListener( this );

        textLightsMenu = (TextView) findViewById( R.id.textViewChannelTimeSettingsMainLogo );
        textSettingsMenu = (TextView) findViewById( R.id.textViewSettingsMenu );
        textTime = (TextView) findViewById( R.id.textViewTimeField );

        imgLights.setImageDrawable( getResources().getDrawable( R.drawable.sun_clicked ) );

        btnLights = (RelativeLayout) findViewById( R.id.button_lights_menu );
        btnLights.setOnClickListener( this );

        btnSettings = (RelativeLayout) findViewById( R.id.button_settings_menu );
        btnSettings.setOnClickListener( this );

        mainMenu = (RelativeLayout) findViewById( R.id.MainMenuID );
        //	mainMenu.setVisibility(RelativeLayout.VISIBLE);

        textTime.setText( getTime( 1 ) );
        updateTimeTimer.start();

        loadingDialog = new ProgressDialog( this );

        this.registerReceiver( this.WifiStateChangedReceiver, new IntentFilter( WifiManager.WIFI_STATE_CHANGED_ACTION ) );

        this.registerReceiver( this.myWifiReceiver, new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION ) );
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //Emulierte ServerDaten zum Testen und Entwickeln der Gui
    private void createEmuLoaderData ()
    {

        String gatewayInfo = "2CH A FLUVAL_WIFI";

        gatewayLoader.getGatewaySettingsData()
                     .parseGetwayGetInfo( gatewayInfo );

        Log.d( "MARCEL", "createEmuLoaderData()" );
        Log.d( "MARCEL", "---------------------------------------------------" );
        ArrayList< String > slotData = new ArrayList< String >();
        ArrayList< String > updateData = new ArrayList< String >();
        slotData.add( "02" );
        slotData.add( "Hugo 1" );
        gatewayLoader.buildSlotData( 0, slotData );
        updateData.add( "0800 0830 000 100 1200 1400 050 050 1800 1900 100 000" );
        updateData.add( "0800 0830 000 100 1200 1400 050 050 1800 1900 100 000" );
        updateData.add( "0800 0830 000 100 1200 1400 050 050 1800 1900 100 000" );
        updateData.add( "0800 0830 000 100 1200 1400 050 050 1800 1900 100 000" );
        updateData.add( "0800 0830 000 100 1200 1400 050 050 1800 1900 100 000" );
        updateData.add( "100" );
        updateData.add( "100" );
        updateData.add( "100" );
        updateData.add( "100" );
        updateData.add( "100" );
        gatewayLoader.updateSlotData( 0, updateData );
        slotData.clear();
        slotData.add( "05" );
        slotData.add( "Hugo 2" );
        gatewayLoader.buildSlotData( 1, slotData );
        slotData.clear();
        slotData.add( "13" );
        slotData.add( "Hugo 3" );
        gatewayLoader.buildSlotData( 2, slotData );
        Log.d( "MARCEL", "---------------------------------------------------" );
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu ( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected ( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == R.id.action_settings )
        {
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

    //Hier sind die Daten die vom Fragment her kommen :)
    @Override
    public void sendTag ( String fragmentTag )
    {
        Log.d( "MARCEL", "Fragment Tag: " + fragmentTag );
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment frag = fm.findFragmentByTag( fragmentTag );
        isendFromActivity = (InterfaceSendFromActivity) frag; //Von hier aus werden Daten zur dem Aktuellen Fragment gesendet
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void sendFragmentFromFragmentData ( String command, ArrayList< String > data )
    {
        // TODO Auto-generated method stub

    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

    public void connectToGateway ( String ip, final String password )
    {
        //	print("connecting...");
        showLoadingDialog( true );
//        socketConnectorTimer.start();
//        if ( !loginOk )
//        {
//            if ( clientSocket != null )
//            {
//                clientSocket.disConnectWithServer();
//            }
            loginPassword = password;
            Ip = ip;
//            ClientSocketConnectionTask clientSocketConnectionTask = new ClientSocketConnectionTask();
//            clientSocketConnectionTask.execute( ip, Port, "100" );
//            int count = 0;
//            while ( true )
//            {
                ClientSocket.getInstance().connectWithServer( ip, 4000, 200, new ConnectListener() {
                    @Override
                    public void onConnectSuccess ()
                    {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run ()
                            {
                                Toast.makeText( MainActivity.this, "connect success", Toast.LENGTH_SHORT )
                                     .show();
                            }
                        } );
                        login( password );
                    }

                    @Override
                    public void onConnectFailed ()
                    {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run ()
                            {
                                Toast.makeText( MainActivity.this, "connect failed,", Toast.LENGTH_SHORT )
                                     .show();
                                showLoadingDialog( false );
                            }
                        } );
                        ClientSocket.getInstance().disconnectWithServer();
                    }
                } );
//                if ( ClientSocket.getInstance().isConnected() )
//                {
//                    break;
//                }
//                count++;
//                if ( count > 10 )
//                {
//                    showDialogCanNotConnect( getString( R.string.gateway_error2 ) );
//                    break;
//                }
//            }
//        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
//    private void reconnectToGateway ()
//    {
//        if ( socketConnectorTimerTimeout <= 0 )
//        {
//            socketConnectorTimer.cancel();
//            showDialogCanNotConnect( getString( R.string.gateway_error2 ) );
//        }
//        else
//        {
////            if ( clientSocket != null )
////            {
////                clientSocket.disConnectWithServer();
////            }
////            ClientSocketConnectionTask clientSocketConnectionTask = new ClientSocketConnectionTask();
////            clientSocketConnectionTask.execute( Ip, Port, "100" );
//            ClientSocket.getInstance().connectWithServer( Ip, 4000, 200 );
//            socketConnectorTimer.start();
//        }
//    }
    //-------------------------------------------------------------------------------------------------------------------------------------------

    private void login( String psw )
    {
        String data = new String( new StringBuffer( "login " ).append( psw ) );
        SendSocketData( data, new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                if ( "OK".equals( buffer ) )
                {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run ()
                        {
                            print( "Login Success." );
                        }
                    } );
                    syncTime();
                }
                else
                {
                    ClientSocket.getInstance().disconnectWithServer();
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run ()
                        {
                            print( "Login Failed." );
                            showLoadingDialog( false );
                        }
                    } );
                }
            }

            @Override
            public void onReceiveTimeout ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        print( "Login Failed." );
                        showLoadingDialog( false );
                    }
                } );
            }

            @Override
            public void onReceiveError ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        print( "Login Failed." );
                        showLoadingDialog( false );
                    }
                } );
            }
        } );
        keepAliveTimer.cancel();
//        SendSocketData( "login " + loginPassword );
    }

    private void syncTime()
    {
        String data = new String( new StringBuffer( "time set " ).append( getTime( 0 ) ) );
        SendSocketData( data, new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                if ( "OK".equals( buffer ) )
                {
                    getGatewayInfo();
                }
                else
                {
                    ClientSocket.getInstance().disconnectWithServer();
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run ()
                        {
                            showLoadingDialog( false );
                        }
                    } );
                }
            }

            @Override
            public void onReceiveTimeout ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }

            @Override
            public void onReceiveError ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }
        } );
        keepAliveTimer.cancel();
    }

    private void getGatewayInfo()
    {
        String data = "gateway get info";
        SendSocketData( data, new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                gatewayLoader.getGatewaySettingsData()
                             .parseGetwayGetInfo( buffer );
                getCloud();
            }

            @Override
            public void onReceiveTimeout ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }

            @Override
            public void onReceiveError ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }
        } );
        keepAliveTimer.cancel();
    }

    private void getCloud()
    {
        String data = "cloud get";
        SendSocketData( data, new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                gatewayLoader.getGatewaySettingsData()
                             .setActiveCloudMode( buffer );
                getLightType( 0 );
            }

            @Override
            public void onReceiveTimeout ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }

            @Override
            public void onReceiveError ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }
        } );
        keepAliveTimer.cancel();
    }

    public void getLightType ( final int index )
    {
        if ( index > 1 )
        {
            return;
        }
        String data = "light get type 0" + index;
        SendSocketData( data, new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                gatewayLoader.buildSlotData( index, buffer );
                getLightName( index );
            }

            @Override
            public void onReceiveTimeout ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }

            @Override
            public void onReceiveError ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }
        } );
        keepAliveTimer.cancel();
    }

    private void getLightName ( final int index )
    {
        if ( index > 1 )
        {
            return;
        }
        String data = "light get name 0" + index;
        SendSocketData( data, new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                gatewayLoader.getSlots()[index].setName( buffer );
                if ( gatewayLoader.getSlots()[index].isActive() )
                {
                    getSlotData( index, gatewayLoader.getSendSocketFireCommands() );
                }
                else
                {
                    if ( index > 0 )
                    {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run ()
                            {
                                firstLoadComplete();
                            }
                        } );
                    }
                    else
                    {
                        getLightType( index+1 );
                    }
                }
            }

            @Override
            public void onReceiveTimeout ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }

            @Override
            public void onReceiveError ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }
        } );
        keepAliveTimer.cancel();
    }

    private void getSlotData ( final int index, final ArrayList<String> datas )
    {
        if ( datas == null || datas.size() == 0 || index > 1 )
        {
            return;
        }
        final ArrayList<String> rcvs = new ArrayList<>();
        final TransmitListener listener = new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                rcvs.add( buffer );
                if ( rcvs.size() == datas.size() )
                {
                    gatewayLoader.updateSlotData( index, rcvs );
                    if ( index > 0 )
                    {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run ()
                            {
                                firstLoadComplete();
                            }
                        } );
                    }
                    else
                    {
                        getLightType( index+1 );
                    }
                }
                else
                {
                    SendSocketData( datas.get( rcvs.size() ), this );
                    keepAliveTimer.cancel();
                }
            }

            @Override
            public void onReceiveTimeout ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }

            @Override
            public void onReceiveError ()
            {
                ClientSocket.getInstance().disconnectWithServer();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run ()
                    {
                        showLoadingDialog( false );
                    }
                } );
            }
        };
        SendSocketData( datas.get( 0 ), listener );
        keepAliveTimer.cancel();
//        for ( int i = 0; i < datas.size(); i++ )
//        {
//            SendSocketData( datas.get( i ), listener );
//            //lock
//            bary[0] = true;
//            //wait for unlock
//            while( bary[0] )
//            {
//                //timeout or receive error
//                if ( bary[1] )
//                {
//                    return;
//                }
//            }
//        }
    }

    //Methode Event, wird aufgerufen wenn der Socket Daten empf�ngt
    //Daten String kann aus der data Var ausgelesen werden
//    public void reveiveData ( String data )
//    {
//
////        connectionTimeOutBomb.cancel();
//
//        data = data.replace( "\r\n", "" );
//
//        TcpLogger( "[Incomming]: " + data + "\r\n" );
//
//        if ( !connectionOk )
//        {
//            if ( data.equals( "connected...\n" ) )
//            {
//                connectionOk = true;
//                connected();
//            }
//            else
//            {
//                if ( data.equals( "connection closed...\n" ) )
//                {
//                    connectionOk = false;
//                    disconnected();
//                }
//            }
//        }
//
//        if ( checkPassword && !( loginOk ) )
//        {
//            if ( data.equals( "OK" ) )
//            {
//                print( "Login OK..." );
//                showLoadingDialog( true );
//                print( "loading..." );
//                loginOk = true;
//                checkPassword = false;
//                SendSocketData( "time set " + getTime( 0 ) );
//                //TODO extra timer einf�gen der dann das Sniffen der GateWayDataSettings Vornimmt �ber die SniffGateway function
//                sniffGatewayDataTimer.start();
//            }
//            else
//            {
//                print( "Login Fail..." );
//                isendFromActivity.sendActivityData( null, "login fail" );
//                loginOk = false;
//                checkPassword = false;
//                connectionOk = false;
//                ClientSocket.getInstance().disconnectWithServer();
////                clientSocket.disConnectWithServer();
//                //clientSocket = new ClientSocket();
//                //clientSocketConnectionTask = new ClientSocketConnectionTask();
//
//            }
//        }
//
//        if ( connectionOk && loginOk )
//        {
//            //TODO hier �berpr�fen ob erst ein login vorhanden ist sonnst gibt es porbleme gegebenfalls den send login aus dem startfiresocket rausnehmen und
//            // so implementieren :)
//
//            if ( fireSocketActive )
//            {
//
//                receiveSockeList.add( data );
//
//                if ( fireSocketCommandList.size() < 1 )
//                {
//                    receiveFireSocket( receiveSockeList );
//                }
//                else
//                {
//                    SendSocketData( fireSocketCommandList.get( 0 ) );
//                    fireSocketCommandList.remove( 0 );
//                }
//            }
//        }
//
//        if ( data.equals( "connection closed...\n" ) )
//        {
//            showDiaglogConnectionClose();
//        }
//
//        data = "";
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //Alle Daten wurden aus dem Gateway gelesen geparst nun kann das Light Fragment Anzeigt werden
    private void firstLoadComplete ()
    {

        if ( gatewayLoader.getGatewaySettingsData()
                          .getSlots() == 2 )
        {
            FragmentManager fm = getSupportFragmentManager();
            getSupportFragmentManager().popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
            FragmentTransaction ft = fm.beginTransaction();
            //	ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace( R.id.fragment_root, new FragmentLights(), "FragmentLights" );
            //	ft.addToBackStack(null);
            ft.commit();
            mainMenu.setVisibility( RelativeLayout.VISIBLE ); //Zeige das MainMen�An
            keepAliveTimer.start();
            firstLoadComplete = true;
            showLoadingDialog( false );
        }
        else
        {
            print( "This is not a CON 1 Gateway ...closing... application..." );
            FragmentManager fm = getSupportFragmentManager();
            getSupportFragmentManager().popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
            FragmentTransaction ft = fm.beginTransaction();
            //	ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace( R.id.fragment_root, new FragmentLogin(), "FragmentLogin" );
            //	ft.addToBackStack(null);
            ft.commit();
            mainMenu.setVisibility( RelativeLayout.GONE ); //Zeige das MainMen�An
            keepAliveTimer.cancel();
            loginOk = false;
            checkPassword = false;
            connectionOk = false;
            ClientSocket.getInstance().disconnectWithServer();
//            clientSocket.disConnectWithServer();
            finish();
//            System.exit( 2 );
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //SocketFireScript wurde fertig gestellt
//    private void finishReceiveSocketProcess ()
//    {
//
//        if ( ActiveWorkingSocketProcess.equals( "sniffSlotData(0)" ) )
//        {
//            ActiveWorkingSocketProcess = "sniffSlotData(1)";
//            sniffSlotData( 1 );
//        }
//        else
//        {
//            if ( ActiveWorkingSocketProcess.equals( "sniffSlotData(1)" ) )
//            {
//                //	ActiveWorkingSocketProcess = "sniffSlotData(2)";
//                //sniffSlotData(2);
//                ActiveWorkingSocketProcess = "";
//                firstLoadComplete();
//            }
//            else
//            {
//                if ( ActiveWorkingSocketProcess.equals( "sniffSlotData(2)" ) )
//                {
//                    ActiveWorkingSocketProcess = "";
//                    firstLoadComplete();
//                }
//            }
//        }
//
//        isendFromActivity.sendActivityData( null, "sniffSlotDataFinish" );
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //FireSocket Receive Methode die aufgerufen wird wenn der alle Socket Befehle an den Server geschickt wurden.
//    private void receiveFireSocket ( ArrayList< String > receiveSocketDataList )
//    {
//        fireSocketActive = false;
//
//        if ( sniffSlotDataActive )
//        {
//            sniffSlotData_ReceiveMethode( sniffSlotDataActive_SlotVar, receiveSocketDataList );
//        }
//
//        if ( sniffGatewaySettingsData )
//        {
//            sniffGatewaySettingsData_ReceiveMethode( receiveSocketDataList );
//            ActiveWorkingSocketProcess = "sniffSlotData(0)";
//            sniffSlotData( 0 );
//        }
//
//        isendFromActivity.sendActivityData( null, "firesocketFinish" );
//
//        if ( firstLoadComplete )
//        {
//            keepAliveTimer.start();
//        }
//
//        //Immer am ende dieser Function den Tag Inhalt l�schen...!!!
//        sendFireCommandTag = "";
//        //	receiveSockeList = new ArrayList<String>();
//
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //Hier mit kann man mehrere Befehle in eine Liste packen und an den Socket feuern
//    public void startFireSocket ( ArrayList< String > fireSocketCommands )
//    {
//        if ( !fireSocketActive )
//        {
//            keepAliveTimer.cancel();
//            fireSocketActive = true;
//            fireSocketCommandList = new ArrayList< String >();
//            fireSocketCommandList.addAll( fireSocketCommands );
//            receiveSockeList = new ArrayList< String >();
//            SendSocketData( fireSocketCommandList.get( 0 ) );
//            fireSocketCommandList.remove( 0 );
//        }
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //Snifft Daten von einem Bestimmten Slot und Parst diese direkt
//    private void sniffGatewaySettingsData ()
//    {
//        sniffGatewaySettingsData = true;
//        ArrayList< String > sniffGatewaySettingsDataSendSocketCmd = new ArrayList< String >();
//        sniffGatewaySettingsDataSendSocketCmd.add( "gateway get info" );
//        sniffGatewaySettingsDataSendSocketCmd.add( "cloud get" );
//        //	sniffGatewaySettingsDataSendSocketCmd.add(gatewayLoader.buildGetLightColorSocketCommand(0).get(0));
//        //	sniffGatewaySettingsDataSendSocketCmd.add(gatewayLoader.buildGetLightColorSocketCommand(1).get(0));
//        //	sniffGatewaySettingsDataSendSocketCmd.add(gatewayLoader.buildGetLightColorSocketCommand(2).get(0));
//        startFireSocket( sniffGatewaySettingsDataSendSocketCmd );
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
//    private void sniffGatewaySettingsData_ReceiveMethode ( ArrayList< String > receiveSocketDataList )
//    {
//        gatewayLoader.getGatewaySettingsData()
//                     .parseGetwayGetInfo( receiveSocketDataList.get( 0 ) );
//        if ( gatewayLoader.getGatewaySettingsData()
//                          .getSlots() != 2 )
//        {
//            print( "This is not a CON 1 Gateway ...closing... application..." );
//        }
//
//        gatewayLoader.getGatewaySettingsData()
//                     .setActiveCloudMode( receiveSocketDataList.get( 1 ) ); //TODO kann beim Gateway noch nicht benutzt werden da es dort noch keine Cloud Function gibt
//        //	gatewayLoader.updateRGBColorData(0, receiveSocketDataList.get(1));
//        //	gatewayLoader.updateRGBColorData(1, receiveSocketDataList.get(2));
//        //	gatewayLoader.updateRGBColorData(2, receiveSocketDataList.get(3));
//        Log.d( "MARCEL",
//               gatewayLoader.getGatewaySettingsData()
//                            .toString() );
//        sniffGatewaySettingsData = false;
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

//    public void sniffSlotData ( int slot )
//    {
//        sniffSlotDataActive = true;
//        sniffSlotDataActive_SlotVar = slot;
//        gatewayLoader.createLoadSlotTypeSocketCommands( slot );
//        startFireSocket( gatewayLoader.getSendSocketFireCommands() );
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
//    private void sniffSlotData_ReceiveMethode ( int slot, ArrayList< String > receiveSocketDataList )
//    {
//
//        if ( sniffSlotDataActive )
//        {
//            if ( GatewayLoaderStepsCounter == 0 )
//            {
//                gatewayLoader.buildSlotData( slot, receiveSocketDataList );
//                GatewayLoaderStepsCounter++;
//                if ( gatewayLoader.getSlots()[slot].isActive() )
//                {
//                    startFireSocket( gatewayLoader.getSendSocketFireCommands() );
//                }
//                else
//                {
//                    GatewayLoaderStepsCounter = 0;
//                    sniffSlotDataActive = false;
//                    sniffSlotDataActive_SlotVar = 0;
//                    finishReceiveSocketProcess();
//                }
//            }
//            else
//            {
//                if ( GatewayLoaderStepsCounter == 1 )
//                {
//                    gatewayLoader.updateSlotData( slot, receiveSocketDataList );
//                    GatewayLoaderStepsCounter = 0;
//                    sniffSlotDataActive = false;
//                    sniffSlotDataActive_SlotVar = 0;
//                    finishReceiveSocketProcess();
//                }
//            }
//        }
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //App ist mit Gateway verbunden
//    private void connected ()
//    {
//
//        //print("connected...");
////        socketConnectorTimer.cancel();
//        loginTimer.start();
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //Die Verbindung zum Gateway wurde unterbrochen
//    private void disconnected ()
//    {
//        print( "disconnected..." );
//        loginOk = false;
//        checkPassword = false;
//        ClientSocket.getInstance().disconnectWithServer();
////        clientSocket.disConnectWithServer();
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //Hier befindet sich der Asynchrone Client Connection Task
    //Von hier aus wird die ganze ClientSocket Klasse instanziert und auch gesteuert
//    private class ClientSocketConnectionTask extends AsyncTask< String, String, Void >
//    {
//
//        private String data;
//
//        @Override
//        protected void onPreExecute ()
//        {
//            super.onPreExecute();
////            clientSocket = new ClientSocket();
//        }
//
//        @Override
//        protected Void doInBackground ( String... params )
//        {
//
//            publishProgress( "connecting...\n" );
//
////            clientSocket.disConnectWithServer();
//
////            clientSocket.connectWithServer( params[0], Integer.parseInt( params[1] ), Integer.parseInt( params[2] ) );
//
//            if ( clientSocket.getSocket()
//                             .isConnected() )
//            {
//                publishProgress( "connected...\n" );
//                publishProgress( "start data listener...\n" );
//                while ( true )
//                {
//                    data = "";
//                    data = clientSocket.receiveDataFromServer();
//                    if ("error".equals( data ) || "".equals( data ) )
//                    {
//                        clientSocket.disConnectWithServer();
//                        break;
//                    }
//                    else
//                    {
//                        publishProgress( data );
//                    }
//                }
//                publishProgress( "connection closed...\n" );
//            }
//            else
//            {
//                publishProgress( "connection fail...\n" );
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate ( String... values )
//        {
//            reveiveData( values[0] );
//            super.onProgressUpdate( values );
//        }
//
//        @Override
//        protected void onPostExecute ( Void result )
//        {
//            clientSocket = null;
//            super.onPostExecute( result );
//        }
//    }
    //-------------------------------------------------------------------------------------------------------------------------------------------

    //Checkt ob das Wlan Modul eingeschaltet ist
    public boolean checkConnectionWifi ()
    {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
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

    //Snifft die Verbindungsip und liefert diese zur�ck
    //Das letze Octett wird mit einer 1 belegt um an die Gateway Adresse des Accesspoints  zu kommen
    //-------------------------------------------------------------------------------------------------------------------------------------------
    public String getIpAddr ( String lastOctett )
    {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService( WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        String ipString = String.format( "%d.%d.%d." + lastOctett, ( ip & 0xff ), ( ip >> 8 & 0xff ), ( ip >> 16 & 0xff ), ( ip >> 24 & 0xff ) );

        return ipString;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    public void SendSocketData ( final String data, TransmitListener listener )
    {
        keepAliveTimer.cancel();
        ClientSocket.getInstance().sendDataWithString( data, true, listener );
        keepAliveTimer.start();
//        connectionTimeOutBomb.start();

//        if ( clientSocket != null )
//        {
//            //Hier werden Daten an den Socket gesendet (String,Boolean)  �ber das Boolean
//            //kann man einstelle ob ein (CR) mitgesendet werden soll oder nicht
////            try
////            {
////                Thread.sleep( 40 );
////            }
////            catch ( InterruptedException e )
////            {
////                e.printStackTrace();
////            }
//            TcpLogger( "[Send]: " + data + "\r\n" );
//            keepAliveTimer.cancel();
////            clientSocket.sendDataWithString( data, true );
////            connectionTimeOutBomb.start();
//            keepAliveTimer.start();
//        }
    }

    public void sendDataArray ( final ArrayList<String> datas )
    {
        if ( datas == null || datas.size() == 0 )
        {
            return;
        }
        final ArrayList<String> rcvs = new ArrayList<>();
        TransmitListener listener = new TransmitListener() {
            @Override
            public void onReceive ( String buffer )
            {
                rcvs.add( buffer );
                if ( rcvs.size() == datas.size() )
                {

                }
                else
                {
                    ClientSocket.getInstance().sendDataWithString( datas.get( rcvs.size() ), true, this );
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
        keepAliveTimer.cancel();
        ClientSocket.getInstance().sendDataWithString( datas.get( 0 ), true, listener );
        keepAliveTimer.start();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //Keep Alive Timer sendet in einem bestimmten Interval ein paar TestDaten an den Server
    //damit dieser merkt das noch aktivit�ten vorhanden sind
    private class KeepAliveTimer extends CountDownTimer
    {

        public KeepAliveTimer ( long millisInFuture, long countDownInterval )
        {
            super( millisInFuture, countDownInterval );
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick ( long millisUntilFinished )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish ()
        {

            SendSocketData( "gateway get info", null );
//            keepAliveTimer.start();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    public void print ( String text )
    {
        Toast.makeText( this, text, Toast.LENGTH_SHORT )
             .show();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
//    private class LoginTimer extends CountDownTimer
//    {
//
//        public LoginTimer ( long millisInFuture, long countDownInterval )
//        {
//            super( millisInFuture, countDownInterval );
//            // TODO Auto-generated constructor stub
//        }
//
//        @Override
//        public void onTick ( long millisUntilFinished )
//        {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onFinish ()
//        {
//            checkPassword = true;
//            SendSocketData( "login " + loginPassword );
//        }
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
//    private class SniffGatewayDataTimer extends CountDownTimer
//    {
//
//        public SniffGatewayDataTimer ( long millisInFuture, long countDownInterval )
//        {
//            super( millisInFuture, countDownInterval );
//            // TODO Auto-generated constructor stub
//        }
//
//        @Override
//        public void onTick ( long millisUntilFinished )
//        {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onFinish ()
//        {
//            sniffGatewaySettingsData();
//        }
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick ( View button )
    {
        FragmentManager fm = getSupportFragmentManager();
        getSupportFragmentManager().popBackStack( null, FragmentManager.POP_BACK_STACK_INCLUSIVE );
        FragmentTransaction ft = fm.beginTransaction();

        switch ( button.getId() )
        {
            case R.id.imageLights:

                if ( MenuLightSettingSwitcher )
                {
                    imgLights.setImageDrawable( getResources().getDrawable( R.drawable.sun_clicked ) );
                    imgSettings.setImageDrawable( getResources().getDrawable( R.drawable.wrench ) );
                    //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    ft.replace( R.id.fragment_root, new FragmentLights(), "FragmentLights" );
                    //ft.addToBackStack(null);
                    ft.commit();
                    displayBackStack();
                    MenuLightSettingSwitcher = false;
                }
                break;

            case R.id.imageSettings:

                if ( !MenuLightSettingSwitcher )
                {
                    imgSettings.setImageDrawable( getResources().getDrawable( R.drawable.wrench_clicked ) );
                    imgLights.setImageDrawable( getResources().getDrawable( R.drawable.sun ) );
                    //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    ft.replace( R.id.fragment_root, new FragmentSettings(), "FragmentSettings" );
                    //ft.addToBackStack(null);
                    ft.commit();
                    displayBackStack();
                    MenuLightSettingSwitcher = true;
                }
                break;

            case R.id.button_lights_menu:

                if ( MenuLightSettingSwitcher )
                {
                    imgLights.setImageDrawable( getResources().getDrawable( R.drawable.sun_clicked ) );
                    imgSettings.setImageDrawable( getResources().getDrawable( R.drawable.wrench ) );
                    //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    ft.replace( R.id.fragment_root, new FragmentLights(), "FragmentLights" );
                    //ft.addToBackStack(null);
                    ft.commit();
                    displayBackStack();
                    MenuLightSettingSwitcher = false;
                }
                break;

            case R.id.button_settings_menu:

                if ( !MenuLightSettingSwitcher )
                {
                    imgSettings.setImageDrawable( getResources().getDrawable( R.drawable.wrench_clicked ) );
                    imgLights.setImageDrawable( getResources().getDrawable( R.drawable.sun ) );
                    //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    ft.replace( R.id.fragment_root, new FragmentSettings(), "FragmentSettings" );
                    //ft.addToBackStack(null);
                    ft.commit();
                    displayBackStack();
                    MenuLightSettingSwitcher = true;
                }
                break;
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    public void displayBackStack ()
    {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        Log.d( "MARCEL_Backstack log", "There are " + count + " entries" );
        for ( int i = 0; i < count; i++ )
        {
            // Display Backstack-entry data like
            String name = fm.getBackStackEntryAt( i )
                            .getName();
            Log.d( "MARCEL_Backstack log", "entry " + i + ": " + name );
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

    private boolean isGerman ()
    {
        if ( java.util.Locale.getDefault()
                             .getDisplayName()
                             .substring( 0, 7 )
                             .equals( "Deutsch" ) )
        {
            return true;
        }
        return false;
    }

    private String getTime ( int timeStyle )
    {
        SimpleDateFormat df = new SimpleDateFormat();

        switch ( timeStyle )
        {
            case 0:
                df = new SimpleDateFormat( "HH:mm" );
                break;
            case 1:
                if ( isGerman )
                {
                    df = new SimpleDateFormat( "HH:mm" );
                }
                else
                {
                    df = new SimpleDateFormat( "HH:mm a" );
                }
                break;
        }

        String date = df.format( Calendar.getInstance()
                                         .getTime() );
        return date;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

    private class UpdateTimeTimer extends CountDownTimer
    {

        public UpdateTimeTimer ( long millisInFuture, long countDownInterval )
        {
            super( millisInFuture, countDownInterval );
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onTick ( long millisUntilFinished )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish ()
        {

            textTime.setText( getTime( 1 ) );
            updateTimeTimer.start();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onPause ()
    {
//        if ( clientSocket != null && clientSocket.mSocket.isConnected() )
//        {
//            clientSocket.disConnectWithServer();
//        }
        super.onPause();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

    private void TcpLogger ( String text )
    {

        Log.d( "MARCEL_TCP_LOG", text );

        if ( log_active )
        {
            appendLog( text );
        }

        tcpLog += text;

        if ( tcplogActive )
        {
            isendFromActivity.sendActivityData( null, null );
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    private BroadcastReceiver WifiStateChangedReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive ( Context context, Intent intent )
        {
            // TODO Auto-generated method stub

            int extraWifiState = intent.getIntExtra( WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN );

            switch ( extraWifiState )
            {
                case WifiManager.WIFI_STATE_DISABLED:
                    TcpLogger( "WIFI STATE DISABLED\n" );
                    TcpLogger( "------------------" + "\n" );
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    TcpLogger( "WIFI STATE DISABLING\n" );
                    TcpLogger( "------------------" + "\n" );
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    TcpLogger( "WIFI STATE ENABLED\n" );
                    TcpLogger( "------------------" + "\n" );
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    TcpLogger( "WIFI STATE ENABLING\n" );
                    TcpLogger( "------------------" + "\n" );
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    TcpLogger( "WIFI STATE UNKNOWN\n" );
                    TcpLogger( "------------------" + "\n" );
                    break;
            }
        }
    };

    //-----------------------------------------------------------------------------------------------------------------------------------------

    private void DisplayWifiState ()
    {

        ConnectivityManager myConnManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo myNetworkInfo = myConnManager.getNetworkInfo( ConnectivityManager.TYPE_WIFI );
        WifiManager myWifiManager = (WifiManager) getApplicationContext().getSystemService( Context.WIFI_SERVICE );
        WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();

        TcpLogger( "WifiConnection Info" + "\n" );
        TcpLogger( "--- CONNECTED ---" + "\n" );
        TcpLogger( "------------------" + "\n" );

        if ( myNetworkInfo.isConnected() )
        {

            TcpLogger( "MAC:" + myWifiInfo.getMacAddress() + "\n" );

            int myIp = myWifiInfo.getIpAddress();

            TcpLogger( "IP:" + String.format( "%d.%d.%d.%d", ( myIp & 0xff ), ( myIp >> 8 & 0xff ), ( myIp >> 16 & 0xff ), ( myIp >> 24 & 0xff ) ) + "\n" );

            TcpLogger( "Ssid: " + myWifiInfo.getSSID() + " \n" );
            TcpLogger( "Bssid " + myWifiInfo.getBSSID() + "\n" );

            TcpLogger( "Speed: " + String.valueOf( myWifiInfo.getLinkSpeed() ) + " " + WifiInfo.LINK_SPEED_UNITS + "\n" );
            TcpLogger( "Rssi: " + String.valueOf( myWifiInfo.getRssi() ) + "\n" );
            TcpLogger( "------------------" + "\n" );
        }
        else
        {
            TcpLogger( "WifiConnection Info" + "\n" );
            TcpLogger( "------------------" + "\n" );
            TcpLogger( "--- DIS-CONNECTED! ---" + "\n" );
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private BroadcastReceiver myWifiReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive ( Context arg0, Intent arg1 )
        {
            // TODO Auto-generated method stub
            NetworkInfo networkInfo = (NetworkInfo) arg1.getParcelableExtra( ConnectivityManager.EXTRA_NETWORK_INFO );
            if ( networkInfo.getType() == ConnectivityManager.TYPE_WIFI )
            {
                // DisplayWifiState();
                DisplayWifiState();
            }
        }
    };

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getDeviceSuperInfo ()
    {
        TcpLogger( "--------------------------\n" );
        TcpLogger( "getDeviceSuperInfo\n" );

        try
        {

            String s = "Debug-infos:";
            s += "\n OS Version: " + System.getProperty( "os.version" ) + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
            s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
            s += "\n Device: " + android.os.Build.DEVICE;
            s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

            s += "\n RELEASE: " + android.os.Build.VERSION.RELEASE;
            s += "\n BRAND: " + android.os.Build.BRAND;
            s += "\n DISPLAY: " + android.os.Build.DISPLAY;
            s += "\n CPU_ABI: " + android.os.Build.CPU_ABI;
            s += "\n CPU_ABI2: " + android.os.Build.CPU_ABI2;
            s += "\n UNKNOWN: " + android.os.Build.UNKNOWN;
            s += "\n HARDWARE: " + android.os.Build.HARDWARE;
            s += "\n Build ID: " + android.os.Build.ID;
            s += "\n MANUFACTURER: " + android.os.Build.MANUFACTURER;
            s += "\n SERIAL: " + android.os.Build.SERIAL;
            s += "\n USER: " + android.os.Build.USER;
            s += "\n HOST: " + android.os.Build.HOST;

            TcpLogger( s );

            TcpLogger( "--------------------------\n" );
        }
        catch ( Exception e )
        {

        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    public void appendLog ( String text )
    {

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File( sdCard.getAbsolutePath() );
        File logFile = new File( dir + "/fluval_log.txt" );
        if ( !logFile.exists() )
        {
            try
            {
                logFile.createNewFile();
            }
            catch ( IOException e )
            {
                Log.d( "MARCEL_LOG", "Cant write file..." );
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter( new FileWriter( logFile, true ) );
            buf.append( text );
            buf.newLine();
            buf.close();
            Log.d( "MARCEL_LOG", "file write..." );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    private void showDiaglogConnectionClose ()
    {

//        if ( clientSocket != null )
//        {
//            clientSocket.disConnectWithServer();
//        }

        AlertDialog alertDialog = new AlertDialog.Builder( MainActivity.this ).create();
        alertDialog.setTitle( "Error" );
        alertDialog.setMessage( getString( R.string.gateway_error ) );
        alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
        {
            public void onClick ( DialogInterface dialog, int which )
            {

//                System.exit( 2 );
                // restart(100);

                dialog.dismiss();
//                finish();
            }
        } );
        alertDialog.show();
    }

    @Override
    protected void onResume ()
    {

//        if ( resumeExit )
//        {
////            if ( clientSocket != null )
////            {
////                clientSocket.disConnectWithServer();
////            }
//            finish();
////            System.exit( 2 );
//        }
//        resumeExit = true;
        super.onResume();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

//    private class ConnectionTimeOutBomb extends CountDownTimer
//    {
//
//        public ConnectionTimeOutBomb ( long millisInFuture, long countDownInterval )
//        {
//            super( millisInFuture, countDownInterval );
//        }
//
//        @Override
//        public void onTick ( long millisUntilFinished )
//        {
//        }
//
//        @Override
//        public void onFinish ()
//        {
//            Log.d( "ConnectionTimeOutBomb", "onFinish: " );
//            showDiaglogConnectionClose();
//        }
//    }
    //-----------------------------------------------------------------------------------------------------------------------------------------

    public void restart ( int delay )
    {
        PendingIntent intent = PendingIntent.getActivity( this.getBaseContext(), 0, new Intent( getIntent() ), PendingIntent.FLAG_UPDATE_CURRENT );
        AlarmManager manager = (AlarmManager) this.getSystemService( Context.ALARM_SERVICE );
        manager.set( AlarmManager.RTC, System.currentTimeMillis() + delay, intent );
//        System.exit( 2 );
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    public void showLoadingDialog ( boolean active )
    {

        if ( active )
        {
            loadingDialog.setMessage( "please wait..." );
            loadingDialog.setCancelable( false );
            loadingDialog.setCanceledOnTouchOutside( false );
            loadingDialog.show();
        }
        else
        {
            loadingDialog.dismiss();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    public class MyLifecycleHandler implements ActivityLifecycleCallbacks
    {
        // I use two separate variables here. You can, of course, just use one and
        // increment/decrement it instead of using two and incrementing both.
        private int resumed;
        private int stopped;

        @Override
        public void onActivityCreated ( Activity activity, Bundle savedInstanceState )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onActivityStarted ( Activity activity )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onActivityResumed ( Activity activity )
        {
//            if ( clientSocket != null )
//            {
//                clientSocket.disConnectWithServer();
//            }
            //	 System.exit(2);

        }

        @Override
        public void onActivityPaused ( Activity activity )
        {
//            if ( clientSocket != null )
//            {
//                clientSocket.disConnectWithServer();
//            }
//            System.exit( 2 );
        }

        @Override
        public void onActivityStopped ( Activity activity )
        {
//            if ( clientSocket != null )
//            {
//                clientSocket.disConnectWithServer();
//            }
//            System.exit( 2 );
        }

        @Override
        public void onActivitySaveInstanceState ( Activity activity, Bundle outState )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onActivityDestroyed ( Activity activity )
        {
//            if ( clientSocket != null )
//            {
//                clientSocket.disConnectWithServer();
//            }
//            System.exit( 2 );
        }
    }

    @Override
    protected void onStop ()
    {
        super.onStop();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy ()
    {
//        if ( clientSocket != null )
//        {
//            clientSocket.disConnectWithServer();
//        }
        ClientSocket.getInstance().disconnectWithServer();
        this.unregisterReceiver( this.WifiStateChangedReceiver );

        this.unregisterReceiver( this.myWifiReceiver );
//        System.exit( 2 );
        super.onDestroy();
        getApplication().unregisterActivityLifecycleCallbacks( mCallbacks );
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------

//    private class SocketConnectorTimer extends CountDownTimer
//    {
//
//        public SocketConnectorTimer ( long millisInFuture, long countDownInterval )
//        {
//            super( millisInFuture, countDownInterval );
//            // TODO Auto-generated constructor stub
//        }
//
//        @Override
//        public void onTick ( long millisUntilFinished )
//        {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onFinish ()
//        {
//
//            if ( !loginOk )
//            {
//                loginOk = false;
//                checkPassword = false;
//                connectionOk = false;
//                //	if (clientSocket != null)
//                //	{
//                //		clientSocket.disConnectWithServer();
//                //	}
//                if ( clientSocketConnectionTask != null )
//                {
//                    clientSocketConnectionTask.cancel( true );
//                }
//
//                socketConnectorTimerTimeout--;
//
//                reconnectToGateway();
//                Log.d( "MARCEL_SOCKETTIMEOUT", "Timeout: " + socketConnectorTimerTimeout );
//            }
//        }
//    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void showDialogCanNotConnect ( String text )
    {
//        socketConnectorTimer.cancel();
        AlertDialog alertDialog = new AlertDialog.Builder( this ).create();
        alertDialog.setTitle( "Error" );
        alertDialog.setMessage( text );
        alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
        {
            public void onClick ( DialogInterface dialog, int which )
            {
//                System.exit( 2 );
                dialog.dismiss();
//                finish();
            }
        } );
        alertDialog.show();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
}



