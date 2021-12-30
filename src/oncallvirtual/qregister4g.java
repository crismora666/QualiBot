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
public class qregister4g  implements Comparable<qregister4g>{
    public Date fecha;
    public String name;
    public float efi_datos;
    public float users;
    public float datos_mb;
    public float disponibilidad;

    public qregister4g(Date f_fecha, String f_name, float f_efi_datos, float f_users, float f_datos_mb, float f_disponibilidad){
        fecha = f_fecha;
        name = f_name;
        efi_datos = f_efi_datos;
        users = f_users;
        datos_mb = f_datos_mb;
        disponibilidad = f_disponibilidad;
    }

    @Override
    public int compareTo(qregister4g other) {
        return name.compareTo(other.name);
    }    
}
