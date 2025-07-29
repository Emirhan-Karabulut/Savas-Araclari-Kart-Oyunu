public abstract class KaraAraci extends SavasAraci {
    public KaraAraci(int seviyePuani) {
        super(seviyePuani);
    }
    
    public abstract String getAltSinif();
    public abstract void setAltSinif(String altSinif);
    
    public abstract int getDenizVurusAvantaji();
    public abstract void setDenizVurusAvantaji(int denizVurusAvantaji);
}