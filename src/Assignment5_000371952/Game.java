package Assignment5_000371952;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Model class for the Main(View) class
 * represents the game of Blackjack and carries out all calculations needed to play
 * and follows most of the actual rules
 * @author John Young, 000371952
 */
public class Game {
    /**
     * The amount of cards in a standard deck, cannot be changed and can be accessed wherever its needed
     **/
    private static final int maxCards = 52;
    /**
     * An array to hold the values of all the cards in a deck
     **/
    private int[] deck;
    /**
     * An array to hold the values of all the cards in the Dealer's hand
     **/
    private int[] dealer;
    /**
     * An array to hold the values of all the cards in the Player's hand
     **/
    private int[] player;
    /**
     * The amount of cards in the Dealer's hand
     **/
    private int dealerCards;
    /**
     * The amount of cards in the Player's hand
     **/
    private int playerCards;
    /**
     * The amount of hands won by the Player
     **/
    private int handsWon;
    /**
     * The total amount of hands the Player has played
     **/
    private int totalHands;
    /**
     * The current amount of cards left in the deck
     **/
    private int cardsInDeck;
    /**
     * The Player's highest score
     **/
    private int hiScore;
    /**
     * The amount of lives the player has left
     **/
    private int lives;
    /**
     * boolean variable to record if the Player has bust
     **/
    private boolean playerBust;
    /**
     * boolean variable to record if the Player has decided to end their turn
     **/
    private boolean playerStay;
    /**
     * boolean variable to record if the Dealer has bust
     **/
    private boolean dealerBust;
    /**
     * boolean variable to record if the hand has ended
     **/
    private boolean endOfHand;
    /**
     * boolean variable to record if it is time for the Dealer to flip over their first card
     **/
    private boolean showDealer;
    /**
     * boolean variable to record if the Player has lost the game
     **/
    private boolean gameOver;

    /**
     * Constructor for the Game class
     * initializes the deck with its max size and all card values
     * initializes both the Player's and Dealer's hands with their max size, but sets all card values to 0
     * initializes all other instance variables with their default game state values
     */
    public Game() {
        this.deck = new int[maxCards + 1];
        int card = 1;
        for (int i = 0; i < maxCards; i++) {
            this.deck[i] = card;
            card++;
            if (card >= 14) {
                card = 1;
            }
        }

        this.dealer = new int[5];
        this.player = new int[5];

        for (int j = 0; j < 5; j++) {
            this.dealer[j] = 0;
            this.player[j] = 0;
        }
        this.dealerCards = 0;
        this.playerCards = 0;
        this.handsWon = 0;
        this.totalHands = 0;
        this.cardsInDeck = 52;
        this.hiScore = 0;
        this.lives = 10;
        this.playerBust = false;
        this.playerStay = false;
        this.dealerBust = false;
        this.endOfHand = false;
        this.showDealer = false;
        this.gameOver = false;
    }

    /**
     * Deals a card from the deck to ether the Player or Dealer and removes it from the deck
     *
     * @return the value of the card dealt
     */
    public int getCard() {
        int cardDealt = 0;
        while (cardDealt == 0) {
            int card = ((int) (Math.random() * ((maxCards - 1) + 1)));
            if (this.deck[card] != 0) {
                cardDealt = this.deck[card];
                this.deck[card] = 0;
                this.cardsInDeck--;
                if (this.cardsInDeck < 1) {
                    this.reshuffle();
                }
            }
        }
        return cardDealt;
    }

    /**
     * Deals the initial two cards to both the Dealer and Player
     * Doesn't let the player Hit if they already have BlackJack
     */
    public void deal() {
        for (int i = 0; i < 2; i++) {
            this.player[i] = this.getCard();
            this.dealer[i] = this.getCard();
            this.playerCards++;
            this.dealerCards++;

            if (this.getPlayerTotal() == 21) {
                this.setPlayerStay();
            }
        }
    }

