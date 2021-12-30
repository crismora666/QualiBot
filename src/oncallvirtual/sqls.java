/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oncallvirtual;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author NA002456
 */
public class sqls {
    
    public final String kpi_servidor;
    public final String kpi_servidor_u;
    public final String kpi_servidor_p; 
    
    public sqls(){
        kpi_servidor = "jdbc:oracle:thin:@XXX";
//        kpi_servidor = "jdbc:oracle:thin:@XXX";}
        kpi_servidor_u = "GSMKPI";
        kpi_servidor_p = "kpigsm"; 
    }
    
    public String getquery(int code, String query_opts){
        String f_query = "";
        switch(code){
            //3g sitio
            case 0:
                f_query = "SELECT FECHA AS fecha_reporte, \n" +
            "          cell_name as elemento,  \n" +
            "          ROUND (ERL, 2) AS voz_erl,\n" +
            "          ROUND (100 * EFI_VOZ, 2) efi_voz,\n" +
            "          ROUND (DATOS_MB, 2) datos_mb,\n" +
            "          ROUND (100 * EFI_DATOS, 2) efi_datos,\n" +
            "          ROUND (USER_HSDPA, 2) AS hs_user,\n" +
            "          ROUND (RTWP, 2) rtwp,\n" +
            "          NVL (FRAME_LOSS, 0) as frame_loss,\n" +                    
            "          ROUND(100 * DISPONIBILIDAD, 2) disponibilidad\n" +                    
            "            FROM KPIGSM.HTV_3G_KPIS_MORABOT A\n" +
            "            WHERE A.CELLID IN (" + query_opts + ")";
                break;
            //4g sitio
            case 1:
                f_query = "SELECT FECHA AS fecha_reporte,\n" +
            "          cell_name as elemento,         \n" +
            "          ROUND (DATOS_MB, 2) datos_mb,\n" +
            "          ROUND (100*EFI_DATOS, 2) efi_datos,\n" +
            "          ROUND (USERS, 2) AS users,\n" +
            "          ROUND(100 * DISPONIBILIDAD, 2) disponibilidad\n" +
            "     FROM KPIGSM.HTV_4G_KPIS_MORABOT A\n" +
            "    WHERE     A.CELLID IN (" + query_opts + ")";
                break;
            //3g ciudad
            case 2:
                f_query = "SELECT FECHA AS fecha_reporte,\n" +
            "          A.CIUDAD as elemento,  \n" +
            "          ROUND (SUM(ERL), 2) AS voz_erl,\n" +
            "          ROUND (100*\n" +
            "              ((  1\n" +
            "               - (DECODE (SUM (DROP_DEN_VOZ),\n" +
            "                          0, 0,\n" +
            "                          SUM (DROP_NUM_VOZ) / SUM (DROP_DEN_VOZ))))\n" +
            "            * (  DECODE (SUM (RRC_DEN), 0, 1, SUM (RRC_NUM) / SUM (RRC_DEN))\n" +
            "               * DECODE (SUM (RAB_DEN), 0, 1, SUM (RAB_NUM) / SUM (RAB_DEN))))\n" +
            "          ,2) efi_voz,\n" +
            "          ROUND (SUM(DATOS_MB), 2) datos_mb,\n" +
            "          ROUND (100 * \n" +
            "          (DECODE (SUM (DROP_DEN_DATOS),\n" +
            "                      0, 1,\n" +
            "                      SUM (DROP_NUM_DATOS) / SUM (DROP_DEN_DATOS))\n" +
            "            * DECODE (SUM (ACC_DEN_DATOS),\n" +
            "                      0, 1,\n" +
            "                      SUM (ACC_NUM_DATOS) / SUM (ACC_DEN_DATOS)))\n" +
            "          , 2) efi_datos,\n" +
            "          ROUND (SUM(USER_HSDPA), 2) AS hs_user,\n" +
            "          ROUND (AVG(RTWP), 2) rtwp,\n" +
            "          SUM(NVL(FRAME_LOSS,0)) frame_loss,\n" +
            "          ROUND(100 * AVG(DISPONIBILIDAD), 2) disponibilidad\n" +
            "            FROM KPIGSM.HTV_3G_KPIS_MORABOT A\n" +
            "            WHERE LOWER(A.CIUDAD) IN '" + query_opts + "'            \n" +
            "            GROUP BY FECHA, A.CIUDAD";
                break;
            //4g ciudad
            case 3:
                f_query = "SELECT FECHA AS fecha_reporte,\n" +
            "          A.CIUDAD as elemento,\n" +
            "          ROUND (SUM(DATOS_MB), 2) datos_mb,\n" +
            "          ROUND (100*\n" +
            "              ((  1\n" +
            "               - DECODE (SUM (DROP_DEN), 0, 0, SUM (DROP_NUM) / SUM (DROP_DEN)))\n" +
            "            * DECODE (SUM (RRC_DEN), 0, 1, SUM (RRC_NUM) / SUM (RRC_DEN))\n" +
            "            * DECODE (SUM (S1_DEN), 0, 1, SUM (S1_NUM) / SUM (S1_DEN))\n" +
            "            * DECODE (SUM (ERAB_DEN), 0, 1, SUM (ERAB_NUM) / SUM (ERAB_DEN)))          \n" +
            "          , 2) efi_datos,\n" +
            "          ROUND (SUM(USERS), 2) AS users,\n" +
            "          ROUND(100 * AVG(DISPONIBILIDAD), 2) disponibilidad\n" +
            "     FROM KPIGSM.HTV_4G_KPIS_MORABOT A\n" +
            "     WHERE LOWER(A.CIUDAD) IN '" + query_opts + "'\n" +
            "     GROUP BY FECHA, A.CIUDAD";
                break;
            //3g provincia
            case 4:
                f_query = "SELECT FECHA AS fecha_reporte,\n" +
        "          A.PROVINCIA as elemento,  \n" +
        "          ROUND (SUM(ERL), 2) AS voz_erl,\n" +
        "          ROUND (100*\n" +
        "              ((  1\n" +
        "               - (DECODE (SUM (DROP_DEN_VOZ),\n" +
        "                          0, 0,\n" +
        "                          SUM (DROP_NUM_VOZ) / SUM (DROP_DEN_VOZ))))\n" +
        "            * (  DECODE (SUM (RRC_DEN), 0, 1, SUM (RRC_NUM) / SUM (RRC_DEN))\n" +
        "               * DECODE (SUM (RAB_DEN), 0, 1, SUM (RAB_NUM) / SUM (RAB_DEN))))\n" +
        "          ,2) efi_voz,\n" +
        "          ROUND (SUM(DATOS_MB), 2) datos_mb,\n" +
        "          ROUND (100 * \n" +
        "          (DECODE (SUM (DROP_DEN_DATOS),\n" +
        "                      0, 1,\n" +
        "                      SUM (DROP_NUM_DATOS) / SUM (DROP_DEN_DATOS))\n" +
        "            * DECODE (SUM (ACC_DEN_DATOS),\n" +
        "                      0, 1,\n" +
        "                      SUM (ACC_NUM_DATOS) / SUM (ACC_DEN_DATOS)))\n" +
        "          , 2) efi_datos,\n" +
        "          ROUND (SUM(USER_HSDPA), 2) AS hs_user,\n" +
        "          ROUND (AVG(RTWP), 2) rtwp,\n" +
        "          SUM(NVL(FRAME_LOSS,0)) frame_loss,\n" +
        "          ROUND(100 * AVG(DISPONIBILIDAD), 2) disponibilidad\n" +                        
        "            FROM KPIGSM.HTV_3G_KPIS_MORABOT A\n" +
        "            WHERE LOWER(A.PROVINCIA) IN '" + query_opts + "'\n" +
        "            GROUP BY FECHA, A.PROVINCIA";
                break;
            //4g provincia
            case 5:
                f_query = "SELECT FECHA AS fecha_reporte,\n" +
        "          A.PROVINCIA as elemento,\n" +
        "          ROUND (SUM(DATOS_MB), 2) datos_mb,\n" +
        "          ROUND (100*\n" +
        "              ((  1\n" +
        "               - DECODE (SUM (DROP_DEN), 0, 0, SUM (DROP_NUM) / SUM (DROP_DEN)))\n" +
        "            * DECODE (SUM (RRC_DEN), 0, 1, SUM (RRC_NUM) / SUM (RRC_DEN))\n" +
        "            * DECODE (SUM (S1_DEN), 0, 1, SUM (S1_NUM) / SUM (S1_DEN))\n" +
        "            * DECODE (SUM (ERAB_DEN), 0, 1, SUM (ERAB_NUM) / SUM (ERAB_DEN)))\n" +
        "          , 2) efi_datos,\n" +
        "          ROUND (SUM(USERS), 2) AS users,\n" +
        "          ROUND(100 * AVG(DISPONIBILIDAD), 2) disponibilidad\n" +        
        "     FROM KPIGSM.HTV_4G_KPIS_MORABOT A\n" +
        "     WHERE LOWER(A.PROVINCIA) IN '" + query_opts + "'\n" +
        "     GROUP BY FECHA, A.PROVINCIA";
                break;
        case 6:
            f_query = "SELECT FECHA AS fecha_reporte,          \n" +
    "          A.CONTROLADORA as elemento,  \n" +
    "          ROUND (SUM(ERL), 2) AS voz_erl,\n" +
    "          ROUND (100*\n" +
    "              ((  1\n" +
    "               - (DECODE (SUM (DROP_DEN_VOZ),\n" +
    "                          0, 0,\n" +
    "                          SUM (DROP_NUM_VOZ) / SUM (DROP_DEN_VOZ))))\n" +
    "            * (  DECODE (SUM (RRC_DEN), 0, 1, SUM (RRC_NUM) / SUM (RRC_DEN))\n" +
    "               * DECODE (SUM (RAB_DEN), 0, 1, SUM (RAB_NUM) / SUM (RAB_DEN))))\n" +
    "          ,2) efi_voz,\n" +
    "          ROUND (SUM(DATOS_MB), 2) datos_mb,\n" +
    "          ROUND (100 * \n" +
    "          (DECODE (SUM (DROP_DEN_DATOS),\n" +
    "                      0, 1,\n" +
    "                      SUM (DROP_NUM_DATOS) / SUM (DROP_DEN_DATOS))\n" +
    "            * DECODE (SUM (ACC_DEN_DATOS),\n" +
    "                      0, 1,\n" +
    "                      SUM (ACC_NUM_DATOS) / SUM (ACC_DEN_DATOS)))\n" +
    "          , 2) efi_datos,\n" +
    "          ROUND (SUM(USER_HSDPA), 2) AS hs_user,\n" +
    "          ROUND (AVG(RTWP), 2) rtwp,\n" +
    "          SUM(NVL(FRAME_LOSS,0)) frame_loss,\n" +
    "          ROUND(100 * AVG(DISPONIBILIDAD), 2) disponibilidad\n" +
    "            FROM KPIGSM.HTV_3G_KPIS_MORABOT A\n" +
    "            WHERE LOWER(A.CONTROLADORA) IN '" + query_opts + "'\n" +
    "            GROUP BY FECHA, A.CONTROLADORA";
            break;    
        }
        
        return f_query;
    }
    
