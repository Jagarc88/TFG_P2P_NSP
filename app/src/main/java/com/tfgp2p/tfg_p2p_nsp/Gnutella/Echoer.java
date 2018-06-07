package com.tfgp2p.tfg_p2p_nsp.Gnutella;


import java.net.*;
import java.io.*;
import java.util.*;


public class Echoer implements Runnable {
	int i,backlog;
	public static int tcpPort, udpPort= 0;
	public Socket tcpClient= null;
	public ServerSocket tcpSocket= null;
	public DatagramSocket udpSocket=null;
	public static String ipAddr= null;
	static boolean isConnect= false;


	public Echoer(int tcpPort, int udpPort, int backlog) {
			try {
				if((tcpPort >= 1) && (tcpPort <= 60000) &&(udpPort >= 1)&&(udpPort <= 60000)) {

					/*
					Con tcpPort=0 el constructor genera un puerto automáticamente.
					El puerto obtenido se puede ver con getLocalPort().
					 */
					//ServerSocket tcpSocket = new ServerSocket(0, backlog);
					ServerSocket tcpSocket = new ServerSocket(tcpPort, backlog);
					udpSocket = new DatagramSocket(udpPort);
					Socket testSocket= new Socket("8.8.8.8",53);
					ipAddr= testSocket.getLocalAddress().getHostAddress();
					Echoer.tcpPort= tcpPort;
					//Echoer.tcpPort = tcpSocket.getLocalPort();
					///////////////////////////////////////////
					Echoer.udpPort= udpPort;
					System.out.println("Listening on "+ ipAddr + " TCP port- " + tcpPort+" and UDP port- "+ udpPort);
					this.backlog= backlog;
					testSocket.close();
					listening(tcpSocket);
				}
				else {
					System.out.println("Port Numbers should be between 1 and 60000");
				}
			}
			catch (SocketTimeoutException s) {
				System.out.println("timeout");
			}
			catch (IOException ioe) {
				System.out.println("could not listen on port "+ tcpPort);
				System.exit(-1);
			}
	}
	public Echoer (ServerSocket s) {
		this.tcpSocket= s;
	}
	public Echoer () {
	
	}
	void listening(ServerSocket s){
			try {
					//i++;
					System.out.println();

					/*
					 - El Echoer creado se queda bloqueado hasta que alguien quiera conectarse a él.
					 - El AcceptInput es el que enviará los comandos/mensajes.
					 - El UDPrecv se queda bloqueado hasta que alguien le envíe un datagrama.
					 */
					Thread t1 = new Thread((Runnable) new Echoer(s));
					t1.start();
					Thread t2= new Thread((Runnable) new AcceptInput());
					t2.start();
					Thread t3= new Thread((Runnable) new UDPrecv(udpSocket));
					t3.start();
					//start udp listening thread
				}
				catch (Exception e) {
					System.out.println("Cannot accept connection");
				}
			}
	
	public void Client(String addr, int port) 
	{
		try {
			tcpClient = new Socket(addr,port);
			ConnectionList.maintainList(tcpClient);
			//maintain a list here now to have index ipaddr, hostaddr, localport, remoteport;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		while (true) {
			try {
				isConnect=false;
				Socket sock1= tcpSocket.accept();
				//Client(InetAddress.getLocalHost().getHostAddress(),tcpSocket.getLocalPort());
				System.out.println("got Conn. request from "+sock1.getInetAddress().toString());
				Thread t4= new Thread ((Runnable)new Messaging(sock1));
				t4.start();
				//new ConnectionList(sock1);
			}
			catch (IOException o) {
				System.out.println("Cannot connect: Read Failed");
				}
			}
		}
/* ***********************************************************************************
	public static void main(String[] args) {
		new Echoer(Integer.parseInt(args[0]),Integer.parseInt(args[1]),5);
		
	}
 * ***********************************************************************************/
}

