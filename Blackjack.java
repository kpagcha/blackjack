import java.util.Stack;
import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Iterator;

/*
Idea de modificación:

No hardcodear los valores y palos, sino dejar que le usuario los defina como quiera de modo
que pueda crear cartas del valor y palo que quiera (por ejemplo 15 de Mariposas).

Modificar la clase Deck de forma el usuario pueda pasar directamente:
a) el conjunto de todas las cartas, previamente definidas
b) un conjunto de valores posibles y palos posibles y crear el mazo con esta información

Ejemplo:

Rank[] ranks = { new Rank("As", 1), new Rank("Dos", 2), ..., new Rank("Rey", 12) };
Suit[] suits = { new Suit("Oros"), new Suit("Sotas"), new Suit("Espadas"), new Suit("Bastos") };

Deck spanish_deck = new Deck(suits, ranks);

*/

public class Blackjack {

	public static String capitalizeFirstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static class Rank {

		private int value;
		private static final String[] string_values = {
			"Ace",
			"Two",
			"Three",
			"Four",
			"Five",
			"Six",
			"Seven",
			"Eight",
			"Nine",
			"Ten",
			"Jack",
			"Queen",
			"King"
		};

		public Rank(int value) throws IllegalArgumentException {
			if (value < 1 || value > 13) {
				throw new IllegalArgumentException("Invalid card value (must be between 1 and 13).");
			}
			this.value = value;
		}

		public Rank(String value) throws IllegalArgumentException {
			int i = 0;
			String r = capitalizeFirstLetter(value);

			while (i < string_values.length && !r.equals(string_values[i])) {
				i++;
			}

			if (i == string_values.length) {
				throw new IllegalArgumentException("Invalid card rank.");
			} else {
				this.value = i + 1;
			}
		}

		public int getValue() {
			return value;
		}

		public boolean isAce() {
			return value == 1;
		}

		public String toString() {
			return string_values[value - 1];
		}

		public String simpleString() {
			String str = new String();
			
			if (value == 1) {
				str += "A";
			} else if (value > 10) {
				if (value == 11) {
					str += "J";
				} else if (value == 12) {
					str += "Q";
				} else {
					str += "K";
				}
			} else {
				str += value;
			}
			return str;
		}
	}

	public static class Suit {

		private String name;
		private static final String[] string_names = {
			"Hearts",
			"Diamonds",
			"Clubs",
			"Spades"
		};

		public Suit(String name) throws IllegalArgumentException {
			int i = 0;
			String n = capitalizeFirstLetter(name);

			while (i < string_names.length && !n.equals(string_names[i])) {
				i++;
			}

			if (i == string_names.length) {
				throw new IllegalArgumentException("Invalid suit name.");
			} else {
				this.name = n;
			}
		}

		public String toString() {
			return name;
		}

		public String symbolString() {
			switch(name) {
				case "Hearts": return "\u2665";
				case "Diamonds": return "\u2666";
				case "Clubs": return "\u2663";
				case "Spades": return "\u2660";
				default: return null;
			}
		}
	}

	public static class Card {

		private Rank rank;
		private Suit suit;

		public Card(Rank rank, Suit suit) {
			this.rank = rank;
			this.suit = suit;
		}

		public Card(int rank, String suit) {
			this.rank = new Rank(rank);
			this.suit = new Suit(suit);
		}

		public Card(String rank, String suit) {
			this.rank = new Rank(rank);
			this.suit = new Suit(suit);
		}

		public Rank getRank() {
			return rank;
		}

		public String toString() {
			String str = rank.simpleString() + suit.symbolString() + " " + rank + " of " + suit;
			return str;
		}
	}

	public static class Deck {

		private Stack<Card> deck;
		private static final int number = 52;

		public Deck() {
			deck = new Stack<Card>();
			Suit hearts, diamonds, clubs, spades;

			hearts = new Suit("hearts");
			diamonds = new Suit("diamonds");
			clubs = new Suit("clubs");
			spades = new Suit("spades");

			Suit[] suits = { hearts, diamonds, clubs, spades };

			for (int i = 0; i < suits.length; i++) {
				for (int j = 1; j <= 13; j++) {
					deck.push(new Card(new Rank(j), suits[i]));
				}
			}
		}

		public void shuffle() {
			long seed = System.nanoTime();
			Collections.shuffle(deck, new Random(seed));
		}