    /**
     * method to "reshuffle" the deck when it runs out of cards, just recapitalizes the deck to its default values
     * is only called from inside the Game class
     */
    private void reshuffle() {
        this.cardsInDeck = 52;
        int card = 1;
        for (int i = 0; i < maxCards; i++) {
            this.deck[i] = card;
            card++;
            if (card >= 14) {
                card = 1;
            }
        }
    }

    /**
     * method to simulate the Dealer's turn
     * reveals the Dealer's first card (that starts flipped down), then proceeds to Hit if below 17 and Stays if at 17
     * or higher, if the Player hasn't bust already
     */
    public void dealerPlay() {
        this.showDealer = true;
        if (!this.playerBust) {
            boolean dealerStay = false;
            while (!dealerStay) {
                if (this.getDealerTotal() > this.getPlayerTotal()) {
                    dealerStay = true;
                } else if (this.getDealerTotal() < 17 && this.dealerCards < 5) {
                    this.dealer[dealerCards] = this.getCard();
                    this.dealerCards++;
                    if (this.getDealerTotal() > 21) {
                        this.dealerBust = true;
                        dealerStay = true;
                    }
                } else {
                    dealerStay = true;
                }
            }
        }
    }

    /**
     * Method to deal the player more cards when they desire, if they haven't bust already and have not chosen to Stay
     * Busts the Player if they go over 21, and forces them to Stay if they get exactly 21 or get 5 cards
     */
    public void playerHit() {
        if (!this.playerBust && !this.playerStay) {
            this.player[playerCards] = this.getCard();
            this.playerCards++;
            if (this.playerCards == 5) {
                this.endOfHand = true;
            }

            if (this.getPlayerTotal() > 21) {
                this.playerBust = true;
                this.showDealer = true;
            }

            if (this.getPlayerTotal() == 21) {
                this.setPlayerStay();
            }
        }
    }

    /**
     * gets the total value of the Player's hand
     * Changes the value of an Ace from 11 to 1 if their total would exceed 21 before returning the value
     * Also changes the value of all face cards to 10
     *
     * @return the total value of the Player's hand
     */
    public int getPlayerTotal() {
        int total = 0;
        for (int i = 0; i < this.playerCards; i++) {
            if (this.player[i] > 10) {
                total += 10;
            } else if (this.player[i] == 1) {
                total += 11;
            } else {
                total += this.player[i];
            }
        }
        if (total > 21) {
            for (int j = 0; j < this.playerCards; j++) {
                if (this.player[j] == 1) {
                    total -= 10;
                }
            }
        }
        return total;
    }

    /**
     * gets the total value of the Dealer's hand
     * Changes the value of an Ace from 11 to 1 if their total would exceed 21 before returning the value
     * Also changes the value of all face cards to 10 and has the extra step of hiding the value of the first card
     * until it is revealed
     *
     * @return the total value of the Dealer's hand
     */
    public int getDealerTotal() {
        int total = 0;
        for (int i = 0; i < this.dealerCards; i++) {
            if (this.dealer[i] > 10) {
                total += 10;
            } else if (this.dealer[i] == 1) {
                total += 11;
            } else {
                total += this.dealer[i];
            }
        }
        if (!this.showDealer) {
            if (this.dealer[0] > 10) {
                total -= 10;
            } else {
                total -= this.dealer[0];
            }
        }
        if (total > 21) {
            for (int j = 0; j < this.dealerCards; j++) {
                if (this.dealer[j] == 1) {
                    total -= 10;
                }
            }
        }
        return total;
    }

