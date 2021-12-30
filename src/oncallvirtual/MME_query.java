/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oncallvirtual;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author NA002456
 */
public class MME_query {

    private Map<String, SSH_comm> segws;
    private Map<String, Integer> c1;
    private Map<String, String[]> cliente_logs;

    private Map<String, String> clientes;
    private Map<String, Integer> clientes_index;

    private Map<Integer, String> all_sectors;
    private Map<Integer, String> all_mccs;
    private Map<String, String> all_mccmncs;

    private String msisdn;
    private Map<String, String> servidores;
    private Map<String, Boolean> founds;

    public MME_query() {
        segws = new HashMap();

        init_clientes();

        c1 = new HashMap();
        cliente_logs = new HashMap();

        servidores = new HashMap();
        founds = new HashMap();

        init_sectors();
        init_mccs();
        init_mccmncs();
    }

    public String query_imsi_mme(String f_msisdn) {
        msisdn = f_msisdn;
        if(msisdn.length() == 2){
            if(clientes.get(msisdn.toUpperCase()) == null)
                return "Codigo no valido";
            else 
                msisdn = clientes.get(msisdn.toUpperCase());
        }

        (new Thread(new Runnable() {
            public void run() {
//                jL_status_calderon.setText("Calderon: buscando...");
                get_data("calderon", "XXX", "XXX", "XXX", "[SAEGWCLD01]  >");
//                jL_status_calderon.setText("Calderon: finalizado");
                System.out.println("Finalizo busqueda Calderon");
            }
        })).start();
//        (new Thread(new Runnable() {
//            public void run() {
////                jL_status_chongon.setText("Chongon: buscando...");
//                get_data("chongon", "10.116.101.1", "CLOPEZ", "Pafeto.79", "[SAEGWCHO01]  >");
////                jL_status_chongon.setText("Chongon: finalizado");
//                System.out.println("Finalizo busqueda Chongon");
//            }
//        })).start();

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }

        for (int i = 0; i < 12; i++) {                        
            if(founds.get("calderon"))
//            if(founds.get("calderon")&&founds.get("chongon"))
                return servidores.get("calderon") + " - " + servidores.get("chongon");
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }

