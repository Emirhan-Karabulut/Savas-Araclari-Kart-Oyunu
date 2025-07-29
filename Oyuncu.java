import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Oyuncu {
    private int oyuncuID;
    private String oyuncuAdi;
    private int skor;
    private ArrayList<SavasAraci> kartListesi;
    private HashSet<SavasAraci> secilenKartlar;
    private ArrayList<Integer> kullaniciSecimleri;

    public Oyuncu(int oyuncuID, String oyuncuAdi, int skor) {
        this.oyuncuID = oyuncuID;
        this.oyuncuAdi = oyuncuAdi;
        this.skor = skor;
        this.kartListesi = new ArrayList<>();
        this.secilenKartlar = new HashSet<>();
        this.kullaniciSecimleri = new ArrayList<>();
    }

    public void ekleKart(SavasAraci kart) {
        this.kartListesi.add(kart);
    }

    public void setKullaniciSecimleri(ArrayList<Integer> secimler) {
        this.kullaniciSecimleri = secimler;
    }

    public ArrayList<SavasAraci> kartSec(int sayisi) {
        ArrayList<SavasAraci> secilenKartlarListesi = new ArrayList<>();
        Random random = new Random();
    
        if (this.oyuncuID == 1) {
            for (Integer index : kullaniciSecimleri) {
                SavasAraci secilenKart = this.kartListesi.get(index);
                secilenKartlarListesi.add(secilenKart);
                this.secilenKartlar.add(secilenKart);
            }
        } else {
            ArrayList<SavasAraci> kullanilabilirKartlar = new ArrayList<>();
            ArrayList<SavasAraci> kilitliKartlar = new ArrayList<>();

            for (SavasAraci kart : this.kartListesi) {
                if (!this.getSecilenKartlar().contains(kart)) {
                    kullanilabilirKartlar.add(kart);
                } else {
                    kilitliKartlar.add(kart);
                }
            }

            while (secilenKartlarListesi.size() < sayisi && !kullanilabilirKartlar.isEmpty()) {
                int randomIndex = random.nextInt(kullanilabilirKartlar.size());
                SavasAraci secilenKart = kullanilabilirKartlar.remove(randomIndex);
                secilenKartlarListesi.add(secilenKart);
                this.secilenKartlar.add(secilenKart); 
            }
    
            while (secilenKartlarListesi.size() < sayisi && !kilitliKartlar.isEmpty()) {
                int randomIndex = random.nextInt(kilitliKartlar.size());
                SavasAraci secilenKart = kilitliKartlar.get(randomIndex);
                this.secilenKartlar.clear();
                secilenKartlarListesi.add(secilenKart);
                this.secilenKartlar.add(secilenKart);
                System.out.println(secilenKart);
            }

        }
    
        return secilenKartlarListesi;
    }
    
    public HashSet<SavasAraci> getSecilenKartlar() {
        return this.secilenKartlar;
    }

    public boolean hasCardBeenSelected(SavasAraci kart){ 
        return this.secilenKartlar.contains(kart); 
    }

    public int getOyuncuID() {
        return this.oyuncuID;
    }

    public String getOyuncuAdi() {
        return this.oyuncuAdi;
    }

    public int SkorGoster() {
        return this.skor;
    }

    public void setSkor(int skor) {
        this.skor = skor;
    }

    public ArrayList<SavasAraci> getKartListesi() {
        return this.kartListesi;
    }    
        public void kartlariTemizle() {
            this.kartListesi.clear();
        }
    
        public void kartlariSifirla() {
            this.kartListesi = new ArrayList<>();
        }
    
        public int getToplamSeviye() {
            int toplamSeviye = 0;
            for (SavasAraci kart : this.kartListesi) {
                toplamSeviye += kart.getSeviyePuani();
            }
            return toplamSeviye;
        }

        public int toplamDayaniklilik() {
        int toplamDayaniklilik = 0;
        for (SavasAraci kart : this.kartListesi) {
            toplamDayaniklilik += kart.getDayaniklilik();
        }
        return toplamDayaniklilik;
    }
    
}