public abstract class DenizAraci extends SavasAraci {
    public DenizAraci(int seviyePuani) {
        super(seviyePuani);
    }
    
    public abstract String getAltSinif();
    public abstract void setAltSinif(String altSinif);
    
    public abstract int getHavaVurusAvantaji();
    public abstract void setHavaVurusAvantaji(int havaVurusAvantaji);
}