        return servidores.get("calderon") + " - " + servidores.get("chongon");
    }

    private void init_clientes() {
        clientes = new TreeMap();
        clientes_index = new HashMap();

        if (true) {
            Connection conn_cellids = null;
            Statement stmnt_cellids = null;
            String query_cellid = null;
            ResultSet rs_cellids = null;

            try {
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
                conn_cellids = DriverManager.getConnection("jdbc:ucanaccess://listaclientes.accdb");
                stmnt_cellids = conn_cellids.createStatement();
                query_cellid = "select cliente,msisdn from clientesvip";
                rs_cellids = stmnt_cellids.executeQuery(query_cellid);
                while (rs_cellids.next()) {
                    clientes.put(rs_cellids.getString("cliente"), rs_cellids.getString("msisdn"));
                }
            } catch (ClassNotFoundException e1) {
                System.err.println(e1);
            } catch (SQLException e1) {
                System.err.println(e1);
            } finally {
                try {
                    if (stmnt_cellids != null) {
                        stmnt_cellids.close();
                    }
                    if (conn_cellids != null) {
                        conn_cellids.close();
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }

            int cliente_i = 0;
            for (String cliente_name : clientes.keySet()) {
                clientes_index.put(cliente_name, cliente_i);
                cliente_i++;
            }
        }
        System.out.println("Clientes importados: " + clientes.size());
    }

    private void init_sectors() {
        Connection conn_cellids = null;
        Statement stmnt_cellids = null;
        String query_cellid = null;
        ResultSet rs_cellids = null;

        all_sectors = new HashMap();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn_cellids = DriverManager.getConnection("jdbc:ucanaccess://cellids.accdb");
            stmnt_cellids = conn_cellids.createStatement();
            query_cellid = "select cellid,nombre from cellids";
            rs_cellids = stmnt_cellids.executeQuery(query_cellid);
            while (rs_cellids.next()) {
                all_sectors.put(rs_cellids.getInt("cellid"), rs_cellids.getString("nombre"));
            }
        } catch (ClassNotFoundException e1) {
            System.err.println(e1);
        } catch (SQLException e1) {
            System.err.println(e1);
        } finally {
            try {
                if (stmnt_cellids != null) {
                    stmnt_cellids.close();
                }
                if (conn_cellids != null) {
                    conn_cellids.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        System.out.println("Sectores importados: " + all_sectors.size());
    }

    private void init_mccs() {
        Connection conn_mccs = null;
        Statement stmnt_mccs = null;
        String query_mccs = null;
        ResultSet rs_mccs = null;

        all_mccs = new HashMap();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn_mccs = DriverManager.getConnection("jdbc:ucanaccess://operadoras.accdb");
            stmnt_mccs = conn_mccs.createStatement();
            query_mccs = "select mcc,pais from mccs";
            rs_mccs = stmnt_mccs.executeQuery(query_mccs);
            while (rs_mccs.next()) {
                all_mccs.put(rs_mccs.getInt("mcc"), rs_mccs.getString("pais"));
            }
        } catch (ClassNotFoundException e1) {
            System.err.println(e1);
        } catch (SQLException e1) {
            System.err.println(e1);
        } finally {
            try {
                if (stmnt_mccs != null) {
                    stmnt_mccs.close();
                }
                if (conn_mccs != null) {
                    conn_mccs.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        System.out.println("MCCs importados: " + all_mccs.size());
    }

    private void init_mccmncs() {
        Connection conn_mccs = null;
        Statement stmnt_mccs = null;
        String query_mccs = null;
        ResultSet rs_mccs = null;

        all_mccmncs = new HashMap();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn_mccs = DriverManager.getConnection("jdbc:ucanaccess://operadoras.accdb");
            stmnt_mccs = conn_mccs.createStatement();
            query_mccs = "select mccmnc,operadora from mccmncs";
            rs_mccs = stmnt_mccs.executeQuery(query_mccs);
            while (rs_mccs.next()) {
                all_mccmncs.put(rs_mccs.getString("mccmnc"), rs_mccs.getString("operadora"));
            }
        } catch (ClassNotFoundException e1) {
            System.err.println(e1);
        } catch (SQLException e1) {
            System.err.println(e1);
        } finally {
            try {
                if (stmnt_mccs != null) {
                    stmnt_mccs.close();
                }
                if (conn_mccs != null) {
                    conn_mccs.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        System.out.println("MCCMNCs importados: " + all_mccmncs.size());
    }

    private void get_data(String f_central, String f_ip, String f_user, String f_pass, String f_central_prompt) {
        boolean isdemo = true;
        c1.put(f_central, 0);
        cliente_logs.put(f_central, new String[clientes.size()]);
        segws.put(f_central, new SSH_comm(f_central, f_ip, f_user, f_pass, f_central_prompt));
        servidores.put(f_central, "NA");
        founds.put(f_central, false);
        if (isdemo) {
            System.out.println(f_central + c1.get(f_central) + " test1: " + segws.get(f_central).got_response + segws.get(f_central).response_got);
        }
        if (wait_ssh_response(f_central, -1)) {
            return;
        }
        if (isdemo) {
            System.out.println(f_central + c1.get(f_central) + " test2: " + segws.get(f_central).got_response + segws.get(f_central).response_got);
        }
        try {
            Thread.sleep(200);
        } catch (Exception e) {
        }
//        System.out.println(segws.get(f_central).log);
//        for (String vp_name : clientes.keySet()) {
//            int vp_index = clientes_index.get(vp_name);
        int vp_index = 0;
        segws.get(f_central).log = "";
        segws.get(f_central).response_got = 0;
        segws.get(f_central).response_expected = f_central_prompt;
        segws.get(f_central).response_expected2 = "--More--";
//            send_ssh(f_central, "show ng trace data-base-dump filter msisdn 593999726057\r");  
        send_ssh(f_central, "show ng trace data-base-dump filter msisdn " + msisdn + "\r");
//            send_ssh(f_central, "show ng trace data-base-dump filter msisdn " + clientes.get(vp_name) + "\r");
        if (isdemo) {
            System.out.println(f_central + c1.get(f_central) + " test3: " + segws.get(f_central).got_response + segws.get(f_central).response_got);
        }
        if (wait_ssh_response(f_central, vp_index)) {
            return;
        }
        if (isdemo) {
            System.out.println(f_central + c1.get(f_central) + " test4: " + segws.get(f_central).got_response + segws.get(f_central).response_got);
        }
        try {
            Thread.sleep(200);
        } catch (Exception e) {
        }
//            System.out.println(segws.get(f_central).log);
        while (segws.get(f_central).response_got == 2) {
            segws.get(f_central).log = "";
            segws.get(f_central).response_got = 0;
            segws.get(f_central).response_expected = f_central_prompt;
            segws.get(f_central).response_expected2 = "--More--";
            send_ssh(f_central, " ");
            if (isdemo) {
                System.out.println(f_central + c1.get(f_central) + " test5: " + segws.get(f_central).got_response + segws.get(f_central).response_got);
            }
            if (wait_ssh_response(f_central, vp_index)) {
                return;
            }
            if (isdemo) {
                System.out.println(f_central + c1.get(f_central) + " test6: " + segws.get(f_central).got_response + segws.get(f_central).response_got);
            }
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
        }
//            System.out.println(vp_log);

        String vp_imsi = "";
        String vp_rat = "";
        String vp_rat_print = "";
        boolean notfirst_vp_rat = false;
        String vp_loc = "";
        String vp_loc_parsed = "";
        String vp_pais = "";
        String vp_operador = "";
        String vp_lactac = "";
        int i_vp_lactac = 0;
        String vp_ci = "";
        int i_vp_ci = 0;
        String vp_lactac_ci_print = "";
        boolean notfirst_vp_lactacci = false;
        for (String vp_line : cliente_logs.get(f_central)[vp_index].split("\\r")) {
            if (vp_line.contains("IMSI = ")) {
                vp_imsi = vp_line.substring(vp_line.indexOf("IMSI = ") + "IMSI = ".length());
                System.out.println(vp_imsi);
//                    update_tabla(vp_imsi, clientes_index.get(vp_name), central_imsi_col.get(f_central));
            }
            if (vp_line.contains("RAT type = ")) {
                vp_rat = vp_line.substring(vp_line.indexOf("RAT type = ") + "RAT type = ".length());
                System.out.println(vp_rat);
                vp_rat_print += (notfirst_vp_rat ? " / " : "") + vp_rat;
//                    update_tabla(vp_rat_print, clientes_index.get(vp_name), central_tech_col.get(f_central));
                notfirst_vp_rat = true;
            }
            if (vp_line.contains("geographic location = ")) {
                vp_loc_parsed = "";
                vp_lactac_ci_print += (notfirst_vp_lactacci ? " / " : "");
                notfirst_vp_lactacci = true;
                vp_loc = vp_line.substring(vp_line.indexOf("geographic location = ") + "geographic location = ".length());
                System.out.println(vp_loc);
                for (String vp_loc_pair : vp_loc.trim().split(" ")) {
                    if (vp_loc_pair.compareTo("(HEX)") != 0) {
                        vp_loc_parsed += vp_loc_pair;
                    }
                }
                System.out.println(vp_loc_parsed.length() + " " + vp_loc_parsed);
                vp_pais = "" + (char) vp_loc_parsed.charAt(1) + (char) vp_loc_parsed.charAt(0) + (char) vp_loc_parsed.charAt(3);
                vp_operador = "" + (char) vp_loc_parsed.charAt(5) + (char) vp_loc_parsed.charAt(4) + ((char) vp_loc_parsed.charAt(2) != 'f' ? (char) vp_loc_parsed.charAt(2) : "");
                System.out.println(vp_pais + " " + vp_operador);
                if (vp_pais.contains("740")) {
                    if (vp_operador.compareTo("00") == 0) {
                        if (vp_rat.contains("E UTRAN") && vp_loc_parsed.length() == 24) {
                            vp_lactac = vp_loc_parsed.substring(6, 10);
                            i_vp_lactac = Integer.parseInt(vp_lactac, 16);
                            System.out.println(i_vp_lactac);
                            vp_ci = vp_loc_parsed.substring(16);
                            i_vp_ci = Integer.parseInt(vp_ci, 16);
                            System.out.println(i_vp_ci + " " + all_sectors.get(i_vp_ci));
                            if (all_sectors.get(i_vp_ci) == null) {
                                vp_lactac_ci_print += "No in db: " + i_vp_lactac + " " + i_vp_ci;
                            } else {
                                vp_lactac_ci_print += i_vp_lactac + " " + all_sectors.get(i_vp_ci);
                            }
                        } else if (vp_rat.contains("UTRAN") && vp_loc_parsed.length() == 14) {
                            vp_lactac = vp_loc_parsed.substring(6, 10);
                            i_vp_lactac = Integer.parseInt(vp_lactac, 16);
                            System.out.println(i_vp_lactac);
                            vp_ci = vp_loc_parsed.substring(10);
                            i_vp_ci = Integer.parseInt(vp_ci, 16);
                            System.out.println(i_vp_ci + " " + all_sectors.get(i_vp_ci));
                            if (all_sectors.get(i_vp_ci) == null) {
                                vp_lactac_ci_print += "No in db: " + i_vp_lactac + " " + i_vp_ci;
                            } else {
                                vp_lactac_ci_print += i_vp_lactac + " " + all_sectors.get(i_vp_ci);
                            }
                        } else if (vp_rat.contains("GERAN") && vp_loc_parsed.length() == 19) {
                            vp_lactac = vp_loc_parsed.substring(6, 10);
                            i_vp_lactac = Integer.parseInt(vp_lactac, 16);
                            System.out.println(i_vp_lactac);
                            vp_ci = vp_loc_parsed.substring(10, 14);
                            i_vp_ci = Integer.parseInt(vp_ci, 16);
                            System.out.println(i_vp_ci + " " + all_sectors.get(i_vp_ci));
                            if (all_sectors.get(i_vp_ci) == null) {
                                vp_lactac_ci_print += "No in db: " + i_vp_lactac + " " + i_vp_ci;
                            } else {
                                vp_lactac_ci_print += i_vp_lactac + " " + all_sectors.get(i_vp_ci);
                            }
                        }
                    } else if (vp_operador.compareTo("01") == 0) {
                        vp_lactac_ci_print += "Roaming con Claro";
                    }
                } else {
                    if (all_mccmncs.get(vp_pais + vp_operador) != null) {
                        vp_lactac_ci_print += all_mccmncs.get(vp_pais + vp_operador);
                    } else if (all_mccs.get(Integer.parseInt(vp_pais)) != null) {
                        vp_lactac_ci_print += all_mccs.get(Integer.parseInt(vp_pais)) + "-" + vp_operador;
                    } else {
                        vp_lactac_ci_print += "No in mccmnc: " + vp_pais + vp_operador;
                    }
                }
                servidores.put(f_central, vp_lactac_ci_print);
//                    update_tabla(vp_lactac_ci_print, clientes_index.get(vp_name), central_serv_col.get(f_central));
            }
            if (vp_line.contains("not found.")) {
                System.out.println("not found");
//                    update_tabla("NA", clientes_index.get(vp_name), central_imsi_col.get(f_central));
//                    update_tabla("NA", clientes_index.get(vp_name), central_tech_col.get(f_central));
//                    update_tabla("NA", clientes_index.get(vp_name), central_serv_col.get(f_central));
            }
        }
//        }
        send_ssh(f_central, "exit\r");
        try {
            Thread.sleep(200);
        } catch (Exception e) {
        }
        founds.put(f_central, true);
    }

    public void send_ssh(String f_central, String f_to_send) {
        if (segws.get(f_central) != null) {
            if (segws.get(f_central).isconnected()) {
                for (int i = 0; i < f_to_send.length(); i++) {
                    segws.get(f_central).send(f_to_send.charAt(i));
                }
            }
        }
    }

    private boolean wait_ssh_response(String f_central, int f_vp_index) {
        Date f_now = new Date();
        while (!segws.get(f_central).got_response) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
            if (TimeUnit.SECONDS.convert((new Date().getTime() - f_now.getTime()), TimeUnit.MILLISECONDS) >= 20) {
                return true;
            }
        }
//        System.out.println(c1 + "Cliente Encontro: " + segws.get(f_centr).response_expected);
//        System.out.println(c1 + "Cliente Log: " + segws.get(f_centr).log);
        if (f_vp_index != -1) {
            cliente_logs.get(f_central)[f_vp_index] += segws.get(f_central).log;
        }
        segws.get(f_central).got_response = false;
        c1.put(f_central, c1.get(f_central) + 1);
        return false;
    }
}