    /**
     * Method to calculate the end result of the round, who wins, who loses
     * updates the color of the result message accordingly along with the Player's wins and lives, as well as the total
     * hands
     *
     * @param msg  the hand result label from the Main Class
     * @param hit  the Hit button from the Main class
     * @param stay the Stay button from the Main class
     * @param next the Next Hand button from the Main class
     */
    public void result(Label msg, Button hit, Button stay, Button next) {
        if (this.playerCards == 5 && this.getPlayerTotal() <= 21) {
            msg.setTextFill(Color.DARKGREEN);
            msg.setText("You Win! \nYou got 5 Cards without busting!");
            this.handsWon++;
            this.lives++;
        } else if (this.getPlayerTotal() == 21 && this.playerCards == 2) {
            msg.setTextFill(Color.GOLD);
            msg.setText("B L A C K J A C K! WINNER!");
            msg.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 46));
            this.handsWon++;
            this.lives++;
            this.endOfHand = true;
        } else if (this.playerStay) {
            dealerPlay();
            if (this.dealerBust) {
                msg.setTextFill(Color.DARKGREEN);
                msg.setText("You Won! \nDealer Bust! ");
                this.handsWon++;
                this.lives++;
                this.endOfHand = true;
            } else if (!this.playerBust && this.getPlayerTotal() > this.getDealerTotal()) {
                msg.setTextFill(Color.DARKGREEN);
                msg.setText("You Won! \nYour hand was better than the dealers!");
                this.handsWon++;
                this.lives++;
                this.endOfHand = true;
            } else if (!this.dealerBust && this.getDealerTotal() >= this.getPlayerTotal()) {
                msg.setTextFill(Color.DARKRED);
                msg.setText("You Lost! \nDealer's hand was equal to or greater than yours!");
                this.endOfHand = true;
                this.lives--;
            }
        } else {
            if (this.playerBust) {
                msg.setTextFill(Color.DARKRED);
                msg.setText("You Lost! \nYou went over 21!");
                this.endOfHand = true;
                this.lives--;
            }
        }
        //disables the Hit and Stay buttons until the Player clicks the Next Hand button and increases the total hands
        if (this.endOfHand) {
            this.totalHands++;
            next.setDisable(false);
            hit.setDisable(true);
            stay.setDisable(true);
        }
        //updates the Player's high score
        if (this.handsWon > this.hiScore) {
            this.hiScore = this.handsWon;
        }
        //sets the game state to GAME OVER if the Player runs out of lives
        if (this.getLives() < 1) {
            this.gameOver = true;
        }
    }

    /**
     * Reset method to either change the round variables to default or the entire game state to default, depending on if
     * the Player has gotten a GAME OVER
     *
     * @param msg  the hand result label from the Main Class
     * @param hit  the Hit button from the Main class
     * @param stay the Stay button from the Main class
     * @param next the Next Hand button from the Main class
     * @param play the Play Again button from the Main class
     */
    public void reset(Label msg, Button hit, Button stay, Button next, Button play) {

        this.playerCards = 0;
        this.dealerCards = 0;
        this.playerBust = false;
        this.dealerBust = false;
        this.playerStay = false;
        this.endOfHand = false;
        this.showDealer = false;

        for (int i = 0; i < 5; i++) {
            this.player[i] = 0;
            this.dealer[i] = 0;
        }
        if (this.gameOver) {

            msg.setText("!   !   !   GAME OVER   !   !   !");
            msg.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 48));
            play.setDisable(false);
            next.setDisable(true);
            hit.setDisable(true);
            stay.setDisable(true);
            this.gameOver = false;
            this.totalHands = 0;
            this.handsWon = 0;
            this.lives = 10;
            this.reshuffle();
        } else {
            hit.setDisable(false);
            stay.setDisable(false);
            this.deal();
        }
    }

    /**
     * changes the Player's state to Stay
     */
    public void setPlayerStay() {
        this.playerStay = true;
    }

    /**
     * get method to access the amount of hands won by the Player
     *
     * @return the amount of hands won by the Player
     */
    public int getHandsWon() {
        return this.handsWon;
    }

    /**
     * get method to access the total amount of hands played by the Player
     *
     * @return the total amount of hands played by the Player
     */
    public int getTotalHands() {
        return this.totalHands;
    }

    /**
     * get method to access the amount of lives the Player has
     *
     * @return the amount of lives the Player has
     */
    public int getLives() {
        return this.lives;
    }

    /**
     * get method to access the Player's highest score
     *
     * @return the Player's highest score
     */
    public int getHighScore() {
        return this.hiScore;
    }

    /**
     * get method to access the total amount of cards left in the deck
     *
     * @return the total amount of cards left in the deck
     */
    public int getCardsInDeck() {
        return this.cardsInDeck;
    }

    /**
     * Method to calculate each card's position and call the drawCard method
     * Hides the Dealer's first card and draws all the rest
     *
     * @param gc the graphics context object created in the Main class
     */
    public void draw(GraphicsContext gc) {
        int count = 0;

        if (this.endOfHand) {
            showDealer = true;
        }

        for (int i = 0; i < 5; i++) {
            if (this.dealer[i] != 0) {
                drawCard(230 + count, 50, this.dealer, i, gc, showDealer);
            } else {
                drawCard(230 + count, 50, gc);
            }

            if (this.player[i] != 0) {
                drawCard(230 + count, 250, this.player, i, gc, true);
            } else {
                drawCard(230 + count, 250, gc);
            }
            count += 100;
        }
    }

    /**
     * Method to draw all cards on the canvas when they are dealt
     * Draws a ? for the Dealer's first card until it is revealed
     * Draws the letter representative for all face cards and aces
     *
     * @param x        the card's x co-ordinate
     * @param y        the card's y co-ordinate
     * @param hand     an array to hold the values of the Player's or Dealer's hand
     * @param num      the current card to be drawn
     * @param gc       the graphics context object created in the Main class
     * @param showCard boolean variable that decides if the Dealer's first card is to be shown
     */
    public void drawCard(int x, int y, int[] hand, int num, GraphicsContext gc, boolean showCard) {
        String cardVal = "";

        gc.setFill(Color.WHITE);
        gc.fillRect(x, y, 80, 100);
        gc.setLineWidth(2);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, 80, 100);

        if (showCard || num > 0) {
            if (hand[num] == 11) {
                cardVal += "J";
            } else if (hand[num] == 12) {
                cardVal += "Q";
            } else if (hand[num] == 13) {
                cardVal += "K";
            } else if (hand[num] == 1) {
                cardVal += "A";
            } else {
                cardVal = String.valueOf(hand[num]);
            }
        } else {
            cardVal = "?";

        }
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(50));
        gc.fillText(cardVal, x + 10, y + 50);
    }

    /**
     * Overloaded drawCard method to cover up drawn cards in preparation for the next hand
     *
     * @param x  the card's x co-ordinate
     * @param y  the card's y co-ordinate
     * @param gc the graphics context object created in the Main class
     */
    public void drawCard(int x, int y, GraphicsContext gc) {
        gc.setFill(Color.DARKOLIVEGREEN);
        gc.fillRect(x, y, 80, 100);
        gc.setStroke(Color.DARKOLIVEGREEN);
        gc.strokeRect(x, y, 80, 100);
    }

    /**
     * Custom toString method for the Game object
     * Runs through the deck and adds each card's value to the String, excluding all that have been played already,
     * so the Player can see what cards are still in the deck
     * Prints two newLine characters every 13 values to represent each theoretical suit that a deck of cards should
     * have, but are not implemented in this class
     *
     * @return Custom toString message that shows what cards are still in the deck
     */
    @Override
    public String toString() {
        String msg = "";
        for (int i = 0; i < maxCards; i++) {
            if (this.deck[i] == 11) {
                msg += "J ";
            } else if (this.deck[i] == 12) {
                msg += "Q ";
            } else if (this.deck[i] == 13) {
                msg += "K ";
            } else if (this.deck[i] == 1) {
                msg += "A ";
            } else if (this.deck[i] == 0) {
                msg += "_ ";
            } else
                msg += (this.deck[i]) + " ";

            if ((i + 1) % 13 == 0) {
                msg += "\n\n";
            }
        }
        return msg;
    }
}

