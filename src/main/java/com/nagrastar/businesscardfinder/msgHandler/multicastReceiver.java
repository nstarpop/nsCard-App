package com.nagrastar.businesscardfinder.msgHandler;
import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import com.nagrastar.businesscardfinder.MainActivity;
import com.nagrastar.businesscardfinder.util.NMPLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import com.nagrastar.businesscardfinder.businesscard.BusinessCard;
import com.nagrastar.businesscardfinder.businesscard.BusinessCardDBHandler;
import com.nagrastar.businesscardfinder.util.NMPLog;
import android.net.wifi.WifiManager;


import java.lang.reflect.Field;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

/**
 * 
 * @author pop.wang
 *  1. receive multicast msg from other device
 *  2. send response to other device
 */
public class multicastReceiver extends Thread {
	private static final String TAG = "multicastReceiver";

	private MulticastSocket socket = null;
	private DatagramPacket inPacket = null;
	private DatagramSocket responseSocket = null;
	private MainActivity.IOnReceviedBussinessCard mReceiveListener = null;
	private Context context = null;
	private boolean mRunning = true;
	byte[] inBuf = new byte[256];
	
	private String gSSDPAddress = "239.255.255.250";
	private int gSSDPPort = 1900;
	
	public multicastReceiver(Context xContext, MainActivity.IOnReceviedBussinessCard receiveListener)
	{
		mReceiveListener = receiveListener;
		context = xContext;
	  try {
	      //Prepare to join multicast group
		  responseSocket  = new DatagramSocket();
	      socket = new MulticastSocket(gSSDPPort);
	      InetAddress address = InetAddress.getByName(gSSDPAddress);
	      socket.joinGroup(address);
	    } catch (IOException ioe) {
	      System.out.println(ioe);
	    }
	}

	public void quit() {
		socket.close();
		mRunning = false;
	}

	public void run()
	{
		inPacket = new DatagramPacket(inBuf, inBuf.length);
		while (mRunning)
		{
			try {
				socket.receive(inPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String msg = new String(inBuf, 0, inPacket.getLength());

			Log.d("multicastReceiver", "From " + inPacket.getAddress() + " Msg : " + msg);

			//filter out packet from itself

			String SenderIP =  inPacket.getAddress().toString();
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			String ReceiverIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
			ReceiverIP = "/" + ReceiverIP;
			if(ReceiverIP.equals(SenderIP))
			{
				continue;
			}

			NMPLog.v("multicastReceiver", msg);
			String[] ss = msg.split(";");
			// TODO 1, if receive cards info, store it
			String header = ss[0];
			if(header.contains("M-Cards"))
			{
				System.out.println("Received Card msg: " + msg);
				for(int i = 1; i< ss.length; i++)
				{
					NMPLog.d(TAG, "Card received:  "+ss[i]);
					String currentCard = ss[i];
					String[] cardDetails = currentCard.split(",");
					if (cardDetails.length < 8)
						continue;

					String Name    = cardDetails[0];
					String Phone = cardDetails[1];
					String Jobtitle   = cardDetails[2];
					String email   = cardDetails[3];
					String address   = cardDetails[4];
					String company   = cardDetails[5];
					String type   = "OTHERS";
					String tag   = cardDetails[7];

					//TODO, store the Cards info to DB
					BusinessCard card = new BusinessCard(Name,Jobtitle, Phone, email, address,company, type,tag );
					mReceiveListener.onOnReceviedBusinessCard(card);
				}
			}
		}
	}

    public void response(byte[] responseMsg, String host, int port) throws IOException 
    {  
        System.out.println("sender ip: " + host  
                + ", sender port:" + port);
        InetAddress address = InetAddress.getByName(host);
        DatagramPacket outPacket = new DatagramPacket(responseMsg, responseMsg.length, address, port);
        responseSocket.send(outPacket);  
    } 
	
}
