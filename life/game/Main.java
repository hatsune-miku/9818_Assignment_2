package life.game;

import life.Life;
import life.view.GOLMain;

public class Main {
    public static void main(String[] args) {
        new GOLMain(new Life()).present();
    }
}
