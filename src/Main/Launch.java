package Main;


/**
 * Created by AlexVR on 7/1/2018.
 */

public class Launch {

    public static void main(String[] args) {
        GameSetUp game = new GameSetUp("Snake", 600, 600); //Decreased game window size for better compatibility with small screens
        game.start();
    }
}
