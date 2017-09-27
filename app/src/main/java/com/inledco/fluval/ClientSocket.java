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

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ClientSocket
{
    private static final String TAG = "ClientSocket";

    public static final int BUFFER_SIZE = 1024;
    public Socket mSocket = null;
//    private BufferedOutputStream out = null;
//    private BufferedInputStream in = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
//    private InetSocketAddress inetAddress;
    private char[] mRxBuffer;
    private boolean mConnected;
//    private ConnectListener mConnectListener;
//    private TransmitListener mTransmitListener;

    public static ClientSocket getInstance()
    {
        return ClientHolder.mInstance;
    }

    private ClientSocket ()
    {
        mSocket = new Socket();
        mRxBuffer = new char[BUFFER_SIZE];
    }

    public boolean isConnected ()
    {
        return mConnected;
    }

    public void connectWithServer ( final String ip, final int port, final int timeout, final ConnectListener listener )
    {
        if ( mConnected )
        {
            return;
        }
        Log.d( TAG, "connectWithServer: " + ip + "\t" + port );
        // mSocket = new Socket();
        new Thread( new Runnable() {
            @Override
            public void run ()
            {
                try
                {
                    mSocket.setTcpNoDelay( true );
                    mSocket.setSendBufferSize( BUFFER_SIZE );
                    mSocket.setReceiveBufferSize( BUFFER_SIZE );
                    Log.i("ip,port,timeout",ip + "," + port + "," + timeout);
                    mSocket.connect( new InetSocketAddress( ip, port ), timeout );

                    mConnected = mSocket.isConnected();
                    if (mConnected){
                        Thread.sleep(50);
                        out = new PrintWriter( mSocket.getOutputStream() );
                        in = new BufferedReader( new InputStreamReader( mSocket.getInputStream() ) );
                    }
                }
                catch ( IOException e)
                {
                    e.printStackTrace();
                    mConnected = false;
                }
                catch (InterruptedException e){

                }
                finally
                {
                    if ( listener != null )
                    {
                        if ( mConnected )
                        {
                            listener.onConnectSuccess();
                        }
                        else
                        {
                            listener.onConnectFailed();
                        }
                    }
                }
            }
        } ).start();
    }

    public void disconnectWithServer()
    {
//        if ( mConnected )
//        {
        new Thread( new Runnable() {
            @Override
            public void run ()
            {
                try
                {
                    if ( in != null )
                    {
                        in.close();
                        in = null;
                    }
                    if ( out != null )
                    {
                        out.close();
                        out = null;
                    }
                    if ( mSocket != null )
                    {
                        mSocket.close();
                        mSocket = null;
                    }
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
                mConnected = false;
            }
        } ).start();

//        }
    }

    public synchronized void sendDataWithString ( String data, boolean cr, final TransmitListener listener )
    {
        if ( mConnected )
        {
            Log.i("已经连接！","已经连接！");
            if ( TextUtils.isEmpty( data ) )
            {
                return;
            }

            if ( cr )
            {
                data = new String( new StringBuffer( data ).append( "\r\n" ) );
            }
            final String d = data;
            new Thread( new Runnable() {
                @Override
                public void run ()
                {
                    try
                    {
                        Log.i("线程启动","进入线程");
                        if (mConnected){
                            Log.i("socket连接","进入线程");
                        }else{
                            Log.i("socket没有连接","进入线程");
                        }

                        out.write( d );
                        out.flush();
                        if (out.checkError()){
                            Log.i("检测out状态","有错误");
                        }else {
                            Log.i("检测out状态","无错误");
                        }

                        Log.i("线程启动","写完数据:" + d + d.length());

                        int len = in.read(mRxBuffer);
                        Log.i("线程启动","读取完数据长度:" + len);
                        if ( listener  != null )
                        {
                            Log.i("已经连接！","listener不是空！");
                            if ( len > 0 )
                            {
                                String rcv = new String( mRxBuffer, 0, len );
                                Log.d( TAG, "Send: " + d + "\r\nReceive: " + rcv );
                                if ( rcv.endsWith( "\r\n" ) )
                                {
                                    rcv = rcv.substring( 0, rcv.length()-2 );
                                }
                                Log.i("线程启动","读取完数据:" + rcv);
                                listener.onReceive( rcv );
                            }
                            else
                            {
                                listener.onReceiveTimeout();
                            }
                        }else{
                            Log.i("listener是空！","listener是空！");
                        }
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                        if ( listener  != null )
                        {
                            listener.onReceiveError();
                        }
                    }
//                    finally
//                    {
//                        try
//                        {
//                            in.close();
//                            out.close();
//                        }
//                        catch ( IOException e )
//                        {
//                            e.printStackTrace();
//                        }
//                    }
                }
            } ).start();
        }
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
                    sendDataWithString( datas.get( rcvs.size() ), true, this );
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
        sendDataWithString( datas.get( 0 ), true, listener );
    }

//    public boolean connectWithServer ( String Ip, int Port, int timeout )
//    {
//        try
//        {
////            if ( mSocket == null )
////            {
//            InetSocketAddress inetAddress = new InetSocketAddress( Ip, Port );
//                mSocket = new Socket();
//                mSocket.setTcpNoDelay( true );
//                mSocket.setReceiveBufferSize( BUFFER_SIZE );
//                mSocket.setSendBufferSize( BUFFER_SIZE );
//                mSocket.connect( inetAddress, timeout );
////                out = new BufferedOutputStream( mSocket.getOutputStream() );
////                in = new BufferedInputStream( mSocket.getInputStream() );
//                out = new PrintWriter( mSocket.getOutputStream() );
//                in = new BufferedReader( new InputStreamReader( mSocket.getInputStream() ) );
//                if ( mSocket.isConnected() )
//                {
//                    return true;
//                }
////            }
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    public boolean disConnectWithServer ()
//    {
//        if ( mSocket != null && mSocket.isConnected() && !mSocket.isClosed() )
//        {
//            try
//            {
//                in.close();
//                out.close();
//                mSocket.close();
//                in = null;
//                out = null;
//                mSocket = null;
//                return true;
//            }
//            catch ( IOException e )
//            {
//                e.printStackTrace();
//            }
//        }
//
//        return false;
//    }

//    public String receiveDataFromServer ()
//    {
//        if ( mSocket != null && mSocket.isConnected() && !mSocket.isClosed() )
//        {
//            try
//            {
//                char[] buffer = new char[BUFFER_SIZE];
////                byte[] buffer = new byte[BUFFER_SIZE];
//                int len = in.read( buffer );
//                String data = new String( buffer, 0, len );
//                Log.d( "MARCEL_CONNECTION", "RECEIVEDATA:" + "[" + data + "]" );
//                return data;
//            }
//            catch ( Exception e )
//            {
////                Log.d( "Exception", "receiveDataFromServer: " + e.toString() );
//                return "error";
////                return e.toString();
//            }
//        }
//
//        return "";
//    }

//    public void sendDataWithString ( String data, boolean cr )
//    {
//        if ( mSocket != null && mSocket.isConnected() && !mSocket.isClosed() && data != null )
//        {
//            if ( cr )
//            {
//                data = new String( new StringBuffer( data ).append( "\r\n" ) );
//            }
//            final String d = data;
////            final byte[] d = new byte[data.length()];
////            for ( int i = 0; i < d.length; i++ )
////            {
////                d[i] = (byte) data.charAt( i );
////            }
//            new Thread( new Runnable() {
//                @Override
//                public void run ()
//                {
//                    out.write( d );
//                    out.flush();
////                    try
////                    {
////                        out.write( d );
////                        out.flush();
////                    }
////                    catch ( IOException e )
////                    {
////                        e.printStackTrace();
////                    }
//                    Log.d( "MARCEL_CONNECTION", "SENDDATA:" + "[" + d + "]" );
//                }
//            } ).start();
//        }
//    }

//    public Socket getSocket ()
//    {
//        return mSocket;
//    }
//
//    public void setSocket ( Socket socket )
//    {
//        this.mSocket = socket;
//    }

    private static class ClientHolder
    {
        private static ClientSocket mInstance = new ClientSocket();
    }
}
