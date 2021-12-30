/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oncallvirtual;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author NA002456
 */
public class SSH_comm {
    public JSch jsch;
    public Session session;
    public UserInfo ui;
    public Channel channel;
    public ByteArrayOutputStream bits_outputstream;
    public PipedInputStream piped_in;
    public PipedOutputStream piped_out;
    private String central;
    public String response_expected;
    public String response_expected2;
    public boolean got_response;
    public String log;    
    public int response_got;
    
    public SSH_comm(String f_central, String f_host, String f_user, String f_pass, String f_central_prompt) {
        jsch=new JSch();
        try{
            central = f_central;
            response_expected = f_central_prompt;
            response_expected2 = "ñññ";
            got_response = false;
            log = "";
            response_got = 0;
            
            String host = f_host;
            String user = f_user;
            session = jsch.getSession(user, host, 22);
            String passwd = f_pass;
            session.setPassword(passwd);
            ui = new MyUserInfo(){
                public void showMessage(String message){
                  JOptionPane.showMessageDialog(null, message);
                }
                public boolean promptYesNo(String message){
                  Object[] options={ "yes", "no" };
                  int foo=JOptionPane.showOptionDialog(null, 
                                                       message,
                                                       "Warning", 
                                                       JOptionPane.DEFAULT_OPTION, 
                                                       JOptionPane.WARNING_MESSAGE,
                                                       null, options, options[0]);
                  return foo==0;
                }

                // If password is not given before the invocation of Session#connect(),
                // implement also following methods,
                //   * UserInfo#getPassword(),
                //   * UserInfo#promptPassword(String message) and
                //   * UIKeyboardInteractive#promptKeyboardInteractive()
                
            };
            session.setUserInfo(ui);
        } catch (JSchException ex) {
            System.out.println("No pudo crear session: " + ex.getMessage());
            ex.printStackTrace();
        }       
        try{
            // It must not be recommended, but if you want to skip host-key check,
            // invoke following,
            session.setConfig("StrictHostKeyChecking", "no");

            //session.connect();
            session.connect(30000);   // making a connection with timeout.
        } catch (JSchException ex) {
            System.out.println("No pudo conectar session: " + ex.getMessage());
            ex.printStackTrace();
        }
        try{
            channel=session.openChannel("shell");

            // Enable agent-forwarding.
            //((ChannelShell)channel).setAgentForwarding(true);

//            channel.setInputStream(System.in);
//            bits_inputstream = new ByteArrayInputStream((jTF_send.getText() + "\r").getBytes());
//            channel.setInputStream(bits_inputstream);
            piped_out = new PipedOutputStream();
            piped_in = new PipedInputStream(piped_out);
            channel.setInputStream(piped_in);
            /*
            // a hack for MS-DOS prompt on Windows.
            channel.setInputStream(new FilterInputStream(System.in){
                public int read(byte[] b, int off, int len)throws IOException{
                    return in.read(b, off, (len>1024?1024:len));
                }
            });
            */

//            channel.setOutputStream(System.out);
            bits_outputstream = new ByteArrayOutputStream();
            channel.setOutputStream(bits_outputstream);

            /*
            // Choose the pty-type "vt102".
            ((ChannelShell)channel).setPtyType("vt102");
            */

            /*
            // Set environment variable "LANG" as "ja_JP.eucJP".
            ((ChannelShell)channel).setEnv("LANG", "ja_JP.eucJP");
            */
        } catch (JSchException ex) {
            System.out.println("No pudo crear channel jsch: " + ex.getMessage());
            ex.printStackTrace();
        }
        catch (IOException ex) {
            System.out.println("No pudo crear channel io: " + ex.getMessage());
            ex.printStackTrace();
        }
        try{
            //channel.connect();
            channel.connect(3*1000);
        } catch (JSchException ex) {
            System.out.println("No pudo conectar channel: " + ex.getMessage());
            ex.printStackTrace();
        }
        (new Thread(new Runnable() {
            public void run() {
                int f_rx_buffer_stat = 0;
                boolean f_rx_buffer_ready = false;                
                try {
                    do {
                        if(bits_outputstream.size() > 0){
                            if(!f_rx_buffer_ready){
                                String f_rx_temp = bits_outputstream.toString();
                                if(f_rx_temp.length() > f_rx_buffer_stat) {
                                    f_rx_buffer_stat = f_rx_temp.length();
                                    try{Thread.sleep(1000);} catch (Exception e){}
                                } else {
                                    f_rx_buffer_ready = true;
                                }
                            } else {
                                String b_out = bits_outputstream.toString();
                                bits_outputstream.reset();                                
//                                System.out.print(b_out);   
                                log += b_out;
                                f_rx_buffer_ready = false;
                                f_rx_buffer_stat = 0;
                                if(log.contains(response_expected) || log.contains(response_expected2)){ 
//                                    System.out.println("SSH Encontro: " + response_expected);
//                                    System.out.println("SSH Log: " + log);
                                    if(log.contains(response_expected)) response_got = 1;
                                    if(log.contains(response_expected2)) response_got = 2;
                                    response_expected = "ñññ";
                                    response_expected2 =  "ñññ";
                                    got_response = true;                                                                    
                                }                                
                            }
                        }                          
                    } while(isconnected()); 
                    System.out.println("RX cerrado: " + central);
                } 
                catch (Exception e) {                                       
                    System.out.println(central + " RX interrumpido: " + e.getMessage());
                    e.printStackTrace();
                    salir();
                }
            }
        })).start();  
        (new Thread(new Runnable() {
            public void run() {
                do {
                    try{Thread.sleep(5000);} catch (Exception e){}
//                    System.out.println(central + ": " + isconnected());
                } while(isconnected());  
                System.out.println(central + ": saliendo");
            }
        })).start();        
    }
    
    public void send(int key){
        try {
            //        bits_inputstream = new ByteArrayInputStream((jTF_send.getText() + "\r").getBytes());
//        channel.setInputStream(bits_inputstream);
            piped_out.write(key);
//            System.out.print((char)key);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public boolean isconnected(){
        return channel.isConnected();
    }
    
    public void salir(){
        channel.disconnect();
        session.disconnect();
    }
    
    public class MyUserInfo implements UserInfo, UIKeyboardInteractive{
        public String getPassword(){ return null; }
        public boolean promptYesNo(String str){ return false; }
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return false; }
        public boolean promptPassword(String message){ return false; }
        public void showMessage(String message){ }
        public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo){
            return null;
        }
    }
    
}
