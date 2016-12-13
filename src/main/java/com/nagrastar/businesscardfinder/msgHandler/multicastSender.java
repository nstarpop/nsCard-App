package com.nagrastar.businesscardfinder.msgHandler;
import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.MulticastSocket;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.content.Context;

import com.nagrastar.businesscardfinder.businesscard.BusinessCard;
import com.nagrastar.businesscardfinder.businesscard.BusinessCardDBHandler;


/**
 * 
 * @author pop.wang
 *   send msg to multicast group with a give interval 
 */
public class multicastSender extends Thread {

    private MulticastSocket socket = null;
    private DatagramPacket outPacket = null;
    byte[] outBuf;
    private String gSSDPAddress = "239.255.255.250";
    private int gSSDPPort = 1900;
    private BusinessCardDBHandler dbhandler = null;

    protected DatagramSocket dgsocket = null;
    private boolean mRunning = true;

    //String multicastMsg = "M-Cards * HTTP/1.1;Pop,7204002535,pop.wang@nagrastar.com,corporation;Chao, 7201233333, test@gmail.com, persona";

    public multicastSender(BusinessCardDBHandler mBusinessCardDBHandler)
    {
       super();
        dbhandler = mBusinessCardDBHandler;
        try
        {
          dgsocket = new DatagramSocket(4445);
        }
        catch (IOException  e)
        {
            e.printStackTrace();
        }
    }

    public void quit() {
        socket.close();
        mRunning = false;
    }

    public void run()
    {
        try {
            socket = new MulticastSocket(gSSDPPort);
            InetAddress group = InetAddress.getByName(gSSDPAddress);
            socket.joinGroup(group);



        }catch (IOException  e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while(mRunning)
        {
          // call send() to broad cast msg to group address every interval
            //String sendContent = "sample";

            //TODO: get business card info from the stored data
            BusinessCard[] cards = dbhandler.getDownloadByType("SELF");
            if(cards == null)
            {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            String multicastMsg = "M-Cards * HTTP/1.1;";

            for(int i = 0;i< cards.length;i++)
            {
                BusinessCard curCard = cards[i];

                multicastMsg += curCard.getName();
                multicastMsg += ",";

                multicastMsg += curCard.getPhone();
                multicastMsg += ",";

                multicastMsg += curCard.getJobTitle();
                multicastMsg += ",";

                multicastMsg += curCard.getEmail();
                multicastMsg += ",";

                multicastMsg += curCard.getAddress();
                multicastMsg += ",";

                multicastMsg += curCard.getCompany();
                multicastMsg += ",";

                multicastMsg += "OTHER";
                multicastMsg += ",";

                multicastMsg += curCard.getmTag();
                multicastMsg += ";";
            }

            send(multicastMsg, gSSDPAddress, gSSDPPort);
            send2();
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void send2()
    {
        //socket.send(packet);
    }

    public void send(String responseMsg, String host, int port)
    {
       //Send to multicast IP address and port
       InetAddress address = null;
	   try {
		address = InetAddress.getByName(host);
	   } catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       outPacket = new DatagramPacket(responseMsg.getBytes(), responseMsg.getBytes().length, address, port);

        try {
		//socket.send(outPacket);
            dgsocket.send(outPacket);
           //socket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
        System.out.println("Server sends : " + responseMsg);
    }
}