		public void add(Card card) {
			deck.push(card);
		}

		public void add(List<Card> cards) {
			for (int i = 0; i < cards.size(); i++) {
				deck.push(cards.get(i));
			}
		}

		public Card draw() {
			return deck.pop();
		}

		public boolean isEmpty() {
			return deck.isEmpty();
		} 

		public String toString() {
			String str = new String();

			for (int i = 0; i < deck.size(); i++) {
				str += deck.get(i) + "\n";
			}
			return str;
		}
	}

	public static class Hand {

		private List<Card> cards;

		public Hand() {
			cards = new ArrayList<Card>();
		}

		public void add(Card card) {
			cards.add(card);
		}

		public List<Card> cards() {
			return cards;
		}

		public int size() {
			return cards.size();
		}

		public void clear() {
			cards.clear();
		}

		public String toString() {
			String str = new String();

			for (int i = 0; i < cards.size(); i++) {
				str += "\t" + cards.get(i) + "\n";
			}
			return str;
		}

	}

	public static class Player {

		private String name;
		private int money;
		private int bet;
		private Hand hand;
		private State state;

		public static enum State { PLAYING, TWENTYONE, BLACKJACK, STAND, BUSTED, RETIRED, RUINED };
		
		public Player(String name, int money) {
			this.name = name;
			this.money = money;
			bet = 0;
			hand = new Hand();
			state = State.PLAYING;
		}

		public int getMoney() {
			return money;
		}

		public void setMoney(int money) {
			this.money = money;
		}

		public int getBet() {
			return bet;
		}

		public void setBet(int bet) {
			this.bet = bet;
		}

		public Hand hand() {
			return hand;
		}

		public Play choosePlay() {
			char choice;
			do {
				p(name + ", choose your play; (H)IT, (S)TAND, (D)OUBLE, S(P)LIT OR S(U)RRENDER:");
				pnln("> ");
				choice = Character.toLowerCase(in.next().charAt(0));
			} while (!(choice == 'h' || choice == 's' || choice == 'd' || choice == 'p' || choice == 'u' ));

			switch(choice) {
				case 'h': return Play.HIT;
				case 's': return Play.STAND;
				case 'd': return Play.DOUBLE;
				case 'p': return Play.SPLIT;
				case 'u': return Play.SURRENDER;
				default: return null;
			}
		}

		// public void double() {

		// }

		// public void split() {

		// }

		// public void surrender() {
		// 	money =/ 2; 
		// }

		public boolean bet(int quantity) {
			int remainder = money - quantity;

			if (remainder >= 0) {
				money = remainder;
				return true;
			} else {
				return false;
			}
		}

		public void startsTurn() {
			state = State.PLAYING;
		}

		public void stand() {
			state = State.STAND;
		}

		public void busted() {
			state = State.BUSTED;
		}

		public void blackjack() {
			state = State.BLACKJACK;
		}

		public void twentyoneScore() {
			state = State.TWENTYONE;
		}

		public State getState() {
			return state;
		}

		public boolean hasBlackjack() {
			return state == State.BLACKJACK;
		}

		public boolean hasTwentyone() {
			return state == State.TWENTYONE;
		}

		public boolean isPlaying() {
			return state == State.PLAYING;
		}

		public boolean isBusted() {
			return state == State.BUSTED;
		}

		public String toString() {
			return name;
		}
	}

	public static class Dealer {

		private String name;
		private Hand hand;
		private State state;

		public static enum State { PLAYING, TWENTYONE, BLACKJACK, BUSTED };

		public Dealer() {
			name = "Dealer";
			hand = new Hand();
			state = State.PLAYING;
		}

		public Hand hand() {
			return hand;
		}

		public void startsTurn() {
			state = State.PLAYING;
		}

		public void busted() {
			state = State.BUSTED;
		}

		public void blackjack() {
			state = State.BLACKJACK;
		}

		public void twentyoneScore() {
			state = State.TWENTYONE;
		}

		public boolean isBusted() {
			return state == State.BUSTED;
		}

		public boolean hasBlackjack() {
			return state == State.BLACKJACK;
		}

		public boolean hasTwentyone() {
			return state == State.TWENTYONE;
		}

		public String toString() {
			return name;
		}

