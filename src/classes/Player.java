package classes;

public class Player {
    private String codename;
    private final int score;

    public Player(String id, int score) {
        this.codename = id;
        this.score = score;
    }

    public void setCodename(String addB) {
        this.codename = addB;
    }

    public String getCodename() {
        return codename;
    }

    public int getScore() {
        return score;
    }
}
