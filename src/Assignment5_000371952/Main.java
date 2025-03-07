package Assignment5_000371952;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Creates a User Interface and acts as the View for the Game Class
 * the User can use the UI to play a game(s) of blackjack with almost all of the actual rules.
 *
 * @author John Young, 000371952
 */
public class Main extends Application {

    //Instance Variables
    private Game almostBJ;
    private GraphicsContext gc;
    private Label handStatus, pTotal, dTotal, tHands, wHands, lives, deckRemain, cards, info, hiScore;
    private Button butNext, butHit, butStay, butPlay, butNewGame;
    private Pane root;
    // Event Handlers

    /**
     * event handler for the play button, when clicked calls the setup method and makes the rules smaller
     * and moves them to the side, also removes the play button and adds the newGame button which will be used later,
     * and updates more labels
     *
     * @param e onClick event
     */
    private void play(ActionEvent e) {
        setup(gc);
        info.setText("RULES: \n" +
                "Player wins if they have a higher card \nvalue than the dealer.\n\n" +
                "Player wins if they get 5 cards without \ngoing over 21 or BLACKJACK!\n\n" +
                "The Dealer will win if they have an \nequal or higher value than the Player.\n\n" +
                "The Dealer will always hit if below 17 \nand stay if they have a value of 17 or \nhigher.\n\n" +
                "You will GAME OVER if you run out of \nlives! Win to get more!");
        info.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        info.relocate(750, 345);
        deckRemain.setText("Cards remaining in deck: 48");
        deckRemain.relocate(760, 160);
        deckRemain.setFont(Font.font("Verdana", 14));

        root.getChildren().removeAll(butPlay);
        root.getChildren().addAll(butNewGame);

        butHit.setDisable(false);
        butStay.setDisable(false);
        butNewGame.setDisable(true);
    }

    /**
     * event handler for the Hit button, used when the Player wants another card
     * also calls the update method
     *
     * @param e onClick event
     */
    private void hitMe(ActionEvent e) {
        almostBJ.playerHit();
        update();
    }

    /**
     * event handler for the Stay button, used when the Player is done with their turn
     * also calls the update method
     *
     * @param e onClick event
     */
    private void dealersTurn(ActionEvent e) {
        almostBJ.setPlayerStay();
        update();
    }

