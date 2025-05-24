import java.awt.*;
import java.util.*;
import java.util.List;
import java.nio.file.*;
import java.io.IOException;
import javax.swing.*;

public class Dictionnaire {
 // Liste des mots français chargés à partir du dictionnaire
 private static List<String> frenchWords;

 // Case sélectionnée par l'utilisateur pour placer une lettre
 private static JTextField selectedCase = null;

 // Ensemble des mots déjà utilisés dans le jeu pour éviter les répétitions
 private static HashSet<String> motsUtilises = new HashSet<>();

 // Liste des boutons désactivés après utilisation
 private static List<Button> boutonsDesactives = new ArrayList<>();

 // Gestionnaire de mise en page pour naviguer entre les panneaux
 private static CardLayout cardLayout;

 // Panneau contenant les différents écrans de jeu
 private static JPanel cardPanel;

 // Champ de texte pour afficher le score total
 private static JTextField scoreTextField;

 // Variable pour suivre le score total
 private static double scoreTotal = 0;

 public static void main(String[] args) {
     // Charge les mots du dictionnaire et affiche la fenêtre d'introduction
     chargerDictionnaire();
     afficherFenetreIntroduction();
 }

     // Affiche une fenêtre d'accueil avant de commencer le jeu
    private static void afficherFenetreIntroduction() {
        JFrame introFrame = new JFrame("Bienvenue");
        introFrame.setSize(400, 300);
        introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        introFrame.setLayout(new BorderLayout());

         // Ajout d'un label stylisé pour le message de bienvenue
        JLabel label = new JLabel("Bienvenue à notre jeu des mots", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setOpaque(true);
        label.setBackground(new Color(173, 216, 230)); // Couleur de fond bleu clair (Light Blue)
        label.setForeground(new Color(25, 25, 112)); // Texte en bleu très foncé (Midnight Blue)
        introFrame.add(label, BorderLayout.CENTER);

         // Bouton pour commencer le jeu
        JButton commencerButton = new JButton("Commencer");
        commencerButton.setFont(new Font("Arial", Font.BOLD, 18));
        commencerButton.setFocusPainted(false);
        commencerButton.setBackground(new Color(25, 25, 112)); // Bleu clair (Light Blue)
        commencerButton.setForeground(Color.WHITE); // Texte en blanc
        commencerButton.setBorder(BorderFactory.createLineBorder(new Color(25, 25, 112), 2)); // Bordure bleu foncé (Steel Blue)
        
        commencerButton.addActionListener(e -> {
            introFrame.dispose(); // Ferme la fenêtre d'introduction
            lancerJeu(); // Lance le jeu principal
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(commencerButton);
        introFrame.add(buttonPanel, BorderLayout.SOUTH);

        introFrame.setLocationRelativeTo(null); // Centre la fenêtre
        introFrame.setVisible(true);
    }
    // Variable pour suivre le nombre de clics sur le bouton "Valider"
    private static int validerButtonClickCount = 0;

    // Lance le jeu principal en initialisant les panneaux et la logique du jeu
    private static void lancerJeu() {
        JFrame frame = new JFrame("Jeu des Mots");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        Random random = new Random();
        HashMap<Character, Integer> dictionnaire = genererDictionnaire();
        ArrayList<Character> lettresAffichees = new ArrayList<>();

        // Création de 10 panneaux de jeu
        for (int i = 0; i < 10; i++) {
            JPanel panelDeJeu = creerPanelDeJeu(dictionnaire, lettresAffichees, random, frame);
            cardPanel.add(panelDeJeu, "Panel " + i);
        }

        frame.add(cardPanel, BorderLayout.CENTER);

        // Création des panneaux pour le score et la navigation
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        // Panneau pour afficher le score
        JPanel scorePanel = new JPanel(new GridLayout(1, 1));
        scoreTextField = new JTextField("Score Total: 0", 20);
        scoreTextField.setFont(new Font("Arial", Font.BOLD, 24));
        scoreTextField.setHorizontalAlignment(SwingConstants.CENTER);
        scoreTextField.setEditable(false);
        scoreTextField.setBackground(new Color(245, 245, 245));
        scoreTextField.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));

        scorePanel.add(scoreTextField);
        bottomPanel.add(scorePanel, BorderLayout.NORTH);

        // Panneau pour les boutons de navigation (Précédent, Suivant)
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton flecheGauche = new JButton(" Précédent");
        flecheGauche.setFont(new Font("Arial", Font.BOLD, 18));
        flecheGauche.setBackground(new Color(173, 216, 230));
        flecheGauche.setFocusPainted(false);
        flecheGauche.setPreferredSize(new Dimension(150, 50));
        flecheGauche.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton flecheDroite = new JButton("Suivant ");
        flecheDroite.setFont(new Font("Arial", Font.BOLD, 18));
        flecheDroite.setBackground(new Color(173, 216, 230));
        flecheDroite.setFocusPainted(false);
        flecheDroite.setPreferredSize(new Dimension(150, 50));
        flecheDroite.setBorder(BorderFactory.createRaisedBevelBorder());

        navigationPanel.add(flecheGauche);
        navigationPanel.add(flecheDroite);

        flecheGauche.addActionListener(e -> cardLayout.previous(cardPanel));
        flecheDroite.addActionListener(e -> {
            cardLayout.next(cardPanel);
            verifierFinDuJeu(frame); // Vérifie la fin du jeu après chaque transition
        });
        
        bottomPanel.add(navigationPanel, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null); // Centre la fenêtre
        frame.setVisible(true);
    }

    // Chargement du dictionnaire à partir d'un fichier CSV
    private static void chargerDictionnaire() {
        frenchWords = new ArrayList<>();
        try {
            List<String> lignes = Files.readAllLines(Paths.get("C:\\Users\\yomna\\OneDrive\\Bureau\\projet java\\projetjava\\dico.csv"));
            for (String ligne : lignes) {
                String[] colonnes = ligne.split(",");
                if (colonnes.length > 0) {
                    frenchWords.add(colonnes[0].toLowerCase());
                }
            }
            System.out.println("Dictionnaire charge avec succees. Nombre de mots : " + frenchWords.size());
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du dictionnaire français : " + e.getMessage());
        }
    }

    private static HashMap<Character, Integer> genererDictionnaire() {
        HashMap<Character, Integer> dictionnaire = new HashMap<>();
        for (char lettre = 'a'; lettre <= 'z'; lettre++) {
            dictionnaire.put(lettre, 2);
        }
        dictionnaire.put('a', 4);
        dictionnaire.put('e', 3);
        dictionnaire.put('o', 3);
        dictionnaire.put('u', 3);
        dictionnaire.put('i', 3);
        dictionnaire.put('w', 1);
        dictionnaire.put('z', 1);
        return dictionnaire;
    }

    //Crée le panneau de jeu principal contenant les boutons et les cases de texte.
    private static JPanel creerPanelDeJeu(HashMap<Character, Integer> dictionnaire, ArrayList<Character> lettresAffichees, Random random, JFrame frame) {
        JPanel panel = new JPanel(new GridLayout(4, 1));

        JPanel panel1 = new JPanel(new FlowLayout());
        List<Button> boutons = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Object[] lettres = dictionnaire.keySet().toArray();
            Character lettreAleatoire = (Character) lettres[random.nextInt(lettres.length)];
            lettresAffichees.add(lettreAleatoire);

            Button button = new Button(lettreAleatoire.toString());
            button.setPreferredSize(new Dimension(50, 50));
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setBackground(new Color(173, 216, 230));
           
            boutons.add(button);
            panel1.add(button);

            button.addActionListener(e -> {
                if (selectedCase != null && selectedCase.getText().isEmpty()) {
                    selectedCase.setText(button.getLabel());
                    button.setEnabled(false);
                    boutonsDesactives.add(button);
                }
            });
        }
        panel.add(panel1);

        List<JTextField> toutesLesCases = new ArrayList<>();
        int[] tailles = genererTaillesAleatoiresDistinctes(random, 3, 2, 6);

        for (int taille : tailles) {
            JPanel lignePanel = creerPanelAvecCases(taille, lettresAffichees, frame, boutons, toutesLesCases);
            panel.add(lignePanel);
        }

        return panel;
    }

