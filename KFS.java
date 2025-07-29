public class KFS extends KaraAraci {
    private int dayaniklilik;
    private String sinif;
    private int vurus;
    private String altSinif;
    private int denizVurusAvantaji;
    private int havaVurusAvantaji;
    private int seviyePuani;

    public KFS() {
        super(0);
        this.dayaniklilik = 10;
        this.sinif = "Kara";
        this.altSinif = "KFS";
        this.vurus = 10;
        this.denizVurusAvantaji = 10;
        this.havaVurusAvantaji = 20;
        this.seviyePuani = 0;
    }

    @Override
    public int getDayaniklilik() {
        return dayaniklilik;
    }

    @Override
    public void setDayaniklilik(int dayaniklilik) {
        this.dayaniklilik = dayaniklilik;
    }

    @Override
    public String getSinif() {
        return sinif;
    }

    @Override
    public void setSinif(String sinif) {
        this.sinif = sinif;
    }

    @Override
    public int getVurus() {
        return vurus;
    }

    @Override
    public void setVurus(int vurus) {
        this.vurus = vurus;
    }

    @Override
    public String getAltSinif() {
        return altSinif;
    }

    @Override
    public void setAltSinif(String altSinif) {
        this.altSinif = altSinif;
    }

    @Override
    public int getDenizVurusAvantaji() {
        return denizVurusAvantaji;
    }

    @Override
    public void setDenizVurusAvantaji(int denizVurusAvantaji) {
        this.denizVurusAvantaji = denizVurusAvantaji;
    }

    @Override
    public void durumGuncelle(int saldiriDegeri, int seviyePuaniArtisi) {
        if(saldiriDegeri != 0)
        setDayaniklilik(getDayaniklilik() - saldiriDegeri);
        if(seviyePuaniArtisi != 0)
        setSeviyePuani(getSeviyePuani() + seviyePuaniArtisi);
    }
    
    public int getHavaVurusAvantaji() {
        return havaVurusAvantaji;
    }

    public void setHavaVurusAvantaji(int havaVurusAvantaji) {
        this.havaVurusAvantaji = havaVurusAvantaji;
    }

    public int getSeviyePuani() {
        return seviyePuani;
    }

    public void setSeviyePuani(int seviyePuani) {
        this.seviyePuani = seviyePuani;
    }

    
}
