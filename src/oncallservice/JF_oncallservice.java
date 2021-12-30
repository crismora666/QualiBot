/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oncallservice;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author NA002456
 */
public class JF_oncallservice extends javax.swing.JFrame {

    private int tperiod;
    private final int tperiod_largo;
    private final int tperiod_corto;
    private final String tservidor;
    private final ArrayList<TMessage> tmessages;
    private int ttimeout;
    private final int tperiod_timeout;
    private OCcliente_Telegram cliente_telegram;
    private Thread telegram_server;

    /**
     * Creates new form JF_oncallservice
     */
    public JF_oncallservice() {
        initComponents();

        tperiod_largo = 60000;
        tperiod_corto = 5000;
        tperiod = tperiod_largo;
        tperiod_timeout = 50;
//        tservidor = "127.0.0.1";
        tservidor = "XXX:81";
        tmessages = new ArrayList();
        ttimeout = 0;
        cliente_telegram = new OCcliente_Telegram();

        (new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        return;
                    }
                    if (!tmessages.isEmpty()) {
                        System.out.println("Forwarding: " + tmessages.get(0));
                        String chat_name = tmessages.get(0).tchatid;
                        String chat_text = tmessages.get(0).ttext;
                        String chat_id = tmessages.get(0).tid;
                        cliente_telegram.send_telegram(chat_name + ";" + chat_text + ";" + chat_id);
                        tmessages.remove(0);
                    }
                }
            }
        })).start();

        System.out.println("Aplicacion iniciada");
    }

    private ArrayList getURLList(URL url_list) {
        String url_list_line = "";
        ArrayList<ArrayList<String>> url_list_al = new ArrayList();
        try {
            BufferedReader url_list_in = new BufferedReader(new InputStreamReader(url_list.openStream()));
            while ((url_list_line = url_list_in.readLine()) != null) {
                ArrayList<String> arrayList = new ArrayList(Arrays.asList((url_list_line.trim()).split(";")));
                url_list_al.add(arrayList);
            }
            url_list_in.close();
        } catch (IOException ex) {
            System.out.println("Error leyendo chats: " + ex.getMessage());
        }
        System.out.println("Chats leidos: " + url_list_al.size());
        return url_list_al;
    }

    private void start_Ttimer() {
        Timer chat_timer = new Timer();
        chat_timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("Leer chats on server");
                ArrayList<ArrayList<String>> chats_list = null;

                try {
                    chats_list = getURLList(new URL("http://" + tservidor + "/webhook/getchats.php/" + (new SimpleDateFormat("yyyyMMddhhmmss")).format(new Date())));
                    /*
                        INSERT INTO `chats` (`chatid`, `text`, `id`) VALUES
                        ('203470126', 'Hola', 1),
                        ('203470126', '1', 2),
                        ('203470126', 'Cum', 3),
                        ('203470126', '1', 4);
                     */
                } catch (MalformedURLException ex) {
                }
                System.out.println("chats: " + chats_list);

                if (!chats_list.isEmpty() && chats_list.get(0).size() == 3) {
                    for (ArrayList<String> t_mess : chats_list) {
                        System.out.println("Processing : " + t_mess);
                        ArrayList<ArrayList<String>> delete_response = null;
                        try {
                            delete_response = getURLList(new URL("http://" + tservidor + "/webhook/deletechat.php/" + (new SimpleDateFormat("yyyyMMddhhmmss")).format(new Date()) + "?fila=" + t_mess.get(2)));
                        } catch (MalformedURLException ex) {
                        }
                        if (((ArrayList<String>) delete_response.get(0)).get(0).trim().equals("1")) {
                            System.out.println("Borrado exitoso");
                        } else {
                            System.out.println("Error en el borrado");
                        }

                        String chat_name = t_mess.get(0).trim();
                        String chat_text = t_mess.get(1).trim();
                        String chat_id = t_mess.get(2).trim();

                        if (!tmessages.contains(new TMessage("", "", chat_id))) {
                            tmessages.add(new TMessage(chat_name, chat_text, chat_id));
                        }
                    }
                    if (tperiod == tperiod_largo) {
                        tperiod = tperiod_corto;
                        System.out.println("Nuevo periodo = " + tperiod);
                        chat_timer.cancel();
                        start_Ttimer();
                        System.out.println(tmessages);
                        return;
                    }
                    ttimeout = 0;
                } else {
                    ttimeout++;
                    if (ttimeout == tperiod_timeout && tperiod == tperiod_corto) {
//                        if(ttimeout == 5 && tperiod == tperiod_corto && tmessages.isEmpty()){
                        tperiod = tperiod_largo;
                        System.out.println("Nuevo periodo = " + tperiod);
                        chat_timer.cancel();
                        start_Ttimer();
                        System.out.println(tmessages);
                        cliente_telegram.send_telegram("borra;chatter");
                        return;
                    }
                }
                System.out.println(tmessages);
            }
        }, 0, tperiod);
    }

    private class TMessage implements Comparable<TMessage> {

        public String tchatid;
        public String ttext;
        public String tid;

        public TMessage(String f_tchatid, String f_ttext, String f_tid) {
            tchatid = f_tchatid;
            ttext = f_ttext;
            tid = f_tid;
        }

        public int compareTo(TMessage other) {
            return tid.compareTo(other.tid);
        }

        public boolean equals(Object obj) {
            if (obj instanceof TMessage) {
                TMessage tmess = (TMessage) obj;
                return this.tid.compareTo(tmess.tid) == 0;
            } else {
                throw new IllegalArgumentException("Chatter expected");
            }
        }
    }

    public void start_Tserver() {
        if (telegram_server != null) {
            if (telegram_server.isAlive()) {
                return;
            }
        }

        telegram_server = new Thread(new Runnable() {
            ServerSocket providerSocket = null;
            int puerto;
            Socket connection = null;

            public void run() {
                try {
                    //1. creating a server socket
                    puerto = 4446;
                    providerSocket = new ServerSocket(puerto, 10);

                    while (true) {
                        //2. Wait for connection
                        System.out.println("Waiting for connection");
                        connection = providerSocket.accept();

                        Thread t = new Thread(new ChatSocket(connection));
                        t.start();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    //4: Closing connection
                    try {
                        providerSocket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        telegram_server.start();
    }

    class ChatSocket implements Runnable {

        Socket chatsocket;
        ObjectInputStream chat_in;
        ObjectOutputStream chat_out;
        String chat_message;

        public ChatSocket(Socket f_chatsocket) {
            chatsocket = f_chatsocket;
        }

        public void run() {
            try {
                System.out.println("Connection received from " + chatsocket.getInetAddress().getHostName() + " in port " + chatsocket.getPort());

                try {
                    // 20 second timeout
                    chatsocket.setSoTimeout(20000);
                } catch (SocketException ex) {

                }

                //3. get Input and Output streams
                chat_out = new ObjectOutputStream(chatsocket.getOutputStream());
                chat_out.flush();
                chat_in = new ObjectInputStream(chatsocket.getInputStream());

                sendMessage("Connection successful", chat_out);

                //4. The two parts communicate via the input and output streams
                try {
                    chat_message = (String) chat_in.readObject();
                    System.out.println("server<" + chat_message);
                    ////////////////
                    ArrayList<String> tlista = new ArrayList(Arrays.asList((chat_message.trim()).split(";")));
                    System.out.println("mensaje recibido: " + tlista);
                    if (chat_message.contains(".jpeg")) {
                        String chat_name = tlista.get(0).trim();
                        String chat_file = tlista.get(1).trim();
                        int response = sendPhoto(chat_name, chat_file);
                        System.out.println("respuesta envio foto: " + response);
                    } else if (chat_message.contains(".pdf")) {
                        String chat_name = tlista.get(0).trim();
                        String chat_file = tlista.get(1).trim();
                        int response = sendDocument(chat_name, chat_file);
                        System.out.println("respuesta envio pdf: " + response);
                    } else if (chat_message.contains(".xlsx")) {
                        String chat_name = tlista.get(0).trim();
                        String chat_file = tlista.get(1).trim();
                        int response = sendDocument(chat_name, chat_file);
                        System.out.println("respuesta envio xlsx: " + response);
                    } else {
                        String chat_name = tlista.get(0).trim();
                        String chat_text = tlista.get(1).trim().toLowerCase();
                        URL url_bot = null;
                        try {
                            url_bot = new URL("https://api.telegram.org/botXXX/sendMessage?chat_id=" + chat_name + "&parse_mode=HTML&text=" + URLEncoder.encode(chat_text, "UTF-8"));
                        } catch (MalformedURLException ex) {
                        } catch (UnsupportedEncodingException ex) {
                        }

                        String url_response = "";
                        try {
                            BufferedReader url_list_in = new BufferedReader(new InputStreamReader(url_bot.openStream()));
                            while ((url_response = url_list_in.readLine()) != null) {
                                System.out.println(url_response);
                                JsonObject jsonObject = new JsonParser().parse(url_response).getAsJsonObject();
                                String j_result = jsonObject.get("ok").getAsString();
                                System.out.println("Resultado: " + j_result);
                            }
                            url_list_in.close();
                        } catch (IOException ex) {

                        }
                    }
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }

                sendMessage("Data recieved", chat_out);

            } catch (IOException ex) {

            } finally {
                //4: Closing connection
                try {
                    chat_in.close();
                    chat_out.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private int sendPhoto(String pchat, String pfile) {
        String url = "https://api.telegram.org/botXXX/sendPhoto";
        String charset = "UTF-8";
        String param = pchat;
        File binaryFile = new File(pfile);
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.
        URLConnection connection = null;
        int responseCode = 0;

        try {
            connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

            // Send normal param.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"chat_id\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(param).append(CRLF).flush();

            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"photo\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();

            // Request is lazily fired whenever you need to obtain information about response.
            responseCode = ((HttpURLConnection) connection).getResponseCode();
            System.out.println(responseCode); // Should be 200
        } catch (IOException ex) {
            System.out.println("error1: " + ex.getMessage());
        }
        return responseCode;
    }

    private int sendDocument(String pchat, String pfile){
        String url = "https://api.telegram.org/botXXX/sendDocument";
        String charset = "UTF-8";
        String param = pchat;
        File binaryFile = new File(pfile);
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.
        URLConnection connection = null;
        int responseCode = 0;
        
        try {    
            connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
    
            OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
 
            // Send normal param.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"chat_id\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(param).append(CRLF).flush();
    
            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"document\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();    
    
            // Request is lazily fired whenever you need to obtain information about response.
            responseCode = ((HttpURLConnection) connection).getResponseCode();
            System.out.println(responseCode); // Should be 200
        } catch (IOException ex) {
            System.out.println("error1: " + ex.getMessage());            
        }
        return responseCode;
    }
    
    void sendMessage(String msg, ObjectOutputStream f_chat_out) {
        try {
            f_chat_out.writeObject(msg);
            f_chat_out.flush();
            System.out.println("server>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jB_init = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Oncall Service 1.0.5");

        jB_init.setText("Iniciar Servicio");
        jB_init.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_initActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jB_init)
                .addContainerGap(270, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jB_init)
                .addContainerGap(266, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jB_initActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_initActionPerformed
        jB_init.setEnabled(false);
        start_Ttimer();
        start_Tserver();
    }//GEN-LAST:event_jB_initActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_init;
    // End of variables declaration//GEN-END:variables
}
