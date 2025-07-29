public abstract class SavasAraci {
    public int seviyePuani;

    public SavasAraci(int seviyePuani) {
        this.seviyePuani = seviyePuani;
    }

    public abstract int getDayaniklilik();
    public abstract void setDayaniklilik(int dayaniklilik);

    public abstract String getSinif();
    public abstract String getAltSinif();
    public abstract void setSinif(String sinif);

    public abstract int getVurus();
    public abstract void setVurus(int vurus);

    public int getSeviyePuani() {
        return seviyePuani;
    }

    public void setSeviyePuani(int seviyePuani) {
        this.seviyePuani = seviyePuani;
    }

    public void kartPuaniGoster(StringBuilder log) {
        log.append("➤ ")
           .append(getAltSinif())
           .append(":\n")
           .append("   • Dayanıklılık: ")
           .append(getDayaniklilik())
           .append("\n")
           .append("   • Vuruş Gücü: ")
           .append(getVurus())
           .append("\n")
           .append("   • Seviye: ")
           .append(getSeviyePuani())
           .append("\n");
    }
    
    public int baslangicSeviyePuani;


    public abstract void durumGuncelle(int saldiriDegeri, int seviyePuaniArtisi);
}
