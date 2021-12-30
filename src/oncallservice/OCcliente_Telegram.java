/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oncallservice;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author NA002456
 */
public class OCcliente_Telegram {
    public void send_telegram(String f_chat_data){
        Socket requestSocket = null;
        ObjectOutputStream chat_out = null;
        ObjectInputStream chat_in = null;
        String message = null;
        int puerto;
        String servidor;
        
        try{
            //1. creating a socket to connect to the server
            servidor = "127.0.0.1";
//            servidor = "10.116.211.106";
            puerto = 4445;
            requestSocket = new Socket(servidor, puerto);
            System.out.println("Connected to " + servidor + " in port " + puerto);
            //2. get Input and Output streams
            chat_out = new ObjectOutputStream(requestSocket.getOutputStream());
            chat_out.flush();
            chat_in = new ObjectInputStream(requestSocket.getInputStream());
            //3: Communicating with the server
            try{
                message = (String)chat_in.readObject();
                System.out.println("client<" + message);
                sendMessage(f_chat_data, chat_out);
                message = (String)chat_in.readObject();
                System.out.println("client<" + message);
            }
            catch(ClassNotFoundException classNot){
                System.err.println("data received in unknown format");
            }

        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException){   
            ioException.printStackTrace();
        }
        finally{
            //4: Closing connection
            try{
                chat_in.close();
                chat_out.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }
    
    private void sendMessage(String msg, ObjectOutputStream f_chat_out){
        try{
            f_chat_out.writeObject(msg);
            f_chat_out.flush();
            System.out.println("client>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
}