    /**
     * event handler for the Stay button, used when the Player is ready for the next hand
     * sets the hand outcome label to nothing in preparation for the next result
     * re-enables the Hit and Stay buttons and disables the nextHand button
     * calls the reset method to revert the Player's and Dealer's hands
     * also calls the update method
     *
     * @param e onClick event
     */
    private void goNext(ActionEvent e) {
        handStatus.setText("");
        handStatus.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 24));
        handStatus.setTextFill(Color.BLACK);
        butNext.setDisable(true);
        butHit.setDisable(false);
        butStay.setDisable(false);
        almostBJ.reset(handStatus, butHit, butStay, butNext, butNewGame);
        update();
    }

    /**
     * event handler for the newGame button, used when the Player GAME OVERs and wants to play again
     * sets the hand outcome label to nothing to erase the GAME OVER message
     * re-enables the Hit and Stay buttons and disables the newGame button
     * calls the reset method to revert the game back to a default state
     * also calls the update method
     *
     * @param e onClick event
     */
    private void newGame(ActionEvent e) {
        handStatus.setText("");
        handStatus.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 24));
        handStatus.setTextFill(Color.BLACK);
        almostBJ.reset(handStatus, butHit, butStay, butNext, butNewGame);
        update();
        butNewGame.setDisable(true);
        butHit.setDisable(false);
        butStay.setDisable(false);
    }

    //Helper Methods

    /**
     * calls the result method to check the Players hand against the Dealers
     * then updates all labels with current stats from the Game model
     */
    private void update() {
        result();
        almostBJ.draw(gc);
        pTotal.setText("Total in Hand: " + almostBJ.getPlayerTotal());
        dTotal.setText("Total in Hand: " + almostBJ.getDealerTotal());
        tHands.setText("Total Hands: " + almostBJ.getTotalHands());
        wHands.setText("Hands Won: " + almostBJ.getHandsWon());
        lives.setText("Lives: " + almostBJ.getLives() + " ♥");
        hiScore.setText("Hi-Score: " + almostBJ.getHighScore());
        deckRemain.setText("Cards remaining in deck: " + almostBJ.getCardsInDeck());
        cards.setText(almostBJ.toString());
    }

    /**
     * calls the Game model's result method to check if either the Player or Dealer has won or bust
     * passes all buttons as arguments, so they can be disabled or re-enabled accordingly
     */
    private void result() {
        almostBJ.result(handStatus, butHit, butStay, butNext);
    }

    /**
     * Creates the borders of the UI and fills in sections with color
     *
     * @param gc the graphics context object created in the Main class
     */
    private void startUp(GraphicsContext gc) {
        gc.setFill(Color.DARKOLIVEGREEN);
        gc.fillRect(0, 0, 1000, 600);

        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(740, 0, 260, 600);
        gc.fillRect(0, 500, 740, 100);

        gc.setFill(Color.TAN);
        gc.fillRect(0, 400, 740, 100);

        gc.setLineWidth(8);
        gc.strokeLine(0, 400, 740, 400);
        gc.strokeLine(0, 500, 740, 500);
        gc.strokeLine(740, 0, 740, 600);
        gc.strokeLine(740, 140, 1000, 140);
        gc.strokeLine(740, 340, 1000, 340);
        gc.strokeLine(0, 2, 1000, 2);
        gc.strokeLine(0, 598, 1000, 598);
        gc.strokeLine(2, 0, 2, 600);
        gc.strokeLine(998, 0, 998, 600);
    }

    /**
     * Sets up the game space after the user has read the rules (hopefully) and creates necessary labels,
     * also deals the first round of cards and has the Game model draw them
     *
     * @param gc the graphics context object created in the Main class
     */
    private void setup(GraphicsContext gc) {
        almostBJ.deal();
        almostBJ.draw(gc);

        Label dHand = new Label("Dealer's Hand: ");
        Label pHand = new Label("Your Hand: ");
        dTotal = new Label("Total in Hand: ");
        pTotal = new Label("Total in Hand: ");

        root.getChildren().addAll(dHand, pHand, dTotal, pTotal);

        dHand.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 25));
        dHand.setTextFill(Color.BLACK);
        pHand.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 25));
        pHand.setTextFill(Color.BLACK);
        dTotal.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, FontPosture.ITALIC, 16));
        pTotal.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, FontPosture.ITALIC, 16));

        dHand.relocate(10, 75);
        pHand.relocate(10, 275);
        dTotal.relocate(35, 115);
        pTotal.relocate(35, 315);

        pTotal.setText("Total in Hand: " + almostBJ.getPlayerTotal());
        dTotal.setText("Total in Hand: " + almostBJ.getDealerTotal());
        cards.setText(almostBJ.toString());

        update();
    }

    /**
     * Start method to create the stage and scene
     *
     * @param stage The main stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        root = new Pane();
        Scene scene = new Scene(root, 1000, 600); //Setting the window size
        stage.setTitle("Almost Blackjack!"); //Setting the window's title
        stage.setScene(scene);

        almostBJ = new Game(); //Creating the model

        Canvas canvas = new Canvas(1000, 600); //Creates a canvas the same size as the window

        //Initial GUI components
        butHit = new Button("Hit!");
        butStay = new Button("Stay!");
        butNext = new Button("Next Hand");
        butPlay = new Button("Play!");
        butNewGame = new Button("Play Again!");
        //Label for the rules for the game
        info = new Label("Welcome to my (Almost) Blackjack game!\n\n" +
                "Here are the rules: Try to get as close to 21 as possible without going over!\n" +
                "See how many times you can win before you run out of lives! Win to get more!\n\n" +
                "Aces are valued at 11 or 1, Face cards are valued at 10.\nThe rest share their value with their " +
                "number.\n\n" +
                "The player will win if they have a higher card value than the dealer.\n\n" +
                "The Player can also win if they get BLACKJACK or \nif they get 5 cards without going over 21!\n\n" +
                "The Dealer will win if they have an equal or higher value than the Player \n(unless player has" +
                " BLACKJACK).\n\n" +
                "The Dealer will always hit if below 17 and stay if they have a value of 17 or higher. \n\n" +
                "Press Play to start!");
        info.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 16));
        info.setTextFill(Color.BLACK);
        //Blank for now, will show the result of the round later
        handStatus = new Label("");
        //Important information labels
        tHands = new Label("Total Hands: 0");
        wHands = new Label("Hands Won: 0");
        lives = new Label("Lives: 10 ♥");
        hiScore = new Label("Hi-Score: 0");
        deckRemain = new Label("");
        cards = new Label("");

        //Adds GUI components to the scene
        root.getChildren().addAll(canvas, butHit, butStay, butPlay, info, tHands, wHands, lives, hiScore,
                deckRemain, butNext, handStatus, cards);

        //Configuring all initial components
        butHit.relocate(50, 525);
        butHit.setStyle("-fx-font-size:25;-fx-border-width: 4px;-fx-border-color: black; -fx-border-radius: 2px");
        butHit.setDisable(true);

        butStay.relocate(200, 525);
        butStay.setStyle("-fx-font-size:25;-fx-border-width: 4px;-fx-border-color: black; -fx-border-radius: 2px");
        butStay.setDisable(true);

        butNext.relocate(350, 525);
        butNext.setStyle("-fx-font-size:25;-fx-border-width: 4px;-fx-border-color: black; -fx-border-radius: 2px");
        butNext.setDisable(true);

        butPlay.relocate(550, 525);
        butPlay.setStyle("-fx-font-size:25;-fx-border-width: 4px;-fx-border-color: black; -fx-border-radius: 2px");

        butNewGame.relocate(550, 525);
        butNewGame.setStyle("-fx-font-size:25;-fx-border-width: 4px;-fx-border-color: black; -fx-border-radius: 2px");

        info.relocate(10, 10);

        handStatus.relocate(20, 420);
        handStatus.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 24));

        tHands.relocate(750, 15);
        wHands.relocate(750, 40);
        lives.relocate(750, 65);
        hiScore.relocate(750, 100);

        tHands.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, FontPosture.ITALIC, 22));
        wHands.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, FontPosture.ITALIC, 22));
        lives.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, FontPosture.ITALIC, 20));
        hiScore.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, FontPosture.ITALIC, 22));

        cards.relocate(770, 180);
        cards.setFont(Font.font("Verdana", 14));

        //Add event handlers to buttons
        butHit.setOnAction(this::hitMe);
        butStay.setOnAction(this::dealersTurn);
        butNext.setOnAction(this::goNext);
        butPlay.setOnAction(this::play);
        butNewGame.setOnAction(this::newGame);
        //final setup before showing the stage
        gc = canvas.getGraphicsContext2D();
        startUp(gc);
        //Showing the stage
        stage.show();
    }

    /**
     * Main method to launch everything
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);
    }
}
