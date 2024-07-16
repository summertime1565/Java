package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// 카드 클래스
class Card {
    private int value;
    private String suit;

    public Card(String suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        String faceValue;
        switch (value) {
            case 1:
                faceValue = "Ace";
                break;
            case 11:
                faceValue = "Jack";
                break;
            case 12:
                faceValue = "Queen";
                break;
            case 13:
                faceValue = "King";
                break;
            default:
                faceValue = String.valueOf(value);
        }
        return faceValue + " of " + suit;
    }
}

//덱 클래스
class Deck {
 private List<Card> cards;

 public Deck() {
     resetDeck(); // 덱 초기화
 }

 private void initializeDeck() {
     String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
     for (String suit : suits) {
         for (int value = 1; value <= 13; value++) {
             cards.add(new Card(suit, value));
         }
     }
 }

 public void shuffle() {
     Random rand = new Random();
     for (int i = 0; i < cards.size(); i++) {
         int randomIndex = rand.nextInt(cards.size());
         Card temp = cards.get(i);
         cards.set(i, cards.get(randomIndex));
         cards.set(randomIndex, temp);
     }
 }

 public Card drawCard() {
     if (cards.isEmpty()) {
         resetDeck(); // 덱이 비어있을 경우 자동으로 재설정
     }
     return cards.remove(cards.size() - 1);
 }

 public void resetDeck() {
     cards = new ArrayList<>(); // 기존 카드 제거
     initializeDeck(); // 새로운 덱 초기화
     shuffle(); // 셔플
 }
}


// 플레이어 클래스
class Player {
    private String name;
    private List<Card> cards;
    private int total;
    private int balance;
    private List<Integer> bettingHistory;

    public Player(String name, int balance) {
        this.name = name;
        this.balance = balance;
        this.cards = new ArrayList<>();
        this.total = 0;
        this.bettingHistory = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
        total += card.getValue();
    }

    public int getTotal() {
        return total;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void reset() {
        cards.clear();
        total = 0;
    }

    public List<Integer> getBettingHistory() {
        return bettingHistory;
    }

    public void addBettingHistory(int result) {
        bettingHistory.add(result);
    }
}

// 딜러 클래스
class Dealer {
    private List<Card> cards;
    private int total;

    public Dealer() {
        this.cards = new ArrayList<>();
        this.total = 0;
    }

    public void addCard(Card card) {
        cards.add(card);
        total += card.getValue();
    }

    public int getTotal() {
        return total;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void reset() {
        cards.clear();
        total = 0;
    }
}

// 바카라 게임 클래스
public class BaccaratGame {
    private static final int BET_AMOUNT = 100;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 기초 자본 및 판돈 입력 받기
        System.out.println("Enter your initial balance: ");
        int initialBalance = scanner.nextInt();
        System.out.println("Enter your bet amount: ");
        int betAmount = scanner.nextInt();
        scanner.nextLine(); // consume newline

        // 플레이어 생성
        Player player = new Player("Player", initialBalance);
        Dealer dealer = new Dealer();
        Deck deck = new Deck();

        while (true) {
            // 베팅
            System.out.println("Current balance: " + player.getBalance());
            System.out.println("Place your bet (1: Player, 2: Banker, 3: Tie, 4: Player Pair, 5: Banker Pair): ");
            int betOption = scanner.nextInt();
            scanner.nextLine(); // consume newline
            if (betOption < 1 || betOption > 5) {
                System.out.println("Invalid bet option.");
                continue;
            }

            // 게임 시작
            deck.shuffle();
            player.reset();
            dealer.reset();

            // 처음 두 장의 카드를 나눠줌
            player.addCard(deck.drawCard());
            dealer.addCard(deck.drawCard());
            player.addCard(deck.drawCard());
            dealer.addCard(deck.drawCard());

            // 결과 출력
            System.out.println("Player's cards: " + player.getCards());
            System.out.println("Dealer's cards: " + dealer.getCards());

            // 딜러의 룰 추가: 딜러의 카드 합이 5 이하일 경우에만 카드를 한 장 더 받음
            while (dealer.getTotal() <= 5) {
                dealer.addCard(deck.drawCard());
            }

            // 결과 확인
            int payout = 0;
            switch (betOption) {
                case 1:
                    // Player bet
                    payout = player.getTotal() > dealer.getTotal() ? betAmount * 2 : 0;
                    break;
                case 2:
                    // Banker bet
                    payout = player.getTotal() < dealer.getTotal() ? betAmount * 2 : 0;
                    break;
                case 3:
                    // Tie bet
                    payout = player.getTotal() == dealer.getTotal() ? betAmount * 9 : 0;
                    break;
                case 4:
                    // Player Pair bet
                    if (player.getCards().get(0).getValue() == player.getCards().get(1).getValue()) {
                        payout = betAmount * 11;
                    }
                    break;
                case 5:
                    // Banker Pair bet
                    if (dealer.getCards().get(0).getValue() == dealer.getCards().get(1).getValue()) {
                        payout = betAmount * 11;
                    }
                    break;
            }

            // 베팅 결과에 따른 지급
            player.setBalance(player.getBalance() + payout - betAmount);

            // 플레이어의 베팅 이력 저장
            player.addBettingHistory(payout);

            // 결과 출력
            System.out.println("Player's balance: " + player.getBalance());

            // 게임 종료 여부 확인
            System.out.println("Do you want to play again? (yes/no): ");
            String playAgain = scanner.nextLine();
            if (!playAgain.equalsIgnoreCase("yes")) {
                break;
            }
        }

        // 베팅 이력 출력
        System.out.println("Player's betting history: " + player.getBettingHistory());

        scanner.close();
    }
}

