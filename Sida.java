public class Sida extends DenizAraci {
    private int dayaniklilik;
    private String sinif;
    private int vurus;
    private String altSinif;
    private int havaVurusAvantaji;
    private int karaVurusAvantaji;
    private int seviyePuani; 

    public Sida() {
        super(0);
        this.dayaniklilik = 15;
        this.sinif = "Deniz";
        this.altSinif = "Sida";
        this.vurus = 10;
        this.havaVurusAvantaji = 10;
        this.karaVurusAvantaji = 10;
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
    public int getHavaVurusAvantaji() {
        return havaVurusAvantaji;
    }

    @Override
    public void setHavaVurusAvantaji(int havaVurusAvantaji) {
        this.havaVurusAvantaji = havaVurusAvantaji;
    }

    @Override
    public void durumGuncelle(int saldiriDegeri, int seviyePuaniArtisi) {
        if(saldiriDegeri != 0)
        setDayaniklilik(getDayaniklilik() - saldiriDegeri);
        if(seviyePuaniArtisi != 0)
        setSeviyePuani(getSeviyePuani() + seviyePuaniArtisi);
    }
    
    public int getKaraVurusAvantaji() {
        return karaVurusAvantaji;
    }

    public void setKaraVurusAvantaji(int karaVurusAvantaji) {
        this.karaVurusAvantaji = karaVurusAvantaji;
    }

    public int getSeviyePuani() {
        return seviyePuani;
    }

    public void setSeviyePuani(int seviyePuani) {
        this.seviyePuani = seviyePuani;
    }

    
}
