import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
 
/**
 * This program demonstrates a socket client program that talks to a SMTP server.
 *
 * @author www.codejava.net
 */
public class TimeClient {
 
    public static void main(String[] args) {
 
        String hostname = "127.0.0.1";
        int port = 27019;

        try{
            Socket sock = new Socket(hostname, port);
    
            InputStream input = sock.getInputStream();
            OutputStream output = sock.getOutputStream();
    
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.putInt(0);
            byteBuffer.putInt(0);  
    
            output.write(byteBuffer.array());

            // try {
            //     Thread.sleep(100);
            // } catch (InterruptedException ex) {
            //     ex.printStackTrace();
            // }

            byte[] data = new byte[ Settings.GRID_TILES_WIDTH * Settings.GRID_TILES_HEIGHT *4];
            input.read(data);

            ByteBuffer readBytes = ByteBuffer.wrap(data);
            
            for( int i = 0 ; i < 2 ; i ++)
                System.out.print(readBytes.getInt() + " ");
                
            System.out.println("");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            output.flush();
            sock.close();
        }catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        }catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }



        // try (Socket socket = new Socket(hostname, port)) {
 
        //     InputStream input = socket.getInputStream();
        //     OutputStream output = socket.getOutputStream();
            
        //     ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        //     byteBuffer.putInt(10);
        //     byteBuffer.putInt(11);

        //     output.write(byteBuffer.array());
        //     socket.close();

            // writer.println("hello " + message);

            // String line = reader.readLine();
            // System.out.println(line);
 
            // line = reader.readLine();
            // System.out.println(line);
 
            // writer.println("quit");
            // line = reader.readLine();
            // System.out.println(line);
 
        // } catch (UnknownHostException ex) {
 
        //     System.out.println("Server not found: " + ex.getMessage());
 
        // } catch (IOException ex) {
 
        //     System.out.println("I/O error: " + ex.getMessage());
        // }
    }
}