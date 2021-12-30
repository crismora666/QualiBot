/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oncallvirtual;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author NA002456
 */
public class Chatter  implements Comparable<Chatter>{
    public String chattername;
    public String chatterchattext;
    public String chatterchatid;                
    public int chatterstatlevel;
    public int chattertick;
    public Map<Integer, String> chattersitesmap;
    public int[] chatterstats; 

    public Chatter(String f_chattername, String f_chattertext, String f_chatterid){
        chattername = f_chattername;
        chatterchattext = f_chattertext;
        chatterchatid = f_chatterid;
        chatterstatlevel = 0;
        chattertick = 0;
        chattersitesmap = new HashMap();
        chatterstats = new int[10];
    }

    @Override
    public int compareTo(Chatter other) {
        return chattername.compareTo(other.chattername);
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Chatter){
            Chatter echatter = (Chatter) obj;
            return this.chattername.compareTo(echatter.chattername) == 0;
        } else{
            throw new IllegalArgumentException("Chatter expected");
        }
    }    
}
