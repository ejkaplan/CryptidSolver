import java.util.Arrays;
import java.util.Set;

public class Clue {

    private int dist;
    private String[] terrain;
    private boolean negated;

    public Clue(int dist, String[] terrain, boolean negated) {
        this.dist = dist;
        this.terrain = terrain;
        this.negated = negated;
    }

    @Override
    public String toString() {
        String out = negated ? "NOT within " : "Within ";
        out += dist + " of ";
        out += Arrays.toString(terrain);
        return out;
    }

    public Clue (int dist, String terrain, boolean negated) {
        this(dist, new String[]{terrain}, negated);
    }

    public Clue(int dist, String[] terrain) {
        this(dist, terrain, false);
    }

    public Clue (int dist, String terrain) {
        this(dist, terrain, false);
    }

    public boolean evaluate(Board b, int r, int c) {
        boolean answer = false;
        Set<String> neighbors = b.getFeaturesAtDistance(r, c, dist);
        terrainLoop: for (String t : terrain) {
            for (String val : neighbors) {
                if (val.equals(t)){
                    answer = true;
                    break terrainLoop;
                }
            }
        }
        return negated ? !answer : answer;
    }

    public boolean clueValid(Board b, int r, int c) {
        return evaluate(b, r, c) && !b.isBad(r, c);
    }

}
