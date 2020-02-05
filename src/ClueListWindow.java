import processing.core.PApplet;

import java.util.List;

class ClueListWindow extends PApplet {

    private Board b;
    private CryptidSolver parent;

    public ClueListWindow(CryptidSolver parent, Board b) {
        super();
        this.parent = parent;
        this.b = b;
        PApplet.runSketch(new String[]{this.getClass().getSimpleName()}, this);
    }

    public void settings() {
        size(500, 200);
    }

    public void setup() {
        surface.setSize((int) (displayHeight * 0.2f), (int) (displayHeight * 0.7f));
        textAlign(LEFT, TOP);
    }

    public void draw() {
        background(255);
        textAlign(LEFT, TOP);
        stroke(CryptidSolver.playerColors[parent.player]);
        noFill();
        strokeWeight(5);
        rect(0, 0, width, height);
        fill(0);
        text("Player " + (parent.player+1), 0, 0);
        if (!b.isReady()) return;
        List<Clue> clues = parent.clues.get(parent.player);
        for (int i = 0; i < clues.size(); i++) {
            text(clues.get(i).toString(), 0, (i + 1) * 12);
        }
        textAlign(CENTER, BOTTOM);
        text("Cryptid is on one of " + parent.possibleSpacesCount() + " spaces.", width/2, height);
    }

    public void keyPressed() {
        if (key < 49 || key > 53) return;
        parent.player = key - 49;
    }
}