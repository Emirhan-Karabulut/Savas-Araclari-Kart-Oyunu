public abstract class HavaAraci extends SavasAraci {
    public HavaAraci(int seviyePuani) {
        super(seviyePuani);
    }
    
    public abstract String getAltSinif();
    public abstract void setAltSinif(String altSinif);
    
    public abstract int getKaraVurusAvantaji();
    public abstract void setKaraVurusAvantaji(int karaVurusAvantaji);
}