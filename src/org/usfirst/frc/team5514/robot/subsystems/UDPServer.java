package org.usfirst.frc.team5514.robot.subsystems;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
/**
 * 
 * This Class handles the communication between the Jetson and the Roborio using UDP to send packets of information.
 * 
 */
public class UDPServer {
	
	@SuppressWarnings("unused")
	private int port;
	private byte[] recieveData;
	private  DatagramSocket serverSocket;
	
	
	/**
	 * 
	 * @param port The port being used to communicate between the two devices
	 * @param packetSize The size of the packet of information to be recieved
	 */
	public UDPServer(int port, int packetSize){
		this.port = port;
		recieveData = new byte[packetSize];
		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.out.println("Could not open DatagramSocket at port: " + port);
			e.printStackTrace();
		}
		try {
			serverSocket.setSoTimeout(10);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return The port currently active
	 */
	public int getPort(){
		return serverSocket.getPort();
	}
	
	/**
	 * Gets the next packet of data and stores it into the recieveData array
	 */
	public void getPacket(){
		DatagramPacket recievePacket = new DatagramPacket(recieveData, recieveData.length);
		try {
			serverSocket.receive(recievePacket);
			recieveData = recievePacket.getData();
			System.out.println("Recieved packet");
		} catch (IOException e) {
			//System.out.println("Unable to recieve a packet, Timeout");
			//e.printStackTrace();
		}
	}
	
	public void printLocalHost(){
		System.out.println(serverSocket.getLocalAddress());
	}
	
	public String getLocalHost(){
		return serverSocket.getLocalAddress().toString();
	}
	
	/**
	 * Prints the hex values of the data in the recieveData array
	 */
	public String toString(){
		 StringBuilder sb = new StringBuilder();
         for(byte b : recieveData){
       	  sb.append(String.format("%02X ", b));
         }
         return sb.toString();
	}
	
	/**
	 * 
	 * @return The total area of the contours the video sees
	 */
	public int getTotalArea(){
		int totalArea = (int) (recieveData[11]&0xff) << 24 | (recieveData[10]& 0xff)<<16 | (recieveData[9] & 0xff) <<8 | (recieveData[8] &0xff);
		return totalArea;
	}
	
	
	
	/**
	 * Converts the 5th to 8th bytes of data to a decimal and converts it back to the original angle to an accuracy of 3 decimal places
	 * 
	 * @return The angle between the center line of the boxes and the bottom of the frame
	 */
	public double getAngleDeg(){
		int angle = (recieveData[7]&0xff) << 24 | (recieveData[6]& 0xff)<<16 | (recieveData[5] & 0xff) <<8 | (recieveData[4] &0xff);
		return angle/100.;
	}
	
	/**
	 * Calls the getAngleDeg() function and converts from degrees to radians
	 * @return The angle between the center line of the boxes and the bottom of the frame
	 */
	public double getAngleRad(){
		return getAngleDeg()*Math.PI/180;
	}
	
	/**
	 * 
	 * @return Number of pixels the robot is off of alignment, negative means Robot is looking too far right and needs to turn left
	 */
	public int getOffset(){
		int offset = recieveData[3] << 24 | (recieveData[2]& 0xff)<<16 | (recieveData[1] & 0xff) <<8 | (recieveData[0] &0xFF);
        return offset;
	}
	
	
}
