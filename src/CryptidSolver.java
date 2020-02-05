import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class CryptidSolver extends PApplet {

    private Board b;
    public int mode = 0;
    private List<Integer> boardNums = new ArrayList<>();
    private List<Boolean> boardFlipped = new ArrayList<>();
    private ClueListWindow win;
    public int player = 0;
    public List<List<Clue>> clues = new ArrayList<>();

    public static int[] playerColors = {0xff47fffc, 0xff12cd4a, 0xffaa55ff, 0xffb50000, 0xffff9e3e};

    public static void main(String[] args) {
        PApplet.main("CryptidSolver");
    }

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        surface.setSize((int) (displayHeight * 0.9f), (int) (displayHeight * 0.7f));
        b = new Board(this, boardNums, boardFlipped);
        cursor(CROSS);
        for (int i = 0; i < 5; i++) {
            clues.add(new ArrayList<Clue>());
        }
    }

    public void draw() {
        background(0);
        b.display();
        if (mode == 1) {
            float r = 0.4f * min(width / 12.f, height / 9.5f);
            switch (b.nBuildings()) {
                case 0:
                    fill(0xff1e50e6);
                    polygon(mouseX, mouseY, r, 8);
                    break;
                case 1:
                    fill(0xff1e50e6);
                    polygon(mouseX, mouseY, r, 3);
                    break;
                case 2:
                    fill(0xffffffff);
                    polygon(mouseX, mouseY, r, 8);
                    break;
                case 3:
                    fill(0xffffffff);
                    polygon(mouseX, mouseY, r, 3);
                    break;
                case 4:
                    fill(0xff009933);
                    polygon(mouseX, mouseY, r, 8);
                    break;
                case 5:
                    fill(0xff009933);
                    polygon(mouseX, mouseY, r, 3);
                    break;
                case 6:
                    fill(0xff000000);
                    polygon(mouseX, mouseY, r, 8);
                    break;
                case 7:
                    fill(0xff000000);
                    polygon(mouseX, mouseY, r, 3);
                    break;
                case 8:
                    mode++;
                    win = new ClueListWindow(this, b);
                    clues = b.inferAll();
            }
        }
    }

    public int possibleSpacesCount() {
        return b.possibleSpacesCount();
    }

    public void mousePressed() {
        int[] coord = b.getRowCol(mouseX, mouseY);
        if (coord[0] < 0 || coord[0] >= 9) return;
        if (mode == 1) {
            b.addBuilding(coord);
        } else if (mode == 2) {
            b.changeToken(coord[0], coord[1], player);
            clues = b.inferAll();
        }
    }

    public void keyPressed() {
        if (mode == 0) {
            if (keyCode == ENTER && boardNums.size() == 6) {
                mode++;
                return;
            }
            if (key < 49 || key > 54) return;
            int n = key - 48;
            if (boardNums.size() == 0 || boardNums.get(boardNums.size() - 1) != n && boardNums.size() < 6) {
                boardNums.add(n);
                boardFlipped.add(false);
            } else {
                boardFlipped.set(boardFlipped.size() - 1, !boardFlipped.get(boardFlipped.size() - 1));
            }
            b = new Board(this, boardNums, boardFlipped);
        } else if (mode == 1 && keyCode == ENTER && b.nBuildings() == 6) {
            mode++;
            win = new ClueListWindow(this, b);
            clues = b.inferAll();
        } else if (mode == 2) {
            if (key < 49 || key > 53) return;
            player = key - 49;
//            clues = b.inferAll();
        }
    }

    public void polygon(float x, float y, float radius, int npoints) {
        float angle = TWO_PI / npoints;
        beginShape();
        float offset = npoints % 2 == 0 ? HALF_PI - angle / 2 : HALF_PI;
        for (float a = 0; a < TWO_PI; a += angle) {
            float sx = x + cos(a - offset) * radius;
            float sy = y + sin(a - offset) * radius;
            vertex(sx, sy);
        }
        endShape(CLOSE);
    }

    public void drawX(float x, float y, float w, float h) {
        pushStyle();
        strokeWeight(5);
        stroke(0, 100);
        line(x, y, x+w, y+h);
        line(x+w, y, x, y+h);
        popStyle();
    }

}