    //Crée un panneau contenant un nombre donné de cases de texte et un bouton "Valider"
    private static JPanel creerPanelAvecCases(int nbCases, ArrayList<Character> lettresAffichees, JFrame frame, List<Button> boutons, List<JTextField> toutesLesCases) {
        JPanel panel = new JPanel(new FlowLayout());
        JTextField[] casesTexte = new JTextField[nbCases];

        for (int i = 0; i < nbCases; i++) {
            casesTexte[i] = new JTextField(5);
            casesTexte[i].setFont(new Font("Arial", Font.BOLD, 18));
            casesTexte[i].setHorizontalAlignment(SwingConstants.CENTER);
            casesTexte[i].setEditable(false);
            casesTexte[i].setBackground(new Color(255, 255, 255));
            casesTexte[i].setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 1));
            toutesLesCases.add(casesTexte[i]);
            panel.add(casesTexte[i]);

            final int index = i;
            casesTexte[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    selectedCase = casesTexte[index];
                }
            });
        }

        JButton validerButton = new JButton("Valider ");
        validerButton.setFont(new Font("Arial", Font.BOLD, 16));
        validerButton.setBackground(new Color(173, 216, 230));
        validerButton.setFocusPainted(false);
        validerButton.setPreferredSize(new Dimension(100, 40));
        validerButton.setBorder(BorderFactory.createRaisedBevelBorder());

        panel.add(validerButton);

        validerButton.addActionListener(e -> validerMot(nbCases, lettresAffichees, casesTexte, toutesLesCases, frame, validerButton, boutons));

        return panel;
    }

    //Valide le mot formé dans les cases de texte et met à jour le jeu en conséquence
    private static void validerMot(int nbCases, ArrayList<Character> lettresAffichees, JTextField[] casesTexte, List<JTextField> toutesLesCases, JFrame frame, JButton validerButton, List<Button> boutons) {
        // Vérifier si toutes les cases sont remplies
        for (JTextField caseTexte : casesTexte) {
            if (caseTexte.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Veuillez remplir toutes les cases.");
                validerButton.setBackground(Color.RED);
                return;
            }
        }
    
        // Former le mot à partir des cases
        StringBuilder mot = new StringBuilder();
        for (JTextField caseTexte : casesTexte) {
            mot.append(caseTexte.getText());
        }
        String motForme = mot.toString().toLowerCase();
    
        // Vérifier si le mot est formable et valide
        if (peutConstruireMot(motForme, lettresAffichees)) {
            if (frenchWords.contains(motForme)) {
                JOptionPane.showMessageDialog(frame, "Mot valide : " + motForme);
                validerButton.setBackground(Color.GREEN);
                motsUtilises.add(motForme);
    
                // Calcul du score
                int nbCasesTotal = toutesLesCases.size();
                int nbCasesValides = 0;
                for (JTextField caseTexte : toutesLesCases) {
                    if (!caseTexte.getText().isEmpty()) {
                        nbCasesValides++;
                    }
                }
    
                double scorePanel = calculerScore(nbCasesTotal, nbCasesValides);
                scoreTotal += scorePanel;
                scoreTextField.setText("Score Total: " + scoreTotal);
    
            } else {
                JOptionPane.showMessageDialog(frame, "Mot non valide en français.");
                validerButton.setBackground(Color.RED);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Mot non formable avec les lettres disponibles.");
            validerButton.setBackground(Color.RED);
        }
    
        // Réinitialiser les boutons après vérification
        reinitialiserBoutons(boutons);
    
        // Increment the click counter
        validerButtonClickCount++;  // Increment the number of "Valider" clicks
    }
    
    //Calcule le score basé sur le nombre de cases remplies et le nombre total de cases
    private static double calculerScore(int nbCasesTotal, int nbCasesValides) {
        return (2.0 * nbCasesValides) / nbCasesTotal;
    }

    //Vérifie si un mot donné peut être construit à partir des lettres disponibles.
    private static boolean peutConstruireMot(String mot, ArrayList<Character> lettresDisponibles) {
        ArrayList<Character> copieLettres = new ArrayList<>(lettresDisponibles);
        for (char lettre : mot.toCharArray()) {
            if (!copieLettres.remove((Character) lettre)) {
                return false;
            }
        }
        return true;
    }

    //Génère un tableau d'entiers représentant des tailles aléatoires distinctes pour les lignes de cases
    private static int[] genererTaillesAleatoiresDistinctes(Random random, int nombre, int min, int max) {
        int[] tailles = new int[nombre];
        ArrayList<Integer> valeurs = new ArrayList<>();

        while (valeurs.size() < nombre) {
            int taille = random.nextInt(max - min + 1) + min;
            if (!valeurs.contains(taille)) {
                valeurs.add(taille);
            }
        }

        for (int i = 0; i < nombre; i++) {
            tailles[i] = valeurs.get(i);
        }

        return tailles;
    }

    // Réinitialise l'état des boutons en les rendant à nouveau cliquables
    private static void reinitialiserBoutons(List<Button> boutons) {
        for (Button bouton : boutons) {
            bouton.setEnabled(true);
        }
    }

     // Vérifie si le jeu est terminé après 30 clics sur le bouton "Valider"
    private static void verifierFinDuJeu(JFrame frame) {
        if (validerButtonClickCount >= 30) { // Check if the "Valider" button has been clicked 30 times
            String message;
            if (scoreTotal >= 15) {
                message = "Excellent travail ! Vous êtes un champion des mots avec un score de " + scoreTotal + " !";
            } else if (scoreTotal >= 10) {
                message = "Bien joué ! Vous avez obtenu un score respectable de " + scoreTotal + ".";
            } else {
                message = "Jeu terminé. Votre score est " + scoreTotal + ". Essayez encore pour vous améliorer !";
            }
    
            JOptionPane.showMessageDialog(frame, message, "Résultat Final", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    
}
