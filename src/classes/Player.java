package classes;

public class Player {
    private final String codename;
    private final int score;

    public Player(String id, int score) {
        this.codename = id;
        this.score = score;
    }

    public String getCodename() {
        return codename;
    }

    public int getScore() {
        return score;
    }
}