    public Map<String, TreeMap<String, Integer>> carga_lista_sitios(){
        Map<String, TreeMap<String, Integer>> allsites = new HashMap();
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Locale initLocale = Locale.getDefault();
            Locale.setDefault(Locale.ENGLISH);
            conn = DriverManager.getConnection (kpi_servidor, kpi_servidor_u, kpi_servidor_p);
            Locale.setDefault(initLocale); 
            Statement stmt = conn.createStatement();
            String f_query = "SELECT ESTACION AS estacion, NOMBRE AS nombre, CELLID AS cellid FROM DATAWH.DCV_CLUSTERQ_CELLID WHERE RED NOT IN '2G'";
//            System.out.println(f_query);
            ResultSet rset = stmt.executeQuery(f_query);
            if (rset != null)
                while ( rset.next() ) {
                    String f_sitio = rset.getString("estacion");
                    String f_sector = rset.getString("nombre");
                    int f_sectorid = rset.getInt("cellid");
                    if(!allsites.containsKey(f_sitio)){
                        TreeMap<String, Integer> allsectors = new TreeMap();
                        allsites.put(f_sitio, allsectors);
                    }
                    allsites.get(f_sitio).put(f_sector, f_sectorid);                    
                }
            rset.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }       
        
        System.out.println("Sitios cargados: " + allsites.size());
        return allsites;
    }
    
    public ArrayList<String> carga_lista_rncs(){
        ArrayList<String> allrncs = new ArrayList();
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Locale initLocale = Locale.getDefault();
            Locale.setDefault(Locale.ENGLISH);
            conn = DriverManager.getConnection (kpi_servidor, kpi_servidor_u, kpi_servidor_p);
            Locale.setDefault(initLocale); 
            Statement stmt = conn.createStatement();
            String f_query = "SELECT DISTINCT DECODE(BSC_RNC,'102','RNCGYE102','104','RNCGYE104','105','RNCGYE105','106','RNCGYE106','107','RNCGYE107','108','RNCGYE108','6','RNCUIO06','8','RNCUIO08',BSC_RNC) BSC_RNC  FROM DATAWH.DCV_CLUSTERQ_CELLID WHERE BSC_RNC IS NOT NULL AND RED IN '3G'";
//            System.out.println(f_query);
            ResultSet rset = stmt.executeQuery(f_query);
            if (rset != null)
                while ( rset.next() ) {
                    allrncs.add(rset.getString("bsc_rnc").toLowerCase());                  
                }
            rset.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }       
        
        System.out.println("RNCs cargados: " + allrncs.size());
        return allrncs;
    }
    
    public ArrayList<String> carga_lista_provincias(){
        ArrayList<String> allprovincias = new ArrayList();
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Locale initLocale = Locale.getDefault();
            Locale.setDefault(Locale.ENGLISH);
            conn = DriverManager.getConnection (kpi_servidor, kpi_servidor_u, kpi_servidor_p);
            Locale.setDefault(initLocale); 
            Statement stmt = conn.createStatement();
            String f_query = "SELECT DISTINCT PROVINCIA FROM DATAWH.DCV_CLUSTERQ_CELLID";
//            System.out.println(f_query);
            ResultSet rset = stmt.executeQuery(f_query);
            if (rset != null)
                while ( rset.next() ) {
                    allprovincias.add(rset.getString("provincia").toLowerCase());                  
                }
            rset.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }       
        
        System.out.println("Provincias cargados: " + allprovincias.size());
        return allprovincias;
    }
    
    public ArrayList<String> carga_lista_ciudades(){
        ArrayList<String> allciudades = new ArrayList();
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Locale initLocale = Locale.getDefault();
            Locale.setDefault(Locale.ENGLISH);
            conn = DriverManager.getConnection (kpi_servidor, kpi_servidor_u, kpi_servidor_p);
            Locale.setDefault(initLocale); 
            Statement stmt = conn.createStatement();
            String f_query = "SELECT DISTINCT CIUDAD FROM DATAWH.DCV_CLUSTERQ_CELLID";
//            System.out.println(f_query);
            ResultSet rset = stmt.executeQuery(f_query);
            if (rset != null)
                while ( rset.next() ) {
                    allciudades.add(rset.getString("ciudad").toLowerCase());                  
                }
            rset.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }       
        
        System.out.println("Ciudades cargados: " + allciudades.size());
        return allciudades;
    }
    
    public ArrayList<qregister3g> get_kpis3g(String f_query_opts, int f_kpi_scope){        
        ArrayList<qregister3g> f_kpis = new ArrayList();  
        
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Locale initLocale = Locale.getDefault();
            Locale.setDefault(Locale.ENGLISH);
            conn = DriverManager.getConnection (kpi_servidor, kpi_servidor_u, kpi_servidor_p);
            Locale.setDefault(initLocale); 
            Statement stmt = conn.createStatement();
            String f_query = "";
            switch(f_kpi_scope){
                case 1:
                    f_query = getquery(0, f_query_opts);
                    break;
                case 2:
                    f_query = getquery(2, f_query_opts);
                    break;
                case 3:
                    f_query = getquery(4, f_query_opts);
                    break;
                case 4:
                    f_query = getquery(6, f_query_opts);
                    break;
            }
                    
            System.out.println(f_query);
            ResultSet rset = stmt.executeQuery(f_query);
            if (rset != null)
                while ( rset.next() ) {
//                    f_kpis.add(new qregister3g(rset.getTimestamp("fecha_reporte"),rset.getString("elemento"),rset.getFloat("efi_voz"),rset.getFloat("efi_datos"),rset.getFloat("hs_user"),rset.getFloat("rtwp"),0,rset.getFloat("voz_erl"),rset.getFloat("datos_mb")));
                    f_kpis.add(new qregister3g(rset.getTimestamp("fecha_reporte"),rset.getString("elemento"),rset.getFloat("efi_voz"),rset.getFloat("efi_datos"),rset.getFloat("hs_user"),rset.getFloat("rtwp"),rset.getFloat("frame_loss"),rset.getFloat("voz_erl"),rset.getFloat("datos_mb"),rset.getFloat("disponibilidad")));
                }
            rset.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }       
        return f_kpis;
    }
    
    public ArrayList<qregister4g> get_kpis4g(String f_query_opts, int f_kpi_scope){        
        ArrayList<qregister4g> f_kpis = new ArrayList();  
        
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Locale initLocale = Locale.getDefault();
            Locale.setDefault(Locale.ENGLISH);
            conn = DriverManager.getConnection (kpi_servidor, kpi_servidor_u, kpi_servidor_p);
            Locale.setDefault(initLocale); 
            Statement stmt = conn.createStatement();
            String f_query = "";
            switch(f_kpi_scope){
                case 1:
                    f_query = getquery(1, f_query_opts);
                    break;
                case 2:
                    f_query = getquery(3, f_query_opts);
                    break;
                case 3:
                    f_query = getquery(5, f_query_opts);
                    break;
                case 4:
                    return f_kpis;
            }
//            System.out.println(f_query);
            ResultSet rset = stmt.executeQuery(f_query);
            if (rset != null)
                while ( rset.next() ) {
                    f_kpis.add(new qregister4g(rset.getTimestamp("fecha_reporte"),rset.getString("elemento"),rset.getFloat("efi_datos"),rset.getFloat("users"),rset.getFloat("datos_mb"),rset.getFloat("disponibilidad")));
                }
            rset.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }       
        return f_kpis;
    }  
}
