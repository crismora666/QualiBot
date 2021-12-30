/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oncallvirtual;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author NA002456
 */
public class Astellia {

    private ArrayList<Date> fechas_dbuio3g;
    private ArrayList<String> nombres_dbuio3g;
    private ArrayList<Date> fechas_dbuio2g;
    private ArrayList<String> nombres_dbuio2g;

    private String server_uio;
    private String user_uio;
    private String password_uio;

    private boolean is_baseuio_ok;
    private ArrayList<String> imsis_batch;
    private ArrayList<qquery> qquerys;
    int n_dias;

    private ArrayList<ArrayList> all_conn_types;
    private ArrayList<Integer> conn_type_n;
    private ArrayList<String> conn_type_text;
    private ArrayList<String> conn_type_comment;

    private ArrayList<ArrayList> all_endconn_types;
    private ArrayList<Integer> endconn_type_n;
    private ArrayList<String> endconn_type_text;
    private ArrayList<String> endconn_type_comment;

    private ArrayList<ArrayList> all_endconn_phases;
    public ArrayList<Integer> endconn_phase_n;
    private ArrayList<String> endconn_phase_text;
    private ArrayList<String> endconn_phase_comment;

    private ArrayList<ArrayList> all_sectors;
    private ArrayList<Integer> sector_cellid;
    private ArrayList<String> sector_name;

    private Cliente_Telegram tcliente;

    public Astellia() {
        server_uio = "XXX";
        user_uio = "cigale";
        password_uio = "astellia";
//        server_uio = "127.0.0.1";
//        user_uio = "root";
//        password_uio = "enero2012";

        init_conn_types();
        init_sectors();
    }

