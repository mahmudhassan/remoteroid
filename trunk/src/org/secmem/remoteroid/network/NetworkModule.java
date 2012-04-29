package org.secmem.remoteroid.network;

import java.io.*;
import java.net.*;

import android.util.*;

public class NetworkModule {
	private static NetworkModule netModInstance = null;
	private Socket socket = null;
	private String strIP = null;
	private int iPortNum = CONS.HOST.PORT;
	OutputStream out = null;
	InputStream in = null;
	packetmakeable packetMaker = null;
	iFileSendable fileSender = null;
	PacketReceiver packetReceiver = null;
	
	private NetworkModule(){
		socket = new Socket();
	}
	
	
	public static NetworkModule getInstance(){
		if(netModInstance == null)
			netModInstance = new NetworkModule();
		return netModInstance;
	}
	
	/**
	 * 
	 * @param strIP
	 * @param iPortNum
	 * @throws IOException			(알 수 없는 오류)
	 * @throws UnknownHostException  (IP, PORT번호 확인 요망)
	 * ip와 port번호를 넘겨주면 소켓 연결을 하고 바이트스트림을 얻는다.
	 */
	public void ConnectSocket(String strIP) throws IOException, UnknownHostException{
		this.strIP = strIP;		
		
		socket.connect(new InetSocketAddress(strIP, iPortNum));
		
		out = socket.getOutputStream();
		in = socket.getInputStream();
		
		packetMaker = new PacketMaker(out);
		fileSender = new FileSender(packetMaker);
		packetReceiver = new PacketReceiver(in);
		
		Thread thread = new Thread(packetReceiver);
		thread.start();
	}	
	
	public void SendFileInfo(File file){
		fileSender.SendFileInfo(file);
	}
	
	public boolean SendFileData(File file){
		return fileSender.SendFileData(file);
	}
	
	
	/**
	 * 연결 종료시 호출해야함
	 */
	public void CloseSocket(){
		try {
			out.close();
			in.close();
			socket.close();			
		} catch (IOException e) {
		}
		strIP = null;		
	}
}