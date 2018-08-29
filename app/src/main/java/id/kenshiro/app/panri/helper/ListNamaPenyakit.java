package id.kenshiro.app.panri.helper;

public class ListNamaPenyakit {
    public String name;
    public String latin;
    public ListNamaPenyakit(String name, String latin){
        this.name = name;
        this.latin = latin;
    }
    public String getName(){
        return name;
    }
    public String getLatin(){
        return latin;
    }
    public void setLatin(String latin){
        this.latin = latin;
    }
}