		public String initialHandString() {
			return "\t" + hand.cards().get(0) + "\n" + "\t?\n";
		}
	}

	private Deck deck;
	private Dealer dealer;
	private List<Player> players;
	private List<Card> usedCards;

	private static Scanner in = new Scanner(System.in);

	private static enum Play { HIT, STAND, DOUBLE, SPLIT, SURRENDER };

	public Blackjack(Player[] players, Deck deck) throws IllegalArgumentException {
		if (players.length < 1 || players.length > 6) {
			throw new IllegalArgumentException("Number of players must be 1-6.");
		}
		this.deck = deck;
		dealer = new Dealer();
		this.players = new ArrayList<Player>(Arrays.asList(players));
		usedCards = new ArrayList<Card>();
	}

	private boolean playersFinished() {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isPlaying()) {
				return false;
			}
		}
		return true;
	}

	private void resetPlayersStates() {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).startsTurn();
		}
		dealer.startsTurn();
	}

	private void feedDeckIfEmpty() {
		if (deck.isEmpty()) {
			p("\nShuffling new deck...\n");
			resetDeck();
		}
	}

	private void resetDeck() {
		deck.add(usedCards);
		deck.shuffle();
		usedCards.clear();
	}

	private void placeBets() {
		p("\nPlayers, place your bets!\n");

		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			int bet;
			boolean betIsPossible = false;
			do {
				pnln(player + ", place your bet: ");
				bet = in.nextInt();
				if (bet <= 0) {
					p("Incorrect amount! Should be a possitive amount.");
				} else {
					boolean hasMoney = player.bet(bet);
					if (hasMoney) {
						betIsPossible = true;
					} else {
						p("Incorrect amount! You don't have that much money (max. " + player.getMoney() + ").");
					}
				}
			} while(!betIsPossible);

			player.setBet(bet);
		}
	}

	private void dealCards() {
		p("\nDealer dealing cards...\n");

		for (int i = 0; i < 2*players.size(); i++) {
			feedDeckIfEmpty();
			Card card = deck.draw();
			Player player = players.get(i % players.size());
			player.hand().add(card);
			p(player + " gets " + card);
	
			if ((i + 1) % players.size() == 0) {
				feedDeckIfEmpty();
				card = deck.draw();
				if (i + 1 == players.size()) {
					p(dealer + " gets " + card);
				} else {
					p(dealer + " gets a second card; face down");
				}
				dealer.hand().add(card);
			}
		}
	}

	private boolean isBlackjack(Hand hand) {
		if (hand.size() != 2) {
			return false;
		}
		Card card1, card2;
		card1 = hand.cards().get(0);
		card2 = hand.cards().get(1);

		boolean card1_is_ace, card2_is_ace;
		card1_is_ace = card1.getRank().isAce();
		card2_is_ace = card2.getRank().isAce();

		if (card1_is_ace && card2_is_ace) {
			return false;
		} else if (card1_is_ace) {
			return cardValue(card2) == 10;
		} else if (card2_is_ace) {
			return cardValue(card1) == 10;
		} else {
			return false;
		}
	}

	private int cardValue(Card card) {
		int rank = card.getRank().getValue();
		if (rank == 1) {
			int v;
			do {
				pnln("Choose rank for " + card + ", (1) or (11): ");
				v = in.nextInt();
				if (v == 1 || v == 11) {
					return v;
				} else {
					p("Invalid rank, you must choose 1 or 11.");
				}
			} while (!(v == 1 || v == 11));
		} else if (rank == 11 || rank == 12 || rank == 13) {
			return 10;
		} else {
			return rank;
		}
		return 0;
	}

	private int handValue(Hand hand) {
		List<Integer> hand_values = handValues(hand);

		if (hand_values.size() == 1) {
			return hand_values.get(0);
		} else {
			int first = hand_values.get(0), second = hand_values.get(1);
			if (first > 21 && second > 21) {
				if (first <= second) {
					return first;
				} else {
					return second;
				}
			} else if (first > 21) {
				return second;
			} else if (second > 21) {
				return first;
			} else if (first >= second) {
				return first;
			} else {
				return second;
			}
		}
	}

	private List<Integer> handValues(Hand hand) {
		List<Card> cards = hand.cards();
		boolean has_ace = false;
		List<Integer> hand_values = new ArrayList<Integer>();

		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getRank().isAce()) {
				has_ace = true;
			}
		}

		if (!has_ace) {
			int value = 0;
			for (int i = 0; i < cards.size(); i++) {
				value += cardValue(cards.get(i));
			}
			hand_values.add(value);
		} else {
			int value1 = 0, value2 = 0;

			for (int i = 0; i < cards.size(); i++) {
				Card card = cards.get(i);

				if (card.getRank().isAce()) {
					value1 += 1;
					value2 += 11;
				} else {
					int val = cardValue(card);
					value1 += val;
					value2 += val;
				}
			}

			if (value1 <= 21 && value2 <= 21) {
				hand_values.add(value1);
				hand_values.add(value2);
			} else {
				if (value1 <= value2) {
					hand_values.add(value1);
				} else {
					hand_values.add(value2);
				}
			}
		}
		return hand_values;
	} 

	private String handValueString(Hand hand) {
		List<Integer> hand_values = handValues(hand);
		if (hand_values.size() == 1) {
			return Integer.toString(hand_values.get(0));
		} else {
			return hand_values.get(0) + " or " + hand_values.get(1);
		}
	}

	private void printTurnsSummary() {
		if (playersFinished()) {
			p("Dealer's hand:\n" + dealer.hand());
		} else {
			p("Dealer's hand:\n" + dealer.initialHandString());
		}
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			Hand hand = player.hand();

			p("\n< " + player + " >\nCurrent hand:\n" + hand + "\nBet: " + player.getBet() + "\n");
			
			switch(player.getState()) {
				case PLAYING:
					p("This player is still on the game.");
					break;
				case STAND:
					p("This player chose to stand. Waiting for the end of the game.");
					break;
				case TWENTYONE:
					p("This played got a score of 21.");
					break;
				case BLACKJACK:
					p("This player got blackjack!");
					break;
				case BUSTED:
					p("This player was busted!");
					break;
				case RETIRED:
					p("This player has retired, will walk out of the game at the end with half bet back.");
					break;
			}
			p("---------------");
		}
	}

	private void printPlayersMoney() {
		p("Players' money:\n");

		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			p(player + ": " + player.getMoney());
		}
	}

	private void printPlayersHands() {
		p("\n\n---------------\nPlayers' hands:\n");

		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			p(player + ":\n" + player.hand());
		}
		p("---------------\n");
	}

	private void payOutWins() {
		p("\n------------- Players Payouts ---------------\n");

		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			int bet = player.getBet();

			if (dealer.hasBlackjack()) {
				if (player.hasBlackjack()) {
					player.setMoney(player.getMoney() + bet);
					p("Both dealer and " + player + " have blackjacks, which results in a push. Player gets back " + bet + ".");
				} else {
					player.setMoney(player.getMoney() - bet);
					p("The dealer has blackjack, " + player + " loses bet (" + bet + ").");
				}
			} else if (player.hasBlackjack()) {
				int win = bet + bet*3/2;
				player.setMoney(player.getMoney() + win);
				p(player + " has blackjack, gets paid at 3:2, winning " + win + ".");
			} else if (player.isBusted()) {
				p(player + " got busted, loses bet ( " + bet + ").");
			} else if (dealer.isBusted()) {
				p("Dealer got busted, player gets paid at 1:1, winning " + bet + ".");
				player.setMoney(player.getMoney() + bet);
			} else if (handValue(player.hand()) > handValue(dealer.hand())) {
				p(player + " has a higher scoring hand (" + handValue(player.hand()) + ") than dealer's " + handValue(dealer.hand()) + " , player gets paid at 1:1, winning " + bet + ".");
				player.setMoney(player.getMoney() + bet);
			} else {
				p(player + " has a lower scoring hand (" + handValue(player.hand()) + ") than dealer's " + handValue(dealer.hand()) + " , player loses bet, " + bet + ".");
				player.setMoney(player.getMoney() + bet);
			}

			p(player + "'s current money: " + player.getMoney() + "\n");
		}

		for (int i = 0; i < players.size(); i++) {
			players.get(i).setBet(0);
		}
	}

	private void clearHands() {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).hand().clear();
		}
		dealer.hand().clear();
	}

	private void expelRuinedPlayers() {
		for (Iterator<Player> iterator = players.iterator(); iterator.hasNext(); ) {
			Player player = iterator.next();
			if (player.getMoney() == 0) {
				p(player + " has no money left and got kicked out of the game!");
				iterator.remove();
			}
		}
	}

	private void pressAnyKeyToContinue() {
		p("Press any key to continue...");
		try {
			System.in.read();
		} catch(Exception e) {}
	}

	public void play() {

		p("\n\n#############################\n\n   WELCOME TO BLACKJACK 21\n\n#############################\n");

		deck.shuffle();

		while (!players.isEmpty()) {

			p("\n\n--------------- NEW ROUND ---------------\n");

			printPlayersMoney();

			resetPlayersStates();

			placeBets();

			dealCards();
			printPlayersHands();
			pressAnyKeyToContinue();

			while (!playersFinished()) {

				p("\n\n--------------- NEW ROUND OF TURNS ---------------");

				for (int i = 0; i < players.size(); i++) {

					Player player = players.get(i);

					if (player.isPlaying()) {

						feedDeckIfEmpty();

						p("\n< " + player + "'s turn > | Money " + player.getMoney() + " | Bet " + player.getBet());

						p("\nDealer's hand:\n" + dealer.initialHandString() + "\n" + player + "'s hand:\n" + player.hand());

						if (isBlackjack(player.hand())) {
							player.blackjack();
							p("\nYou have blackjack!!");
						} else {
							Hand player_hand = player.hand();

							p("Hand value: " + handValueString(player_hand) + "\n");

							Play play = player.choosePlay();
							p("");

							switch(play) {
								case HIT:
									Card card = deck.draw();
									p(player + " draws " + card + ".");
									player_hand.add(card);
									p("Resulting hand value: " + handValueString(player_hand));
									break;
								case STAND:
									p(player + " stands.");
									player.stand();
									break;
								case DOUBLE:
									p(player + " doubles bet.");
									break;
								case SPLIT:
									p(player + " splits hand.");
									break;
								case SURRENDER:
									p(player + " gives up half the bet and retires from the game.");
									break;
							}

							if (player.isPlaying()) {
								// choose rank innecesario en algunos casos (handValue)
								// controlar no elegir as=11 que se pase y el jugador acaba busted
								int hand_value = handValue(player_hand);
								if (hand_value > 21) {
									player.busted();
									p("\nYou have been busted!");
								} else if (hand_value == 21) {
									player.twentyoneScore();
									p("\nYou got a score of 21");
								}
							}
						}

						p("\n------------------------------");
						pressAnyKeyToContinue();
					}
				}

				p("--------------- END OF ROUND OF TURNS --------------- \n");
				p("\n----- SUMMARY -----\n");
				printTurnsSummary();
				pressAnyKeyToContinue();
			}

			p("\n--------------------------------------\n");
			p("\n\n<<<<<<<<<<<<<<< END OF ROUND >>>>>>>>>>>>>> \n");

			Hand dealer_hand = dealer.hand();
			p("\nDealer's hand:\n" + dealer_hand);

			if (isBlackjack(dealer_hand)) {
				dealer.blackjack();
				p("\nThe dealer got blackjack!");
			} else {
				int hand_value = handValue(dealer_hand);

				while (hand_value < 17) {
					feedDeckIfEmpty();
					Card card = deck.draw();
					p(dealer + " draws " + card + ".");
					dealer_hand.add(card);
					hand_value = handValue(dealer_hand);
				}
				p("\nDealer's final hand:\n" + dealer_hand + "\nDealer's hand value: " + hand_value + "\n");

				if (hand_value > 21) {
					dealer.busted();
					p("\nThe dealer got busted!");
				} else if (hand_value == 21) {
					dealer.twentyoneScore();
					p("\nThe dealer got a score of 21.");
				}
			}
			payOutWins();
			clearHands();
			expelRuinedPlayers();
			resetDeck();
			pressAnyKeyToContinue();
		}
	}

	public static <T> void p(T output) {
		System.out.println(output);

	}

	public static <T> void pnln(T output) {
		System.out.print(output);
	}

	public static void main(String[] args) {

		Player player1, player2, player3;
		player1 = new Player("Player 1", 500);
		player2 = new Player("Player 2", 650);
		player3 = new Player("Player 3", 230);

		Player[] players = { player1, player2, player3 };
		Deck deck = new Deck();
	
		Blackjack blackjack = new Blackjack(players, deck);

		blackjack.play();
	}
}