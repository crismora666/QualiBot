/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oncallvirtual;

import java.util.Date;

/**
 *
 * @author NA002456
 */
public class qregister3g  implements Comparable<qregister3g>{
    public Date fecha;
    public String name;
    public float efi_voz;
    public float efi_datos;
    public float hs_user;
    public float rtwp;        
    public float frame_loss;
    public float voz_erl;
    public float datos_mb;
    public float disponibilidad;

    public qregister3g(Date f_fecha, String f_name, float f_efi_voz, float f_efi_datos, float f_hs_user, float f_rtwp, float f_frame_loss, float f_voz_erl, float f_datos_mb, float f_disponibilidad){
        fecha = f_fecha;
        name = f_name;
        efi_voz = f_efi_voz;
        efi_datos = f_efi_datos;
        hs_user = f_hs_user;
        rtwp = f_rtwp;
        frame_loss = f_frame_loss;
        voz_erl = f_voz_erl;
        datos_mb = f_datos_mb;
        disponibilidad = f_disponibilidad;
    }

    @Override
    public int compareTo(qregister3g other) {
        return name.compareTo(other.name);
    }    
}
