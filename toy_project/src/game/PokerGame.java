package game;
import java.util.*;

public class PokerGame {
    private static final int DECK_SIZE = 52;
    private static final int HAND_SIZE = 5;

    private static final String[] RANKS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"
    };
    private static final String[] SUITS = {
            "♠", "♣", "♥", "♦"
    };

    private List<String> deck;
    private int playerBalance;
    private List<Integer> betHistory;

    public PokerGame(int initialBalance) {
        initializeDeck();
        this.playerBalance = initialBalance;
        this.betHistory = new ArrayList<>();
    }

    private void initializeDeck() {
        deck = new ArrayList<>();
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(rank + suit);
            }
        }
        Collections.shuffle(deck);
    }

    public void playRound() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nYour balance: $" + playerBalance);
        System.out.println("Place your bet (0 to quit): ");
        int betAmount = scanner.nextInt();
        scanner.nextLine();

        if (betAmount == 0) {
            gameOver();
            return;
        }

        if (betAmount > playerBalance || betAmount < 0) {
            System.out.println("Invalid bet amount!");
            return;
        }

        betHistory.add(betAmount);

        List<String> playerHand = drawHand();
        System.out.println("Your hand: " + playerHand);

        List<String> dealerHand = drawHand();
        System.out.println("Dealer's hand: " + dealerHand);

        String playerRank = getHandRank(playerHand);
        String dealerRank = getHandRank(dealerHand);

        System.out.println("Your hand rank: " + playerRank);
        System.out.println("Dealer's hand rank: " + dealerRank);

        int result = compareHands(playerHand, dealerHand);
        if (result > 0) {
            System.out.println("You win!");
            playerBalance += betAmount;
        } else if (result < 0) {
            System.out.println("Dealer wins!");
            playerBalance -= betAmount;
        } else {
            System.out.println("It's a tie! No one wins.");
        }
        
        // 게임 종료 여부 확인
        if (playerBalance <= 0) {
            System.out.println("You're out of money! Game over.");
        } else {
            boolean validChoice = false;
            while (!validChoice) {
                System.out.println("Do you want to play another round? (y/n): ");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("y")) {
                    validChoice = true;
                } else if (choice.equalsIgnoreCase("n")) {
                    gameOver();
                    validChoice = true;
                } else {
                    System.out.println("Invalid choice. Please enter 'y' or 'n'.");
                }
            }
        }
    }

    private List<String> drawHand() {
        List<String> hand = new ArrayList<>();
        for (int i = 0; i < HAND_SIZE; i++) {
            hand.add(deck.remove(0));
            if (deck.size() == 0) {
                initializeDeck();
            }
        }
        return hand;
    }

    private String getHandRank(List<String> hand) {
        // Sort the hand by rank
        Collections.sort(hand);

        // Check for special hands
        if (isRoyalFlush(hand)) {
            return "Royal Flush";
        } else if (isStraightFlush(hand)) {
            return "Straight Flush";
        } else if (isFourOfAKind(hand)) {
            return "Four of a Kind";
        } else if (isFullHouse(hand)) {
            return "Full House";
        } else if (isFlush(hand)) {
            return "Flush";
        } else if (isStraight(hand)) {
            return "Straight";
        } else if (isThreeOfAKind(hand)) {
            return "Three of a Kind";
        } else if (isTwoPair(hand)) {
            return "Two Pair";
        } else if (isOnePair(hand)) {
            return "One Pair";
        } else {
            return "High Card";
        }
    }

    private int compareHands(List<String> hand1, List<String> hand2) {
        // Compare the ranks of the hands
        String rank1 = getHandRank(hand1);
        String rank2 = getHandRank(hand2);
        return rank1.compareTo(rank2);
    }

    private boolean isRoyalFlush(List<String> hand) {
        // Check if the hand is a royal flush (10, J, Q, K, A of the same suit)
        return isStraightFlush(hand) && hand.contains("10♠") && hand.contains("J♠") &&
               hand.contains("Q♠") && hand.contains("K♠") && hand.contains("A♠");
    }

    private boolean isStraightFlush(List<String> hand) {
        // Check if the hand is a straight flush (five consecutive cards of the same suit)
        return isFlush(hand) && isStraight(hand);
    }

    private boolean isFourOfAKind(List<String> hand) {
        // Check if the hand is four of a kind (four cards of the same rank)
        for (int i = 0; i < HAND_SIZE - 3; i++) {
            if (hand.get(i).substring(0, 1).equals(hand.get(i + 3).substring(0, 1))) {
                return true;
            }
        }
        return false;
    }

    private boolean isFullHouse(List<String> hand) {
        // Check if the hand is a full house (three of a kind and a pair)
        return isThreeOfAKind(hand) && isOnePair(hand);
    }

    private boolean isFlush(List<String> hand) {
        // Check if the hand is a flush (five cards of the same suit)
        String firstSuit = hand.get(0).substring(1);
        for (String card : hand) {
            if (!card.substring(1).equals(firstSuit)) {
                return false;
            }
        }
        return true;
    }

    private boolean isStraight(List<String> hand) {
        // Check if the hand is a straight (five consecutive cards of different suits)
        for (int i = 0; i < HAND_SIZE - 1; i++) {
            int rank1 = Arrays.asList(RANKS).indexOf(hand.get(i).substring(0, 1));
            int rank2 = Arrays.asList(RANKS).indexOf(hand.get(i + 1).substring(0, 1));
            if (rank1 + 1 != rank2) {
                return false;
            }
        }
        return true;
    }

    private boolean isThreeOfAKind(List<String> hand) {
        // Check if the hand is three of a kind (three cards of the same rank)
        for (int i = 0; i < HAND_SIZE - 2; i++) {
            if (hand.get(i).substring(0, 1).equals(hand.get(i + 2).substring(0, 1))) {
                return true;
            }
        }
        return false;
    }

    private boolean isTwoPair(List<String> hand) {
        // Check if the hand is two pair (two pairs of cards of the same rank)
        int pairCount = 0;
        for (int i = 0; i < HAND_SIZE - 1; i++) {
            if (hand.get(i).substring(0, 1).equals(hand.get(i + 1).substring(0, 1))) {
                pairCount++;
                i++;
            }
        }
        return pairCount == 2;
    }

    private boolean isOnePair(List<String> hand) {
        // Check if the hand is one pair (a pair of cards of the same rank)
        for (int i = 0; i < HAND_SIZE - 1; i++) {
            if (hand.get(i).substring(0, 1).equals(hand.get(i + 1).substring(0, 1))) {
                return true;
            }
        }
        return false;
    }

    public void gameOver() {
        System.out.println("Game over.");
        System.out.println("Your final balance: $" + playerBalance);
        System.out.print("Bet history: ");
        for (int bet : betHistory) {
            System.out.print(bet + ", ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your initial balance: ");
        int initialBalance = scanner.nextInt();
        scanner.nextLine();

        PokerGame game = new PokerGame(initialBalance);

        while (true) {
            game.playRound();
            if (game.playerBalance <= 0) {
                break;
            }
        }
        scanner.close();
    }
}