    public String getbases_ava() {
        fechas_dbuio3g = new ArrayList();
        nombres_dbuio3g = new ArrayList();
        fechas_dbuio2g = new ArrayList();
        nombres_dbuio2g = new ArrayList();

        ArrayList<Date> fechas_min = new ArrayList();
        ArrayList<Date> fechas_max = new ArrayList();

        Date first_date = null;
        Date last_date = null;

        is_baseuio_ok = false;

        int total_db_inline;

        Connection con_uio = null;
        PreparedStatement pst_uio = null;
        ResultSet rs_db_uio = null;
        ResultSet rs_db_uio2 = null;

        String db_name_uio;
        String db_query_uio;

        try {
            con_uio = DriverManager.getConnection("jdbc:mysql://" + server_uio + ":3306/", user_uio, password_uio);
            rs_db_uio = con_uio.getMetaData().getCatalogs();
            while (rs_db_uio.next()) {
                db_name_uio = rs_db_uio.getString("TABLE_CAT");
//                System.out.println(db_name);
                if (db_name_uio.contains("movistar_ecuador_iucs")) {
                    db_query_uio = "SHOW TABLES FROM " + db_name_uio + " LIKE 'xl3'";
                    pst_uio = con_uio.prepareStatement(db_query_uio);
                    rs_db_uio2 = pst_uio.executeQuery();
                    while (rs_db_uio2.next()) {
                        DateFormat formatter = new SimpleDateFormat("yyMMdd");
                        try {
                            fechas_dbuio3g.add(formatter.parse(db_name_uio.substring(0, 6)));
                        } catch (ParseException ex) {
                            System.out.println("Error parseando fecha de tabla: " + ex.getMessage());
                        }
                        nombres_dbuio3g.add(db_name_uio);
                        System.out.println(db_name_uio + " " + rs_db_uio2.getString(1));
                    }
                }
                if (db_name_uio.contains("movistar_ecuador_gsm")) {
                    db_query_uio = "SHOW TABLES FROM " + db_name_uio + " LIKE 'xl3'";
                    pst_uio = con_uio.prepareStatement(db_query_uio);
                    rs_db_uio2 = pst_uio.executeQuery();
                    while (rs_db_uio2.next()) {
                        DateFormat formatter = new SimpleDateFormat("yyMMdd");
                        try {
                            fechas_dbuio2g.add(formatter.parse(db_name_uio.substring(0, 6)));
                        } catch (ParseException ex) {
                            System.out.println("Error parseando fecha de tabla: " + ex.getMessage());
                        }
                        nombres_dbuio2g.add(db_name_uio);
                        System.out.println(db_name_uio + " " + rs_db_uio2.getString(1));
                    }
                }
                is_baseuio_ok = true;
            }
        } catch (SQLException ex) {
            System.out.println("Error conectando a la base_uio: " + ex.getMessage());
        } finally {
            try {
                if (rs_db_uio2 != null) {
                    rs_db_uio2.close();
                }
                if (rs_db_uio != null) {
                    rs_db_uio.close();
                }
                if (pst_uio != null) {
                    pst_uio.close();
                }
                if (con_uio != null) {
                    con_uio.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error cerrando conecciones: " + ex.getMessage());
            }
        }

        if (!fechas_dbuio3g.isEmpty()) {
            fechas_min.add(Collections.min(fechas_dbuio3g));
            fechas_max.add(Collections.max(fechas_dbuio3g));
        }
        if (!fechas_dbuio2g.isEmpty()) {
            fechas_min.add(Collections.min(fechas_dbuio2g));
            fechas_max.add(Collections.max(fechas_dbuio2g));
        }

        if (!fechas_min.isEmpty() && !fechas_max.isEmpty()) {
            first_date = Collections.min(fechas_min);
            last_date = Collections.max(fechas_max);
        }

        total_db_inline = nombres_dbuio3g.size() + nombres_dbuio2g.size();

        System.out.println(fechas_dbuio3g);
        System.out.println(nombres_dbuio3g);
        System.out.println(fechas_dbuio2g);
        System.out.println(nombres_dbuio2g);
        System.out.println(first_date + " - " + last_date + " - " + is_baseuio_ok);
        System.out.println("Bases totales: " + total_db_inline);

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        if (is_baseuio_ok && total_db_inline != 0) {
            return "Bases 3g: " + nombres_dbuio3g.size() + " ("
                    + df.format(Collections.min(fechas_dbuio3g)) + " al " + df.format(Collections.max(fechas_dbuio3g)) + ")"
                    + "\nBases 2g: " + nombres_dbuio2g.size() + " ("
                    + df.format(Collections.min(fechas_dbuio2g)) + " al " + df.format(Collections.max(fechas_dbuio2g)) + ")";
        } else {
            return "Bases no disponibles";
        }
    }

    public String query_bases(String f_imsi) {
        ArrayList<String> tlista = new ArrayList(Arrays.asList((f_imsi.trim()).split(" ")));  
        if(!StringUtils.isNumericSpace(f_imsi) || tlista.size() > 2) return "Imsi mal ingresado";
        imsis_batch = new ArrayList();
        imsis_batch.add(tlista.get(0));
        n_dias = 3;
        if(tlista.size() == 2) n_dias = Integer.parseInt(tlista.get(1));
        qquerys = new ArrayList();
        String excel_file = "Error consultando imsi";

        if (is_baseuio_ok) {
            consulta_db(server_uio, user_uio, password_uio, nombres_dbuio3g, fechas_dbuio3g, true);
            consulta_db(server_uio, user_uio, password_uio, nombres_dbuio2g, fechas_dbuio2g, false);
            if (qquerys.isEmpty()) {
                return "Imsi no tiene datos";
            }
            try {
                System.out.println(qquerys.size());
                Collections.sort(qquerys);
                SXSSFWorkbook wb = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
                wb.setCompressTempFiles(true); // temp files will be gzipped   
                CellStyle row_style_title = wb.createCellStyle();
                row_style_title.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                row_style_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                CellStyle row_style_3g = wb.createCellStyle();
                row_style_3g.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                row_style_3g.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                CellStyle row_style_2g = wb.createCellStyle();
                row_style_2g.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                row_style_2g.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                ArrayList<Sheet> shs = new ArrayList();
                ArrayList<Integer> row_counts = new ArrayList();

                for (String imsi : imsis_batch) {
                    Sheet sh = (Sheet) wb.createSheet(imsi);
                    Row row = sh.createRow(0);
                    row.createCell(0).setCellValue("Tecnologia");
                    row.createCell(1).setCellValue("Inicio");
                    row.createCell(2).setCellValue("Fin");
                    row.createCell(3).setCellValue("Sector inicio");
                    row.createCell(4).setCellValue("Sector fin");
                    row.createCell(5).setCellValue("Tipo");
                    row.createCell(6).setCellValue("Ultimo evento");
                    row.createCell(7).setCellValue("Causa");
                    row.createCell(8).setCellValue("Primer evento");
                    row.createCell(9).setCellValue("Numero B");
                    row.createCell(10).setCellValue("IMEI");
                    int column_count = row.getPhysicalNumberOfCells();
                    for (int i_row = 0; i_row < column_count; i_row++) {
                        row.getCell(i_row).setCellStyle(row_style_title);
                    }
                    shs.add(sh);
                    row_counts.add(1);
                }
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                for (qquery qr : qquerys) {
                    int imsi_index = imsis_batch.indexOf(qr.imsi);
                    System.out.println(qr.is_3g + " | " + df.format(qr.start_date) + " | " + qr.cellid);
                    Row row = shs.get(imsi_index).createRow(row_counts.get(imsi_index));
                    row.createCell(0).setCellValue(qr.is_3g ? "WCDMA" : "GSM");
                    row.createCell(1).setCellValue(df.format(qr.start_date));
                    row.createCell(2).setCellValue(df.format(qr.end_date));
                    row.createCell(3).setCellValue(qr.start_cellid);
                    row.createCell(4).setCellValue(qr.cellid);
                    row.createCell(5).setCellValue(qr.tipo);
                    row.createCell(6).setCellValue(qr.ultimo_evento);
                    row.createCell(7).setCellValue(qr.causa);
                    row.createCell(8).setCellValue(qr.primer_evento);
                    row.createCell(9).setCellValue(qr.numero);
                    row.createCell(10).setCellValue(Long.toHexString(qr.imei));
                    int column_count = row.getPhysicalNumberOfCells();
                    for (int i_row = 0; i_row < column_count; i_row++) {
                        if (qr.is_3g == true) {
                            row.getCell(i_row).setCellStyle(row_style_3g);
                        } else {
                            row.getCell(i_row).setCellStyle(row_style_2g);
                        }
                    }
                    row_counts.set(imsi_index, row_counts.get(imsi_index) + 1);
                }
                Date excel_now = new Date();
                DateFormat dfx = new SimpleDateFormat("ddMMyyyyHHmmss");
                try {
                    excel_file = "E:\\imag\\" + dfx.format(excel_now) + ".xlsx";
                    FileOutputStream out = new FileOutputStream(excel_file);
                    wb.write(out);
                    out.close();
                } catch (Exception e) {
                }
                // dispose of temporary files backing this workbook on disk
                wb.dispose();
                System.out.println("Exito");
            } catch (Exception ex) {
                System.out.println("Error creando archivo excel: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return excel_file;
    }

    private void consulta_db(String f_server, String f_user, String f_pass, ArrayList<String> f_nombres, ArrayList<Date> f_fechas, boolean f_is3g) {
//        int n_dias = 3;
        Connection con_db = null;
        PreparedStatement pst_db = null;
        ResultSet rs_db = null;
        String url_db = "jdbc:mysql://" + f_server + ":3306/";
        String query_db = "";

        try {
            con_db = DriverManager.getConnection(url_db, f_user, f_pass);
            boolean first_row = true;
            for (int i = f_nombres.size(); i > f_nombres.size() - Math.min(f_nombres.size(), n_dias); i--) {
                String nombre_db = f_nombres.get(i - 1);
                if (f_is3g) {
                    query_db += (first_row ? "" : " UNION ALL ")
                            + "SELECT a.xl3_start_cnx,a.xl3_end_cnx,b.elt_code AS ci_ini,"
                            + "b.elt_code AS ci_fin,CONCAT(d.elt_name,\" - \",d.elt_comment) AS last_event,"
                            + "CONCAT(e.elt_name,\" - \",e.elt_comment) AS cause,"
                            + "CONCAT(f.elt_name,\" - \",f.elt_comment) AS 1st_event,"
                            + "a.xl3_number,a.xl3_imei,a.xl3_imsi,a.xl3_type_slm FROM "
                            + nombre_db + ".xl3 AS a "
                            + "LEFT JOIN (SELECT idelement,elt_code FROM " + nombre_db
                            + ".net_elts WHERE elt_type = 12) AS b ON a.xl3_1st_sai_ti = b.idelement "
                            + "LEFT JOIN (SELECT idelement,elt_code FROM " + nombre_db
                            + ".net_elts WHERE elt_type = 12) AS c ON a.xl3_sai = c.idelement "
                            + "LEFT JOIN (SELECT idelement,elt_name,elt_comment FROM " + nombre_db
                            + ".stm_elts) AS d ON a.xl3_last_event = d.idelement "
                            + "LEFT JOIN (SELECT idelement,elt_name,elt_comment FROM " + nombre_db
                            + ".stm_elts) AS e ON a.xl3_cause = e.idelement "
                            + "LEFT JOIN (SELECT idelement,elt_name,elt_comment FROM " + nombre_db
                            + ".stm_elts) AS f ON a.xl3_1st_event_cnx = f.idelement "
                            + "WHERE";
                } else {
                    query_db += (first_row ? "" : " UNION ALL ")
                            + "SELECT a.xl3_start,a.xl3_end,a.xl3_ci,"
                            + "CONCAT(d.elt_name,\" - \",d.elt_comment) AS last_event,"
                            + "CONCAT(e.elt_name,\" - \",e.elt_comment) AS cause,"
                            + "CONCAT(f.elt_name,\" - \",f.elt_comment) AS 1st_event,"
                            + "a.xl3_number,a.xl3_imei,a.xl3_imsi FROM "
                            + nombre_db + ".xl3 AS a "
                            + "LEFT JOIN (SELECT idelement,elt_name,elt_comment FROM " + nombre_db
                            + ".stm_elts) AS d ON a.xl3_end_event = d.idelement "
                            + "LEFT JOIN (SELECT idelement,elt_name,elt_comment FROM " + nombre_db
                            + ".stm_elts) AS e ON a.xl3_cause_rejcause = e.idelement "
                            + "LEFT JOIN (SELECT idelement,elt_name,elt_comment FROM " + nombre_db
                            + ".stm_elts) AS f ON a.xl3_start_event = f.idelement "
                            + "WHERE";
                }
                first_row = false;
                boolean es_primero = true;
                for (String imsi : imsis_batch) {
                    query_db += (es_primero ? "" : " or") + " a.xl3_imsi = " + Long.parseLong(imsi, 16);
                    es_primero = false;
                }
            }
            System.out.println(query_db);
            pst_db = con_db.prepareStatement(query_db);
            rs_db = pst_db.executeQuery();
            while (rs_db.next()) {
                if (f_is3g) {
                    qquerys.add(new qquery(true, rs_db.getTimestamp("xl3_start_cnx"),
                            rs_db.getTimestamp("xl3_end_cnx"), getsector(rs_db.getInt("ci_ini")),
                            getsector(rs_db.getInt("ci_fin")), rs_db.getString("last_event"),
                            rs_db.getString("cause"), rs_db.getString("1st_event"),
                            rs_db.getString("xl3_number"), rs_db.getLong("xl3_imei"),
                            Long.toHexString(rs_db.getLong("xl3_imsi")),
                            get_type(rs_db.getInt("xl3_type_slm"))));
                } else {
                    qquerys.add(new qquery(false, rs_db.getTimestamp("xl3_start"),
                            rs_db.getTimestamp("xl3_end"), "", getsector(rs_db.getInt("xl3_ci")),
                            rs_db.getString("last_event"), rs_db.getString("cause"),
                            rs_db.getString("1st_event"), rs_db.getString("xl3_number"),
                            rs_db.getLong("xl3_imei"), Long.toHexString(rs_db.getLong("xl3_imsi")),
                            ""));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error connecting " + f_is3g + ": " + ex.getMessage());
        } finally {
            try {
                if (rs_db != null) {
                    rs_db.close();
                }
                if (pst_db != null) {
                    pst_db.close();
                }
                if (con_db != null) {
                    con_db.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing " + f_is3g + ": " + ex.getMessage());
            }
        }
    }

    public String get_type(int f_type_n) {
        if (f_type_n < 100) {
            return "!!!";
        } else {
            int endconn_phase = f_type_n % 10;
            int endconn_type = (f_type_n / 10) % 10;
            int conn_type = f_type_n / 100;
            System.out.println(f_type_n + "   " + endconn_phase + "   " + endconn_type + "   " + conn_type);
            return conn_type_text.get(conn_type_n.indexOf(conn_type))
                    + " - " + endconn_type_text.get(endconn_type_n.indexOf(endconn_type))
                    + " - " + endconn_phase_text.get(endconn_phase_n.indexOf(endconn_phase));
        }
    }

    private void init_conn_types() {
        all_conn_types = new ArrayList();
        conn_type_n = new ArrayList();
        conn_type_text = new ArrayList();
        conn_type_comment = new ArrayList();
        all_conn_types.add(conn_type_n);
        all_conn_types.add(conn_type_text);
        all_conn_types.add(conn_type_comment);

        conn_type_n.add(1);
        conn_type_n.add(2);
        conn_type_n.add(3);
        conn_type_n.add(4);
        conn_type_n.add(5);
        conn_type_n.add(6);
        conn_type_n.add(7);
        conn_type_n.add(8);
        conn_type_n.add(9);
        conn_type_n.add(10);
        conn_type_n.add(11);
        conn_type_n.add(12);
        conn_type_n.add(13);
        conn_type_n.add(14);
        conn_type_n.add(15);
        conn_type_n.add(16);
        conn_type_n.add(17);
        conn_type_n.add(18);
        conn_type_n.add(19);
        conn_type_n.add(20);
        conn_type_n.add(21);
        conn_type_n.add(22);
        conn_type_n.add(23);
        conn_type_n.add(24);
        conn_type_n.add(25);
        conn_type_n.add(26);
        conn_type_n.add(27);
        conn_type_n.add(28);
        conn_type_n.add(29);
        conn_type_n.add(30);
        conn_type_n.add(31);
        conn_type_n.add(32);
        conn_type_n.add(33);
        conn_type_n.add(34);
        conn_type_n.add(35);
        conn_type_n.add(36);
        conn_type_n.add(37);
        conn_type_n.add(38);
        conn_type_n.add(39);
        conn_type_n.add(40);
        conn_type_n.add(41);

        conn_type_text.add("OC");
        conn_type_text.add("OC voice");
        conn_type_text.add("OC video");
        conn_type_text.add("OC others");
        conn_type_text.add("TC");
        conn_type_text.add("TC voice");
        conn_type_text.add("TC video");
        conn_type_text.add("TC others");
        conn_type_text.add("LU ia");
        conn_type_text.add("LU nu");
        conn_type_text.add("LU pu");
        conn_type_text.add("SMS MO");
        conn_type_text.add("SMS MT");
        conn_type_text.add("SS");
        conn_type_text.add("Attach");
        conn_type_text.add("Attach FO");
        conn_type_text.add("RA nu");
        conn_type_text.add("RA pu");
        conn_type_text.add("RA FOR nu");
        conn_type_text.add("RA FOR pu");
        conn_type_text.add("Service Req");
        conn_type_text.add("PDP");
        conn_type_text.add("Reloc-intra");
        conn_type_text.add("Reloc-inter");
        conn_type_text.add("CS detach");
        conn_type_text.add("PS detach");
        conn_type_text.add("PURGE");
        conn_type_text.add("CS Paging");
        conn_type_text.add("PS Paging");
        conn_type_text.add("Truncated connection OC Voice");
        conn_type_text.add("Truncated connection TC Voice");
        conn_type_text.add("Truncated connection MO SMS");
        conn_type_text.add("Truncated connection MT SMS");
        conn_type_text.add("Truncated connection OC Video");
        conn_type_text.add("Truncated connection TC Video");
        conn_type_text.add("Truncated connection OC Others CS");
        conn_type_text.add("Truncated connection TC Others CS");
        conn_type_text.add("Truncated connection CS");
        conn_type_text.add("Truncated connection PS");
        conn_type_text.add("Truncated connection Data PS");
        conn_type_text.add("wrong behaviour");

        conn_type_comment.add("Originating call");
        conn_type_comment.add("Voice originating call");
        conn_type_comment.add("Video originating call");
        conn_type_comment.add("Originating call not specified as voice or video");
        conn_type_comment.add("Terminating call");
        conn_type_comment.add("Voice terminating call");
        conn_type_comment.add("Video terminating call");
        conn_type_comment.add("Terminating call not specified as voice or video");
        conn_type_comment.add("Location updating request, imsi attach");
        conn_type_comment.add("Location updating request, normal update");
        conn_type_comment.add("Location updating request, periodic update");
        conn_type_comment.add("Mobile originating SMS");
        conn_type_comment.add("Mobile terminating SMS");
        conn_type_comment.add("Supplementary service");
        conn_type_comment.add("Attach");
        conn_type_comment.add("Attach with follow-on request");
        conn_type_comment.add("Routing Area Update, normal update");
        conn_type_comment.add("Routing Area Update, normal update");
        conn_type_comment.add("Routing Area Update, normal update, with follow-on request");
        conn_type_comment.add("Routing Area Update, periodic update, with follow-on request");
        conn_type_comment.add("PS service request");
        conn_type_comment.add("PDP context");
        conn_type_comment.add("Intra 3G incoming SRNS relocation");
        conn_type_comment.add("Incoming relocation from 2G");
        conn_type_comment.add("CS IMSI detach");
        conn_type_comment.add("PS detach");
        conn_type_comment.add("PURGE CDR : the end of the connection could not be determined");
        conn_type_comment.add("CS paging without answer");
        conn_type_comment.add("PS paging without answer");
        conn_type_comment.add("Voice Originating Call created from archive file");
        conn_type_comment.add("Voice Terminating Call created from archive file");
        conn_type_comment.add("Mobile Originating SMS created from archive file");
        conn_type_comment.add("Mobile Terminating SMS created from archive file");
        conn_type_comment.add("Video Originating Call created from archive file");
        conn_type_comment.add("Video Terminating Call created from archive file");
        conn_type_comment.add("Originating Call not specified as voice or video created from archive file");
        conn_type_comment.add("Terminating Call not specified as voice or video created from archive file");
        conn_type_comment.add("CS call created from archive file");
        conn_type_comment.add("PS session created from archive file");
        conn_type_comment.add("PS data session created from archive file");
        conn_type_comment.add("Abnormal initial UE message");

        all_endconn_types = new ArrayList();
        endconn_type_n = new ArrayList();
        endconn_type_text = new ArrayList();
        endconn_type_comment = new ArrayList();
        all_endconn_types.add(endconn_type_n);
        all_endconn_types.add(endconn_type_text);
        all_endconn_types.add(endconn_type_comment);

        endconn_type_n.add(1);
        endconn_type_n.add(2);
        endconn_type_n.add(3);
        endconn_type_n.add(4);
        endconn_type_n.add(5);
        endconn_type_n.add(6);
        endconn_type_n.add(7);

        endconn_type_text.add("Normal end");
        endconn_type_text.add("CN failure - No Rej");
        endconn_type_text.add("RNS failure");
        endconn_type_text.add("User failure - No Rej");
        endconn_type_text.add("CN failure - Reject");
        endconn_type_text.add("User failure - Reject");
        endconn_type_text.add("Successful relocation");

        endconn_type_comment.add("The connection is closed normally");
        endconn_type_comment.add("Core Network failure different from an explicit reject of the initial request by the Core network");
        endconn_type_comment.add("Radio Network System failure");
        endconn_type_comment.add("User failure different from an explicit reject of the initial request by the Core Network");
        endconn_type_comment.add("Core Network explicit reject of the initial request due to CN problem");
        endconn_type_comment.add("Core Network explicit reject of the initial request due to User characteristics");
        endconn_type_comment.add("Connection is closed after successful relocation");

        all_endconn_phases = new ArrayList();
        endconn_phase_n = new ArrayList();
        endconn_phase_text = new ArrayList();
        endconn_phase_comment = new ArrayList();
        all_endconn_phases.add(endconn_phase_n);
        all_endconn_phases.add(endconn_phase_text);
        all_endconn_phases.add(endconn_phase_comment);

        endconn_phase_n.add(1);
        endconn_phase_n.add(2);
        endconn_phase_n.add(3);
        endconn_phase_n.add(4);
        endconn_phase_n.add(5);
        endconn_phase_n.add(6);
        endconn_phase_n.add(7);

        endconn_phase_text.add("Service initial access");
        endconn_phase_text.add("Call transaction setup");
        endconn_phase_text.add("ringing");
        endconn_phase_text.add("established call");
        endconn_phase_text.add("FO pending");
        endconn_phase_text.add("Incoming relocation");
        endconn_phase_text.add("RAB re-assignment");

        endconn_phase_comment.add("Initial access to the service (identification and security procedures)");
        endconn_phase_comment.add("Call transaction setup until ringing");
        endconn_phase_comment.add("Ringing phase (starts at progress or alerting message)");
        endconn_phase_comment.add("CS call is established or PS session is activated or reactivated");
        endconn_phase_comment.add("Follow-on request pending");
        endconn_phase_comment.add("Failure during incoming relocation procedure");
        endconn_phase_comment.add("RAB assignment for an already open PDP context");
    }

    private void init_sectors() {
        Connection conn_celdas2G = null;
        Statement stmnt_celdas2G = null;
        String query_celdas2G = null;
        ResultSet rs_celdas2G = null;

        all_sectors = new ArrayList();
        sector_cellid = new ArrayList();
        sector_name = new ArrayList();
        all_sectors.add(sector_cellid);
        all_sectors.add(sector_name);

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn_celdas2G = DriverManager.getConnection("jdbc:ucanaccess://vip.accdb");
            stmnt_celdas2G = conn_celdas2G.createStatement();
            query_celdas2G = "select cellid,name from sectors";
            rs_celdas2G = stmnt_celdas2G.executeQuery(query_celdas2G);
            while (rs_celdas2G.next()) {
                sector_cellid.add(rs_celdas2G.getInt("cellid"));
                sector_name.add(rs_celdas2G.getString("name"));
            }
        } catch (ClassNotFoundException e1) {
            System.err.println(e1);
        } catch (SQLException e1) {
            System.err.println(e1);
        } finally {
            try {
                if (stmnt_celdas2G != null) {
                    stmnt_celdas2G.close();
                }
                if (conn_celdas2G != null) {
                    conn_celdas2G.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        System.out.println("Sectores importados: " + sector_cellid.size());
    }

    private String getsector(int f_cellid) {
        if (sector_cellid.indexOf(f_cellid) == -1) {
            return "Cell_not_found" + f_cellid;
        } else {
            return sector_name.get(sector_cellid.indexOf(f_cellid));
        }
    }
}

class qquery implements Comparable<qquery> {

    public boolean is_3g;
    public Date start_date;
    public Date end_date;
    public String start_cellid;
    public String cellid;
    public String ultimo_evento;
    public String causa;
    public String primer_evento;
    public String numero;
    public Long imei;
    public String imsi;
    public String tipo;

    public qquery(boolean f_is_3g, Date f_start_date, Date f_end_date, String f_start_cellid,
            String f_cellid, String f_ultimoevento, String f_causa, String f_primerevento,
            String f_numero, Long f_imei, String f_imsi, String f_tipo) {
        is_3g = f_is_3g;
        start_date = f_start_date;
        end_date = f_end_date;
        start_cellid = f_start_cellid;
        cellid = f_cellid;
        ultimo_evento = f_ultimoevento;
        causa = f_causa;
        primer_evento = f_primerevento;
        numero = f_numero;
        imei = f_imei;
        imsi = f_imsi;
        tipo = f_tipo;
    }

    public int compareTo(qquery other) {
        return start_date.compareTo(other.start_date);
    }
}