class AcceptInput implements Runnable {
	String cmd, cmdSend;
	int i,j;
	StringTokenizer st= null;
	String[] cl_ip=new String[20];
	String[] cl_host=new String[20];
	int[] cl_port=new int[20];
	int[] cl_remote=new int[20];
	int index=0;
	public void run () {
		//System.out.println("t2");
		//try {
			//while (true) {
				BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
				//cmd= br.readLine();
				///////////////////////////////////////////////////////////////////////

				cmd = "connect 192.168.0.11 1103";
				// GNUTELLA VA POR EL 6346 ????
				//cmd = "connect 10.141.238.180 62351";
				//cmd = "connect 83.42.56.228 5010";
				cmdSend = "send 1 nada";

				///////////////////////////////////////////////////////////////////////
				i= cmd.length();
				tokenize(cmd,i);

				//int j = cmdSend.length();
				//tokenize(cmdSend, j);
				//}
			//}
		/*catch (IOException e) {
				e.printStackTrace();
		}*/
	}

	void tokenize(String cmd, int i) {
		j=0;
		String[] token= new String[i];
		st= new StringTokenizer(cmd," ");
		while (st.hasMoreElements()) {
			token [j]=	st.nextToken();
			j++;
		}
		if (token[0].equalsIgnoreCase("connect")) {
			//token[1] es la dirección IP del cliente, token[2] su puerto.
			ConnectionList.AcceptConn(token[1],Integer.parseInt(token[2]));
		}
		else if (token[0].equalsIgnoreCase("show")) {
			ConnectionList.show();
		}
		else if (token[0].equalsIgnoreCase("send")) {
			int k=2;
			String s= "";
			while (token[k] != null) {
				s = s + token[k] + " ";
				k++;
			}
			ConnectionList.Msg(Integer.parseInt(token[1]), s); //send()
		}
		else if (token[0].equalsIgnoreCase("sendto")) {
			int m=3;
			String s= "";
			while (token[m] != null) {
				s = s + token[m] + " ";
				m++;
			}
			Messaging.send_udp(token[1], Integer.parseInt(token[2]), s);
		}
		else if (token[0].equalsIgnoreCase("disconnect")) {
			ConnectionList.disconnect(Integer.parseInt(token[1]));
		}
		else if (token[0].equalsIgnoreCase("exit")) {
			System.exit(-1);
		}
		else if (token[0].equalsIgnoreCase("info")) {
			ConnectionList.info();
		}
		else {
			System.out.println("No such Commands available, valid commands:");
			System.out.println("info");
			System.out.println("connect <ip-address> <tcp-port>");
			System.out.println("show");
			System.out.println("send <conn-id> <message>");
			System.out.println("sendto <ip-address> <udp-port> <message>");
			System.out.println("disconnect <conn-id>");
			System.out.println("exit");
			System.out.println();
		}
	}
}
	
