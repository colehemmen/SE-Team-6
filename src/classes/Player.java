package classes;

public class Player {
    private final String id;
    private final int score;

    public Player(String id, int score) {
        this.id = id;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }
}
