package id.kenshiro.app.panri.helper;

import java.util.ArrayList;
import java.util.List;

public class ListCiriCiriPenyakit {
    public String ciri;
    public boolean usefirst_flags;
    public boolean ask_flags;
    public List<Integer> listused_flags;
    // special for lists
    public int listused_mode_flags;
    public List<Integer> pointo_flags;
    public static final int MODE_BIND = 0xfff;
    public static final int MODE_SEQUENCE = 0xaa2;
    public ListCiriCiriPenyakit(String ciri, boolean usefirst_flags, boolean ask_flags){
        this.ciri = ciri;
        this.usefirst_flags = usefirst_flags;
        this.ask_flags = ask_flags;
    }

    public void setUsefirst_flags(boolean usefirst_flags) {
        this.usefirst_flags = usefirst_flags;
    }

    public void setAsk_flags(boolean ask_flags) {
        this.ask_flags = ask_flags;
    }

    public void setListused_flags(String listused) {
        if(listused == null || listused.equals(""))return;
        int len = listused.length();
        listused_flags = new ArrayList<Integer>();
        listused_mode_flags = MODE_SEQUENCE;
        char splitter = ',';
        // CHECK THE METHOD
        for(int x = 0; x < len; x++){
            if (listused.charAt(x) == '-') {
                listused_mode_flags = MODE_BIND;
                splitter = '-';
                break;
            }
            else if (listused.charAt(x) == ','){
                listused_mode_flags = MODE_SEQUENCE;
                splitter = ',';
                break;
            }
        }
        // split and grow into the array
        String[] s = listused.split(""+splitter);
        // change the values into integer
        for(int x = 0; x < s.length; x++)
            listused_flags.add(Integer.parseInt(s[x]));

    }

    public void setPointo_flags(String pointo) {
        pointo_flags = new ArrayList<Integer>();
        char splitter = ',';
        // split and grow into the array
        String[] s = pointo.split(""+splitter);
        // change the values into integer
        for(int x = 0; x < s.length; x++)
            pointo_flags.add(Integer.parseInt(s[x]));
    }

    public List<Integer> getListused_flags() {
        return listused_flags;
    }

    public int getListused_mode_flags() {
        return listused_mode_flags;
    }

    public String getCiri() {
        return ciri;
    }

    public List<Integer> getPointo_flags() {
        return pointo_flags;
    }

    public boolean isAsk_flags() {
        return ask_flags;
    }

    public boolean isUsefirst_flags() {
        return usefirst_flags;
    }
}