class Messaging implements Runnable {
	Socket s1,s2;
	String msg;
	public Messaging (Socket s1, String msg) {
		this.s1=s1;
		this.msg= msg;
		send();
	}
	public Messaging (Socket s2) {
		this.s2=s2;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la dirección del sdcard en el dispositivo que se encuentra abierto en ese momento.
	 * @return
	 */
	public static File parseMountDirectory() {
		File dir_00 = new File("/storage/extSdCard");
		File dir_01 = new File("/storage/sdcard1");
		File dir_1 = new File("/storage/usbcard1");
		File dir_2 = new File("/storage/sdcard0");
		File dir_3 = new File("/mnt/sdcard");
		return dir_01.exists() ? dir_01 : dir_00.exists() ? dir_00 :
				dir_1.exists() ? dir_1 : dir_2.exists() ? dir_2 : dir_3.exists() ? dir_3 :
						null;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////
	void send(){
		try {
			DataOutputStream dout= new DataOutputStream(s1.getOutputStream());
			//dout.writeUTF(msg);
			///////////////////////////////////////////////////////////////////////////
			// Escribir los datos del archivo aquí.
			String path = parseMountDirectory().getAbsolutePath() + "/de_julio.txt";
			FileInputStream fis = new FileInputStream(path);
			byte[] buffer = new byte[4096];

			while (fis.read(buffer) > 0) {
				dout.write(buffer);
			}
			dout.flush();
			fis.close();

			///////////////////////////////////////////////////////////////////////////
			//dout.flush();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
	}
	static void send_udp(String ipaddr, int udPort, String message)
	{
		try
		{
			DatagramSocket clientSocket = new DatagramSocket();
			byte[] sendData = new byte[1024];
			sendData = message.getBytes();
			DatagramPacket sendPacket =  new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ipaddr), udPort);
			clientSocket.send(sendPacket);
			clientSocket.close();
		}

		catch(SocketTimeoutException e)
		{
			System.out.println("Connection not able to established :");
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void run() {
		boolean b=true;
		while (b) {
			DataInputStream din;
			try {
				din = new DataInputStream(s2.getInputStream()); 
				/*String str= din.readUTF();
				System.out.println(str);
				//if (str==null) {*/
				/////////////////////////////////////////////////////////////////

				FileOutputStream fos = new FileOutputStream(parseMountDirectory().getAbsolutePath() + "/de_julio.txt");
				byte[] buffer = new byte[4096];

				int read = 0;
				int totalRead = 0;
				int remaining = 1024; //Falta saber cómo mando el tamaño exacto del archivo para no liarla en el envío.
				while((read = din.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
					totalRead += read;
					remaining -= read;
					System.out.println("read " + totalRead + " bytes.");
					fos.write(buffer, 0, read);
				}

				fos.close();

				/////////////////////////////////////////////////////////////////
				din.close();
				//}
			}
			catch (IOException e) {
				e.printStackTrace();
				b=false;
				//din.close();
			}
		}
	}
}

class ConnectionList extends Thread implements Runnable {
	static int index=0;
	static String msg= null;
	static Socket[] tcpClient= new Socket[20];
	static int a=0;
	static void AcceptConn (String ip_addr,int port) {
		new Echoer().Client(ip_addr,port);
	}
	static void Msg (int index1, String msg1) {
		if (tcpClient[index1] != null) {
			new Messaging(tcpClient[index1], msg1);
		}
		else {
			System.out.println("Enter a valid connection id");
		}
	}
	public ConnectionList(Socket sock3) {
		//Echoer.isConnect= sock3.isConnected();
		tcpClient[a]= sock3; //recvd client sock from accept now update array
		a++;
	}
	static void maintainList(Socket sock4){
		System.out.println("now were onset");
		index++;
		tcpClient[index]=sock4;
	}
	static void show() {
		int p=1;
		if (tcpClient[p] != null) {
			System.out.println("Conn ID |	IP	|   Hostname Port |  Local Port | Remote |");
			for (; p<20; p++) {
				if (tcpClient[p] != null) {
						System.out.println(p + "     |     "+ tcpClient[p].getInetAddress().getHostAddress() + "   |    " + tcpClient[p].getInetAddress().getHostName() + "  | " + " " + tcpClient[p].getPort() + "  | " + " " + tcpClient[p].getLocalPort() );
				}
			}
		}
	}
	static void disconnect (int k) {
		int id;
		System.out.println("index to delete-"+k);
		for (id=k; id<19; id++) {
			try{
				if (tcpClient[id+1] != null) {
					tcpClient[id]=tcpClient[id+1];
				} 
				else {
					tcpClient[id]=null;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	index--;
	}
	static void info()
	{
		try
		{
			// String serverIp = InetAddress.getLocalHost().getHostAddress();
			String hostName = InetAddress.getLocalHost().getHostName();

			System.out.println("IP Address     |  Host Name 	         |    TCP Port  |   UDP Port");
			System.out.println(Echoer.ipAddr + "  |   " + hostName + "      |   " + Echoer.tcpPort + "  |    " +Echoer.udpPort);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void run () {
		
	}
}
class UDPrecv implements Runnable 
{
	public DatagramSocket udpSock;
	UDPrecv(DatagramSocket udpSock) throws IOException
	{
		this.udpSock = udpSock;
	}
	UDPrecv()
	{
		
	}
	public void run()
	{
		while(true)
		{
			try
			{
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				udpSock.receive(receivePacket); 
				String message = new String(receivePacket.getData());
				System.out.println(message);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}