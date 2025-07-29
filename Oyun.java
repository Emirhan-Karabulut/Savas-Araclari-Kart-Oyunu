import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class Oyun extends JFrame {
    private static final int MAX_HAMLE = 5;
    private static final int SEVIYE_SINIRI = 20;
    private static int game_number=1;
    private JPanel playerCardsPanel, computerCardsPanel;
    private JLabel playerScoreLabel, computerScoreLabel, turnLabel;
    private ArrayList<JButton> playerCards;
    private ArrayList<JButton> computerCards;
    private ArrayList<Integer> selectedPlayerCards;
    private int turn = 0;
    private Oyuncu kullanici;
    private Oyuncu bilgisayar;
    private HashSet<SavasAraci> kullaniciSecilenKartSeti = new HashSet<>();
    private JPanel playerBattleCards;
    private JPanel computerBattleCards;
    private JTextArea playerLogArea;
    private JTextArea computerLogArea;
    private Map<SavasAraci, Integer> roundStartLevels = new HashMap<>();
    private static PrintWriter writer;
    private static String currentLogFile;
    private static Map<String, Integer> previousStats = new HashMap<>();
    private JButton attackButton;
    private Clip attackSound;
    private Clip selectSound;

    public Oyun(String kullaniciIsmi) {
        cleanupOldLogs();
        setTitle("Kart Savaşı Oyunu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        kullanici = new Oyuncu(1, kullaniciIsmi, 0);
        bilgisayar = new Oyuncu(2, "Bilgisayar", 0);
        baslangicKartlariEkle(kullanici);
        baslangicKartlariEkle(bilgisayar);
        selectedPlayerCards = new ArrayList<>();
        initializeSound();
        createUI();
        updateUI();
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(40, 44, 52));
    
        JPanel scorePanel = new JPanel(new GridLayout(1, 3, 20, 0));
        scorePanel.setOpaque(false);
        
        playerScoreLabel = createStyledLabel(kullanici.getOyuncuAdi() +" Skoru: 0");
        computerScoreLabel = createStyledLabel("Bilgisayar Skoru: 0");
        turnLabel = createStyledLabel("Tur: 1/" + MAX_HAMLE);
        
        scorePanel.add(playerScoreLabel);
        scorePanel.add(turnLabel);
        scorePanel.add(computerScoreLabel);
        mainPanel.add(scorePanel, BorderLayout.NORTH);
    
        JPanel gamePanel = new JPanel(new BorderLayout(20, 20));
        gamePanel.setOpaque(false);
        
        JPanel playerPanel = createStyledCardPanel("Senin Kartların");
        playerCardsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        playerCardsPanel.setOpaque(false);
        playerCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playerPanel.add(playerCardsPanel, BorderLayout.CENTER);
        
        JPanel computerPanel = createStyledCardPanel("Bilgisayar Kartları");
        computerCardsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        computerCardsPanel.setOpaque(false);
        computerCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        computerPanel.add(computerCardsPanel, BorderLayout.CENTER);

        JPanel battlefieldPanel = createStyledCardPanel("Savaş Alanı");
        battlefieldPanel.setLayout(new BoxLayout(battlefieldPanel, BoxLayout.Y_AXIS));

        computerBattleCards = new JPanel(new GridLayout(1, 3, 5, 0));
        computerBattleCards.setOpaque(false);
        computerBattleCards.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1),"Bilgisayar Savaş Kartları",TitledBorder.CENTER,TitledBorder.TOP,new Font("Arial", Font.BOLD, 12),Color.WHITE));
        computerBattleCards.setPreferredSize(new Dimension(500, 120));
        
        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);
        spacerPanel.setPreferredSize(new Dimension(500, 20));
        
        playerBattleCards = new JPanel(new GridLayout(1, 3, 5, 0));
        playerBattleCards.setOpaque(false);
        playerBattleCards.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 1),kullanici.getOyuncuAdi()+" Savaş Kartları",TitledBorder.CENTER,TitledBorder.TOP,new Font("Arial", Font.BOLD, 12),Color.WHITE));
        playerBattleCards.setPreferredSize(new Dimension(500, 120));

        battlefieldPanel.add(computerBattleCards);
        battlefieldPanel.add(spacerPanel);
        battlefieldPanel.add(playerBattleCards);
        
        JPanel gameAreaPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        gameAreaPanel.setOpaque(false);
        gameAreaPanel.add(computerPanel);
        gameAreaPanel.add(battlefieldPanel);
        gameAreaPanel.add(playerPanel);
        
        JPanel logPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        logPanel.setOpaque(false);
        logPanel.setPreferredSize(new Dimension(300, getHeight()));
        
        JPanel playerLogPanel = new JPanel(new BorderLayout());
        playerLogPanel.setOpaque(false);
        playerLogArea = createStyledLogArea("Oyuncu Savaş Logu");
        JScrollPane playerLogScroll = new JScrollPane(playerLogArea);
        playerLogScroll.setBorder(createStyledTitledBorder("Oyuncu Logu"));
        playerLogPanel.add(playerLogScroll, BorderLayout.CENTER);
        
        JPanel computerLogPanel = new JPanel(new BorderLayout());
        computerLogPanel.setOpaque(false);
        computerLogArea = createStyledLogArea("Bilgisayar Savaş Logu");
        JScrollPane computerLogScroll = new JScrollPane(computerLogArea);
        computerLogScroll.setBorder(createStyledTitledBorder("Bilgisayar Logu"));
        computerLogPanel.add(computerLogScroll, BorderLayout.CENTER);
        
        logPanel.add(playerLogPanel);
        logPanel.add(computerLogPanel);
        
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setOpaque(false);
        mainSplitPane.setBorder(null);
        mainSplitPane.setDividerSize(3);
        mainSplitPane.setLeftComponent(gameAreaPanel);
        mainSplitPane.setRightComponent(logPanel);
        mainSplitPane.setDividerLocation(5000);
        
        gamePanel.add(mainSplitPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setOpaque(false);
        
        attackButton = createStyledButton("Hamle Yap", new Color(70, 130, 180));
        JButton newGameButton = createStyledButton("Yeni Oyun", new Color(60, 179, 113));
        
        attackButton.addActionListener(e -> {
            playAttackSound();
            makeMove();
        });

        newGameButton.addActionListener(e -> {
            playAttackSound();
            resetGame(); 
        });
        
        controlPanel.add(attackButton);
        controlPanel.add(newGameButton);
        
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    public static void cleanupOldLogs() {
        File directory = new File("game_logs");
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().startsWith("game")) {
                        file.delete();
                    }
                }
            }
        }
    }

    public static void initializeLogger(int turn) {
        try {
            File directory = new File("game_logs");
            if (!directory.exists()) {
                directory.mkdir();
            }
            
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            currentLogFile = directory + "/game" + game_number + "_log_turn_" + (turn+1) + "_" + now.format(fileFormatter) + ".txt";
            writer = new PrintWriter(new FileWriter(currentLogFile, true));
            
            logHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeSound() {
        try {
            InputStream attackInputStream = getClass().getResourceAsStream("/attack.wav");
            if (attackInputStream != null) {
                AudioInputStream attackAudioStream = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(attackInputStream));
                attackSound = AudioSystem.getClip();
                attackSound.open(attackAudioStream);
            } else {
                System.err.println("Attack sound file not found!");
            }

            InputStream selectInputStream = getClass().getResourceAsStream("/card-sound.wav");
            if (selectInputStream != null) {
                AudioInputStream selectAudioStream = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(selectInputStream));
                selectSound = AudioSystem.getClip();
                selectSound.open(selectAudioStream);
            } else {
                System.err.println("Card selection sound file not found!");
            }
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file format: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio system unavailable: " + e.getMessage());
        }
    }

    private void playAttackSound() {
        if (attackSound != null && attackSound.isOpen()) {
            attackSound.setFramePosition(0);
            attackSound.start();
        }
    }
    
    private void playSelectSound() {
        if (selectSound != null && selectSound.isOpen()) {
            selectSound.setFramePosition(0);
            selectSound.start();
        }
    }

    private static void logHeader() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        
        StringBuilder header = new StringBuilder();
        header.append("🎮 DETAYLI OYUN KAYDI 🎮\n")
              .append("════════════════════════\n\n")
              .append("📅 Başlangıç Zamanı: ")
              .append(now.format(headerFormatter))
              .append("\n═══════════════════════════════════════\n\n");
        
        writeToLog(header.toString());
    }

    public static void logBattleStart(ArrayList<SavasAraci> playerCards, ArrayList<SavasAraci> computerCards, int turn) {
        StringBuilder battleLog = new StringBuilder();
        battleLog.append("\n🔄 ROUND ")
                 .append(turn+1)
                 .append(" BAŞLANGIÇ DURUMU\n")
                 .append("════════════════════════════════\n\n");
        
        battleLog.append("👤 KULLANICI KARTLARI:\n");
        logUnitDetails(battleLog, playerCards, "Kullanıcı");
        
        battleLog.append("\n🤖 BİLGİSAYAR KARTLARI:\n");
        logUnitDetails(battleLog, computerCards, "Bilgisayar");
        
        writeToLog(battleLog.toString());
    }

    private static void logUnitDetails(StringBuilder log, ArrayList<SavasAraci> units, String owner) {
        for (SavasAraci unit : units) {
            String key = owner + "_" + unit.getAltSinif();
            previousStats.put(key + "_dayaniklilik", unit.getDayaniklilik());
            previousStats.put(key + "_seviye", unit.getSeviyePuani());
    
            unit.kartPuaniGoster(log);
        }
    }
    
    public static void logAttack(SavasAraci attacker, SavasAraci target, int damage, 
                               String attackerSide, String advantageType, int advantageBonus) {
        StringBuilder attackLog = new StringBuilder();
        attackLog.append("\n⚔️ SALDIRI DETAYI\n")
                .append("──────────────────\n");
        
        attackLog.append("🎯 Saldıran (")
                 .append(attackerSide)
                 .append("): ")
                 .append(attacker.getAltSinif())
                 .append("\n")
                 .append("💥 Hedef: ")
                 .append(target.getAltSinif())
                 .append("\n")
                 .append("📊 Vuruş Gücü: ")
                 .append(attacker.getVurus());
        
        if (!advantageType.isEmpty()) {
            attackLog.append("\n🔥 ")
                     .append(advantageType)
                     .append(" Avantajı: +")
                     .append(advantageBonus);
        }
        
        attackLog.append("\n💫 Toplam Hasar: ")
                 .append(damage)
                 .append("\n❤️ Hedef Kalan Can: ")
                 .append(target.getDayaniklilik())
                 .append("\n──────────────────\n");
        
        writeToLog(attackLog.toString());
    }

    public static void logLevelUp(SavasAraci unit, String side, int experienceGained) {
        String key = side + "_" + unit.getAltSinif();
        int previousLevel = previousStats.getOrDefault(key + "_seviye", 0);
        
        if (unit.getSeviyePuani() > previousLevel) {
            StringBuilder levelLog = new StringBuilder();
            levelLog.append("\n📈 SEVİYE ATLAMA\n")
                    .append("──────────────────\n")
                    .append("🎮 ")
                    .append(side)
                    .append(" - ")
                    .append(unit.getAltSinif())
                    .append("\n📊 Önceki Seviye: ")
                    .append(previousLevel)
                    .append("\n📈 Yeni Seviye: ")
                    .append(unit.getSeviyePuani())
                    .append("\n💫 Kazanılan XP: ")
                    .append(experienceGained)
                    .append("\n──────────────────\n");
            
            writeToLog(levelLog.toString());
            previousStats.put(key + "_seviye", unit.getSeviyePuani());
        }
    }

    public static void logCardDestruction(SavasAraci destroyedUnit, String side) {
        StringBuilder destructionLog = new StringBuilder();
        destructionLog.append("\n💥 KART YOK EDİLDİ\n")
                      .append("──────────────────\n")
                      .append("👥 Taraf: ")
                      .append(side)
                      .append("\n")
                      .append("🎴 Kart: ")
                      .append(destroyedUnit.getAltSinif())
                      .append("\n──────────────────\n");
        
        writeToLog(destructionLog.toString());
    }

    public static void logRoundEnd(int playerScore, int computerScore, 
                                 ArrayList<SavasAraci> playerCards, 
                                 ArrayList<SavasAraci> computerCards) {
        StringBuilder roundLog = new StringBuilder();
        roundLog.append("\n🏁 ROUND SONU DURUMU\n")
                .append("══════════════════════\n\n")
                .append("📊 SKORLAR:\n")
                .append("👤 Kullanıcı: ")
                .append(playerScore)
                .append("\n")
                .append("🤖 Bilgisayar: ")
                .append(computerScore)
                .append("\n\n")
                .append("\n══════════════════════\n");
        
        writeToLog(roundLog.toString());
    }

    public static void logGameEnd(String winner, String reason, int playerScore, int computerScore) {
        StringBuilder endLog = new StringBuilder();
        endLog.append("\n🏆 OYUN SONU\n")
              .append("══════════════════════\n")
              .append("👑 Kazanan: ")
              .append(winner)
              .append("\n")
              .append("📝 Sebep: ")
              .append(reason)
              .append("\n\n")
              .append("📊 FİNAL SKORLARI:\n")
              .append("👤 Kullanıcı: ")
              .append(playerScore)
              .append("\n")
              .append("🤖 Bilgisayar: ")
              .append(computerScore)
              .append("\n══════════════════════\n");
        
        writeToLog(endLog.toString());
        closeLogger();
    }

    private static void writeToLog(String message) {
        if (writer != null) {
            writer.write(message + "\n");
            writer.flush();
        }
    }

    public static void closeLogger() {
        if (writer != null) {
            writer.close();
        }
    }

    private JTextArea createStyledLogArea(String title) {
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(30, 33, 39));
        logArea.setForeground(Color.WHITE);
        logArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return logArea;
    }

    private void logPlayerMessage(String message) {
        playerLogArea.append("⚔️ " + message + "\n");
        playerLogArea.setCaretPosition(playerLogArea.getDocument().getLength());
    }

    private void logComputerMessage(String message) {
        computerLogArea.append("🤖 " + message + "\n");
        computerLogArea.setCaretPosition(computerLogArea.getDocument().getLength());
    }

    private void logBattleResult(String message, String taraf) {
        if (taraf.equals("Kullanıcı")) {
            logPlayerMessage("\n=== SAVAŞ SONUCU ===\n" + message);
        } else {
            logComputerMessage("\n=== SAVAŞ SONUCU ===\n" + message);
        }
    }

    private void updateUI() {
        playerScoreLabel.setText(kullanici.getOyuncuAdi()+" Skoru: " + kullanici.SkorGoster());
        computerScoreLabel.setText("Bilgisayar Skoru: " + bilgisayar.SkorGoster());
        turnLabel.setText("Tur: " + (turn + 1) + "/" + MAX_HAMLE);

        updatePlayerCards();
        updateComputerCards();
    }

    private void displayBattleCards(ArrayList<SavasAraci> playerAttackCards, ArrayList<SavasAraci> computerAttackCards) {
        playerBattleCards.removeAll();
        computerBattleCards.removeAll();
    
        for (SavasAraci card : playerAttackCards) {
            playerBattleCards.add(createCardButton(card, true, true));
        }
    
        for (SavasAraci card : computerAttackCards) {
            computerBattleCards.add(createCardButton(card, false, true));
        }
    
        playerBattleCards.revalidate();
        playerBattleCards.repaint();
        computerBattleCards.revalidate();
        computerBattleCards.repaint();
    }
    
    private JButton createCardButton(SavasAraci card, boolean isPlayerCard, boolean isBattleCard) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(120, 160));
        btn.setLayout(new BorderLayout());
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        btn.setBackground(new Color(0, 33, 39));
        btn.setFocusPainted(false);
    
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        cardPanel.setOpaque(false);
        if (!isPlayerCard && !isBattleCard) {
            JLabel hiddenLabel = new JLabel("?????");
            hiddenLabel.setFont(new Font("Arial", Font.BOLD, 14));
            hiddenLabel.setForeground(Color.WHITE);
            hiddenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(hiddenLabel);
        } else {
            JLabel typeLabel = new JLabel(card.getAltSinif());
            typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
            typeLabel.setForeground(Color.WHITE);
            typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardPanel.add(typeLabel);
            cardPanel.add(Box.createVerticalStrut(3));
    
            addCardStat(cardPanel, "Vuruş: " + card.getVurus(), 12);
            addCardStat(cardPanel, "Dayanıklılık: " + card.getDayaniklilik(), 12);
            addCardStat(cardPanel, "Seviye Puanı: " + card.getSeviyePuani(), 12);
        }
    
        if (isPlayerCard) {
            btn.setBackground(Color.BLUE);
            btn.addActionListener(e -> {
                int index = playerCards.indexOf(btn);
                SavasAraci selectedCard = kullanici.getKartListesi().get(index);
                int requiredCards = 3;
    
                if (selectedPlayerCards.contains(index)) {
                    selectedPlayerCards.remove(Integer.valueOf(index));
                    btn.setBackground(Color.BLUE);
                } 
                else if (selectedPlayerCards.size() < requiredCards) {
                    if (!kullaniciSecilenKartSeti.contains(selectedCard) || 
                        kullaniciSecilenKartSeti.size() >= kullanici.getKartListesi().size()) {
                        selectedPlayerCards.add(index);
                        btn.setBackground(new Color(135, 206, 250));
                    } else {
                        StringBuilder unusableCards = new StringBuilder();
                        for (SavasAraci unusableCard : kullaniciSecilenKartSeti) {
                            if (kullaniciSecilenKartSeti.size() < kullanici.getKartListesi().size()) {
                                unusableCards.append(unusableCard.getAltSinif()).append(", ");
                            }
                        }
                    }
                }
                updatePlayerCards();
            });
        }
    
        btn.add(cardPanel, BorderLayout.CENTER);
        return btn;
    }

    private void addCardStat(JPanel panel, String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, fontSize));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel createStyledCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(createStyledTitledBorder(title));
        return panel;
    }

    private TitledBorder createStyledTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            title,
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            Color.WHITE
        );
        return border;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        return button;
    }
    
    private void updatePlayerCards() {
        playerCardsPanel.removeAll();
        playerCards = new ArrayList<>();
        ArrayList<Integer> unusedCardIndices = new ArrayList<>();
        
        for (int i = 0; i < kullanici.getKartListesi().size(); i++) {
            SavasAraci kart = kullanici.getKartListesi().get(i);
            if (!kullaniciSecilenKartSeti.contains(kart)) {
                unusedCardIndices.add(i);
            }
        }

        boolean allUnusedCardsSelected = true;
        for (Integer unusedIndex : unusedCardIndices) {
            if (!selectedPlayerCards.contains(unusedIndex)) {
                allUnusedCardsSelected = false;
                break;
            }
            
        }        

        if (!allUnusedCardsSelected) {
            selectedPlayerCards.removeIf(index -> !unusedCardIndices.contains(index));
        }

        Color DEFAULT_BACKGROUND = new Color(0, 33, 39);
        Color SELECTED_BACKGROUND = new Color(135, 206, 250);  
        Color LOCKED_BACKGROUND = new Color(50, 50, 50);      

        for (int index = 0; index < kullanici.getKartListesi().size(); index++) {
            SavasAraci card = kullanici.getKartListesi().get(index);
            JButton btn = createCardButton(card, true, false);
    
            boolean isUnusedCard = unusedCardIndices.contains(index);
            boolean isCardSelected = selectedPlayerCards.contains(index);

            if (isCardSelected) {
                btn.setBackground(SELECTED_BACKGROUND);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 0, 255), 2),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)
                ));
            } else if (!allUnusedCardsSelected && !isUnusedCard) {
                btn.setBackground(LOCKED_BACKGROUND);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)
                ));
            } else {
                btn.setBackground(DEFAULT_BACKGROUND);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)
                ));
            }
            if (!isUnusedCard) {
                btn.setEnabled(allUnusedCardsSelected);
                if (!allUnusedCardsSelected) {
                    btn.setToolTipText("Önce kullanılmamış kartları seçmelisiniz");
                } else {
                    btn.setToolTipText(null);
                }
            }
        btn.addActionListener(e -> {
            playSelectSound();
            
            int currentIndex = kullanici.getKartListesi().indexOf(card);
            int requiredCards = 3;

            if (selectedPlayerCards.contains(currentIndex)) {
                selectedPlayerCards.remove(Integer.valueOf(currentIndex));
            } else if (selectedPlayerCards.size() < requiredCards) {
                selectedPlayerCards.add(currentIndex);
            }
            updatePlayerCards();
        });
            playerCards.add(btn);
            playerCardsPanel.add(btn);
        }
    
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
    }
    
    private void updateComputerCards() {
        computerCardsPanel.removeAll();
        computerCards = new ArrayList<>();

        ArrayList<Integer> unusedCardIndices = new ArrayList<>();
        for (int i = 0; i < bilgisayar.getKartListesi().size(); i++) {
            SavasAraci kart = bilgisayar.getKartListesi().get(i);
            if (!kullaniciSecilenKartSeti.contains(kart)) {
                unusedCardIndices.add(i);
            }
        }
        
        boolean allUnusedCardsSelected = true;
        for (Integer unusedIndex : unusedCardIndices) {
            SavasAraci kart = bilgisayar.getKartListesi().get(unusedIndex);
            if (!bilgisayar.getSecilenKartlar().contains(kart)) {
                allUnusedCardsSelected = false;
                break;
            }
        }

        if (!allUnusedCardsSelected) {
            bilgisayar.getSecilenKartlar().removeIf(card -> !unusedCardIndices.contains(bilgisayar.getKartListesi().indexOf(card)));
        }
    
        Color DEFAULT_BACKGROUND = new Color(0, 33, 39);      
        Color SELECTED_BACKGROUND = new Color(135, 206, 250);  
        Color LOCKED_BACKGROUND = new Color(50, 50, 50);      
    
        for (int index = 0; index < bilgisayar.getKartListesi().size(); index++) {
            SavasAraci card = bilgisayar.getKartListesi().get(index);
            JButton btn = createCardButton(card, false, false);
    
            boolean isUnusedCard = unusedCardIndices.contains(index);
            boolean isCardSelected = bilgisayar.getSecilenKartlar().contains(card);

            if (isCardSelected) {
                btn.setBackground(SELECTED_BACKGROUND);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 0, 255), 2),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)
                ));
            } else if (!allUnusedCardsSelected && !isUnusedCard) {
                btn.setBackground(LOCKED_BACKGROUND);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)
                ));
                btn.setEnabled(false);
            } else {
                btn.setBackground(DEFAULT_BACKGROUND);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)
                ));
            }
    
            computerCards.add(btn);
            computerCardsPanel.add(btn);
        }
    
        computerCardsPanel.revalidate();
        computerCardsPanel.repaint();
    }
    
    private boolean sonRound = false;
    private HashSet<SavasAraci> computerSecilenKartSeti = new HashSet<>();
    
    private void makeMove() {
        if (selectedPlayerCards.size() != 3) {
            JOptionPane.showMessageDialog(this, "3 kart seçmelisiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bilgisayarKullanilabilirKartSayisi = 0;
        for (SavasAraci kart : bilgisayar.getKartListesi()) {
            if (!bilgisayar.hasCardBeenSelected(kart)) {
                bilgisayarKullanilabilirKartSayisi++;
            }
        }
    
        kullanici.setKullaniciSecimleri(selectedPlayerCards);
        
        ArrayList<SavasAraci> playerAttackCards = kullanici.kartSec(3);
        ArrayList<SavasAraci> computerAttackCards = bilgisayar.kartSec(3);
        
        ArrayList<Integer> originalDurability = new ArrayList<>();        
        ArrayList<Integer> computerOriginalDurability = new ArrayList<>();
    
        for (SavasAraci kart : playerAttackCards) {
            originalDurability.add(kart.getDayaniklilik());
            kullaniciSecilenKartSeti.add(kart);
        }
    
        for (SavasAraci card : computerAttackCards) {
            computerSecilenKartSeti.add(card);
            computerOriginalDurability.add(card.getDayaniklilik());
        }
        
        for (SavasAraci kart : playerAttackCards) {
            roundStartLevels.put(kart, kart.getSeviyePuani());
            if (bilgisayarKullanilabilirKartSayisi < 3) {
                kullaniciSecilenKartSeti.clear();
                kullaniciSecilenKartSeti.add(kart);
                }
        }

        for (SavasAraci kart : computerAttackCards) {
            roundStartLevels.put(kart, kart.getSeviyePuani());
        }

        selectedPlayerCards.clear();

        logRoundStatus(turn, true);
        initializeLogger(turn);
        logPlayerMessage("\n=== SAVAŞ BAŞLIYOR ===");
        logComputerMessage("\n=== SAVAŞ BAŞLIYOR ===");

        logBattleStart(playerAttackCards, computerAttackCards, turn);
        for (int i = 0; i < 3; i++) {
            SavasAraci playerCard = playerAttackCards.get(i);
            SavasAraci computerCard = computerAttackCards.get(i);
            
            playerCard.setDayaniklilik(originalDurability.get(i));
            computerCard.setDayaniklilik(computerOriginalDurability.get(i));
            
            saldiriHesapla(playerCard, computerCard, "Kullanıcı");
            saldiriHesapla(computerCard, playerCard, "Bilgisayar");
        }
    
        updatePlayerScores();
        logPlayerMessage("\n=== SAVAŞ SONU ===\n");
        logComputerMessage("\n=== SAVAŞ SONU ===\n");
        logRoundStatus(turn, false);

        displayBattleCards(playerAttackCards, computerAttackCards);
        temizleVeGuncelle();
    
        if (kullanici.getKartListesi().isEmpty() || bilgisayar.getKartListesi().isEmpty()) {
            logPlayerMessage("\n=== OYUN SONA ERDİ ===");
            logComputerMessage("\n=== OYUN SONA ERDİ ===");
            oyunSonuKontrol();
            return;
        }
    
        if (!sonRound) {
            kullanici.ekleKart(rastgeleYeniKart(kullanici.getToplamSeviye() >= SEVIYE_SINIRI));
            bilgisayar.ekleKart(rastgeleYeniKart(bilgisayar.getToplamSeviye() >= SEVIYE_SINIRI));
            
            if (kullanici.getKartListesi().size() <= 3 || bilgisayar.getKartListesi().size() <= 3) {
                while (kullanici.getKartListesi().size() < 3) {
                    SavasAraci yeniKart = rastgeleYeniKart(kullanici.getToplamSeviye() >= SEVIYE_SINIRI);
                    kullanici.ekleKart(yeniKart);
                }
                while (bilgisayar.getKartListesi().size() < 3) {
                    SavasAraci yeniKart = rastgeleYeniKart(bilgisayar.getToplamSeviye() >= SEVIYE_SINIRI);
                    bilgisayar.ekleKart(yeniKart);
                }
                sonRound = true;
                logPlayerMessage("\n=== SONRAKİ ROUND SON ROUND OLACAK ===");
                logComputerMessage("\n=== SONRAKİ ROUND SON ROUND OLACAK ===");
            }

        } else {
            oyunSonuKontrol();
            return;
        }
    
        turn++;
        if (turn >= MAX_HAMLE) {
            oyunSonuKontrol();
            return;
        }
    
        selectedPlayerCards.clear();
        if (kullaniciSecilenKartSeti.size() >= kullanici.getKartListesi().size()) {
            kullaniciSecilenKartSeti.clear();
        }

        updateUI();
    }
    
    private void oyunSonuKontrol() {
        updateUI();
        String kazanan = "";
        String sebep = "";
        
        if (kullanici.getKartListesi().isEmpty()) {
            kazanan = "Bilgisayar";
            sebep = "Kullanıcının kartları tükendi!";
        } else if (bilgisayar.getKartListesi().isEmpty()) {
            kazanan = "Kullanıcı";
            sebep = "Bilgisayarın kartları tükendi!";
        } 
        else if (turn >= MAX_HAMLE || sonRound) {
            if (kullanici.SkorGoster() > bilgisayar.SkorGoster()) {
                kazanan = "Kullanıcı";
                sebep = "Daha yüksek skor: " + kullanici.SkorGoster() + " > " + bilgisayar.SkorGoster();
            } else if (bilgisayar.SkorGoster() > kullanici.SkorGoster()) {
                kazanan = "Bilgisayar";
                sebep = "Daha yüksek skor: " + bilgisayar.SkorGoster() + " > " + kullanici.SkorGoster();
            } else {
                int kullaniciDayaniklilik = kullanici.toplamDayaniklilik();
                int bilgisayarDayaniklilik = bilgisayar.toplamDayaniklilik();
                
                if (kullaniciDayaniklilik > bilgisayarDayaniklilik) {
                    kazanan = "Kullanıcı";
                    kullanici.setSkor(kullanici.SkorGoster() + 
                        (kullaniciDayaniklilik - bilgisayarDayaniklilik));
                    sebep = "Daha yüksek toplam dayanıklılık";
                } else if (bilgisayarDayaniklilik > kullaniciDayaniklilik) {
                    kazanan = "Bilgisayar";
                    bilgisayar.setSkor(bilgisayar.SkorGoster() + 
                        (bilgisayarDayaniklilik - kullaniciDayaniklilik));
                    sebep = "Daha yüksek toplam dayanıklılık";
                } else {
                    kazanan = "Berabere";
                    sebep = "Skorlar ve dayanıklılıklar eşit";
                }
            }
        }
    
        if (!kazanan.isEmpty()) {
            attackButton.setEnabled(false);
            logOyunSonu();
            logGameEnd(kazanan, sebep, kullanici.SkorGoster(), bilgisayar.SkorGoster());
            JOptionPane.showMessageDialog(this,"Oyun Bitti!\nKazanan: " + kazanan + "\nSebep: " + sebep,"Oyun Sonu", JOptionPane.INFORMATION_MESSAGE);
            sonRound =false;
        }
    }

    private void temizleVeGuncelle() {
        ArrayList<SavasAraci> kullaniciKartListesi = kullanici.getKartListesi();
        ArrayList<SavasAraci> bilgisayarKartListesi = bilgisayar.getKartListesi();
    
        kullaniciKartListesi.removeIf(kart -> {
            if (kart.getDayaniklilik() <= 0) {
                kullaniciSecilenKartSeti.remove(kart);
                return true;
            }
            return false;
        });
    
        bilgisayarKartListesi.removeIf(kart -> {
            if (kart.getDayaniklilik() <= 0) {
                computerSecilenKartSeti.remove(kart);
                return true;
            }
            return false;
        });
    }
    
    private void updatePlayerScores() {
        int playerTotalLevel = kullanici.getToplamSeviye();
        int computerTotalLevel = bilgisayar.getToplamSeviye();

        if (playerTotalLevel > kullanici.SkorGoster()) {
            kullanici.setSkor(playerTotalLevel);
        }
        if (computerTotalLevel > bilgisayar.SkorGoster()) {
            bilgisayar.setSkor(computerTotalLevel);
        }
    }

    private void saldiriHesapla(SavasAraci saldiran, SavasAraci hedef, String saldiranTaraf) {
        int orijinalDayaniklilik = hedef.getDayaniklilik();
        int saldiriDegeri = saldiran.getVurus();
        StringBuilder logMessage = new StringBuilder();
        String avantajTipi = "";
        int bonusDegeri = 0;
    
        if (hedef instanceof HavaAraci && saldiran instanceof DenizAraci) {
            bonusDegeri = ((DenizAraci) saldiran).getHavaVurusAvantaji();
            saldiriDegeri += bonusDegeri;
            avantajTipi = "Hava";
        } else if (hedef instanceof KaraAraci && saldiran instanceof HavaAraci) {
            bonusDegeri = ((HavaAraci) saldiran).getKaraVurusAvantaji();
            saldiriDegeri += bonusDegeri;
            avantajTipi = "Kara";
        } else if (hedef instanceof DenizAraci && saldiran instanceof KaraAraci) {
            bonusDegeri = ((KaraAraci) saldiran).getDenizVurusAvantaji();
            saldiriDegeri += bonusDegeri;
            avantajTipi = "Deniz";
        }
        
        hedef.durumGuncelle(saldiriDegeri, 0);
    
        logMessage.append("\n⚔️ SALDIRI DETAYI ⚔️\n")
                  .append("▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n");
    
        logMessage.append("📋 SALDIRAN BİRLİK\n")
                  .append("➤ Tür: ")
                  .append(saldiran.getAltSinif())
                  .append("\n")
                  .append("➤ Temel Güç: ")
                  .append(saldiran.getVurus())
                  .append("\n");
    
        if (!avantajTipi.isEmpty()) {
            logMessage.append("➤ ")
                      .append(avantajTipi)
                      .append(" Avantajı: +")
                      .append(bonusDegeri)
                      .append("\n");
        }
        
        logMessage.append("➤ Toplam Saldırı Gücü: ")
                  .append(saldiriDegeri)
                  .append("\n\n");
    
        logMessage.append("🎯 HEDEF BİRLİK\n")
                  .append("➤ Tür: ")
                  .append(hedef.getAltSinif())
                  .append("\n")
                  .append("➤ Başlangıç Canı: ")
                  .append(orijinalDayaniklilik)
                  .append("\n")
                  .append("➤ Alınan Hasar: ")
                  .append(saldiriDegeri)
                  .append("\n")
                  .append("➤ Kalan Dayanıklılık: ")
                  .append(hedef.getDayaniklilik())
                  .append("\n")
                  .append("\n▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n");

        logAttack(saldiran, hedef, saldiriDegeri, saldiranTaraf, avantajTipi, bonusDegeri);
    
        if (hedef.getDayaniklilik() <= 0) {
            logMessage.append("💥 Kritik Hasar! Hedef Yok Edildi!\n");
            logCardDestruction(hedef, saldiranTaraf);
        } else if (hedef.getDayaniklilik() < orijinalDayaniklilik * 0.3) {
            logMessage.append("⚠️ Hedef Kritik Seviyede!\n");
        }
    
        if (saldiranTaraf.equals("Kullanıcı")) {
            logPlayerMessage(logMessage.toString());
        } else {
            logComputerMessage(logMessage.toString());
        }
    
        if (hedef.getDayaniklilik() <= 0) {
            int hedefBaslangicSeviyePuani = roundStartLevels.getOrDefault(hedef, 0);
            int seviyeArtisi = Math.max(10, hedefBaslangicSeviyePuani);
            saldiran.durumGuncelle(0, seviyeArtisi);   
                     
            StringBuilder resultMessage = new StringBuilder();
            resultMessage.append("\n🏆 ZAFER RAPORU 🏆\n")
                         .append("▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n")
                         .append("💫 Yok Edilen: ")
                         .append(hedef.getAltSinif()).append("\n")
                         .append("📈 Kazanılan Deneyim: +")
                         .append(seviyeArtisi).append("\n")
                         .append("🎖️ Yeni Seviye: ")
                         .append(saldiran.getSeviyePuani()).append("\n")
                         .append("\n▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n");

            logLevelUp(saldiran, saldiranTaraf, seviyeArtisi);
    
            if (saldiranTaraf.equals("Kullanıcı")) {
                kullanici.setSkor(kullanici.SkorGoster() + seviyeArtisi);
                logBattleResult(resultMessage.toString(), "Kullanıcı");
            } else {
                bilgisayar.setSkor(bilgisayar.SkorGoster() + seviyeArtisi);
                logBattleResult(resultMessage.toString(), "Bilgisayar");
            }
        }
    }
    
    private void logRoundStatus(int roundNo, boolean isRoundStart) {
        StringBuilder logMessage = new StringBuilder();
        
        if (isRoundStart) {
            logMessage.append("\n🎮 ROUND ")
                      .append(roundNo+1)
                      .append(" BAŞLIYOR 🎮\n")
                      .append("▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n")
                      .append("📊 MEVCUT DURUM\n")
                      .append("➤ Kullanıcı Kartları: ")
                      .append(kullanici.getKartListesi().size())
                      .append("\n")
                      .append("➤ Bilgisayar Kartları: ")
                      .append(bilgisayar.getKartListesi().size())
                      .append("\n")
                      .append("➤ Kullanıcı Skor: ")
                      .append(kullanici.SkorGoster())
                      .append("\n")
                      .append("➤ Bilgisayar Skor: ")
                      .append(bilgisayar.SkorGoster())
                      .append("\n");
    
            if (sonRound) {
                logMessage.append("\n⚠️ BU SON ROUND! ⚠️\n");
            }
        } else {
            logMessage.append("\n🏁 ROUND SONA ERDİ 🏁\n")
                      .append("▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n")
                      .append("📊 ROUND SONUÇLARI\n")
                      .append("➤ Kullanıcı Skor: ")
                      .append(kullanici.SkorGoster())
                      .append("\n")
                      .append("➤ Bilgisayar Skor: ")
                      .append(bilgisayar.SkorGoster())
                      .append("\n\n");
    
            if (kullanici.SkorGoster() > bilgisayar.SkorGoster()) {
                logMessage.append("👑 Kullanıcı Önde!\n");
            } else if (bilgisayar.SkorGoster() > kullanici.SkorGoster()) {
                logMessage.append("⚠️ Bilgisayar Önde!\n");
            } else {
                logMessage.append("🤝 Skorlar Eşit!\n");
            }
            logRoundEnd(kullanici.SkorGoster(), bilgisayar.SkorGoster(), kullanici.getKartListesi(), bilgisayar.getKartListesi());
        }
        
        logMessage.append("\n▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n");
    
        logPlayerMessage(logMessage.toString());
        logComputerMessage(logMessage.toString());
    }
    
    private void logOyunSonu() {
        StringBuilder logMessage = new StringBuilder();
        
        logMessage.append("\n🎭 OYUN SONA ERDİ 🎭\n")
                  .append("▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n")
                  .append("📊 FINAL SKORLARI\n")
                  .append("➤ Kullanıcı: ")
                  .append(kullanici.SkorGoster())
                  .append("\n")
                  .append("➤ Bilgisayar: ")
                  .append(bilgisayar.SkorGoster())
                  .append("\n\n");
    
        if (kullanici.SkorGoster() > bilgisayar.SkorGoster()) {
            logMessage.append("🏆 TEBRİKLER! OYUNU KAZANDINIZ! 🎉\n");
        } else if (bilgisayar.SkorGoster() > kullanici.SkorGoster()) {
            logMessage.append("😔 MAALESEF KAYBETTİNİZ! 💫\n");
        } else {
            logMessage.append("🤝 OYUN BERABERE BİTTİ! ✨\n");
        }
        
        logMessage.append("\n▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n");
    
        logPlayerMessage(logMessage.toString());
        logComputerMessage(logMessage.toString());
    }
    
    private void resetGame() {
        game_number++;
        turn = 0;
        attackButton.setEnabled(true);
        kullanici.kartlariTemizle();
        bilgisayar.kartlariTemizle();
        baslangicKartlariEkle(kullanici);
        baslangicKartlariEkle(bilgisayar);
        kullanici.setSkor(0);
        bilgisayar.setSkor(0);
        playerLogArea.setText("🎮 Yeni oyun başladı!\n");
        computerLogArea.setText("🎮 Yeni oyun başladı!\n");
        selectedPlayerCards.clear();
        kullaniciSecilenKartSeti.clear();
        displayBattleCards(new ArrayList<>(), new ArrayList<>());
        updateUI();
    }

    private void baslangicKartlariEkle(Oyuncu oyuncu) {
        for (int i = 0; i < 6; i++) {
            SavasAraci yeniKart = rastgeleBaslangicKarti();
            oyuncu.ekleKart(yeniKart);
        }
    }

    private static SavasAraci rastgeleBaslangicKarti() {
        Random rnd = new Random();
        int rastgele = rnd.nextInt(3);
        switch (rastgele) {
            case 0: return new Ucak();
            case 1: return new Obus();
            case 2: return new Firkateyn();
            default: return new Ucak();
        }
    }
    
    private static SavasAraci rastgeleYeniKart(boolean seviyeAsildi) {
        Random rnd = new Random();
        int rastgele = seviyeAsildi ?  rnd.nextInt(6) :  rnd.nextInt(3);
        
        switch (rastgele) {
            case 0: return new Ucak();
            case 1: return new Obus();
            case 2: return new Firkateyn();
            case 3: return new Siha();
            case 4: return new Sida();
            case 5: return new KFS();
            default: return new Ucak();
        }
    }
    
    public static void main(String[] args) {
        String kullanici_ismi = JOptionPane.showInputDialog(null,"Kullaıcı ismi giriniz : ","İSİM GİRİŞİ",JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Oyun(kullanici_ismi).setVisible(true);
            }
        });
        }
}