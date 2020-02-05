import java.util.*;

public class Board {

    private static String[] terrains = {"wwwwffsswdffssdddf", "sfffffssfdddsmmmmd", "ssfffwssfmwwmmmmww",
            "ddmmmmddmwwwdddfff", "sssmmmsddwmmddwwww", "ddsssfmmssffmwwwwf"};
    private static String[] animals = {"               bbb", "ccc               ", "      cc    c     ",
            "           c     c", "           b    bb", "b     b           "};

    private BoardSegment[][] segments;
    private CryptidSolver parent;
    private List<int[]> buildingLocs;
    private List<PlayerToken[][]> tokens;
    private List<PlayerToken[][]> inferences;

    public Board(CryptidSolver parent, List<Integer> boardNums, List<Boolean> boardFlipped) {
        this.parent = parent;
        buildingLocs = new ArrayList<>(); // blue stone & shack, white stone & shack, black stone & shack (maybe)
        segments = new BoardSegment[3][2];
        tokens = new ArrayList<>();
        inferences = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PlayerToken[][] pt = new PlayerToken[9][12];
            PlayerToken[][] it = new PlayerToken[9][12];
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 12; c++) {
                    pt[r][c] = PlayerToken.NONE;
                    it[r][c] = PlayerToken.NONE;
                }
            }
            tokens.add(pt);
            inferences.add(it);
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 2; c++) {
                if (boardNums.size() > 2 * r + c) {
                    int n = boardNums.get(2 * r + c) - 1;
                    boolean rotate = boardFlipped.get(2 * r + c);
                    segments[r][c] = new BoardSegment(terrains[n], animals[n], rotate);
                } else {
                    segments[r][c] = null;
                }
            }
        }
    }

    public Set<int[]> getNeighbors(int r, int c) {
        Set<int[]> n = new HashSet<>();
        if (r - 1 >= 0) n.add(new int[]{r - 1, c});
        if (c - 1 >= 0) n.add(new int[]{r, c - 1});
        if (r + 1 < 9) n.add(new int[]{r + 1, c});
        if (c + 1 < 12) n.add(new int[]{r, c + 1});
        if (c % 2 == 0 && r - 1 >= 0) {
            if (c - 1 >= 0) n.add(new int[]{r - 1, c - 1});
            if (c + 1 < 12) n.add(new int[]{r - 1, c + 1});
        } else if (c % 2 == 1 && r + 1 < 9) {
            n.add(new int[]{r + 1, c - 1});
            if (c + 1 < 12) n.add(new int[]{r + 1, c + 1});
        }
        return n;
    }

    public Set<String> getFeaturesAtDistance(int r, int c, int d) {
        Set<String> out = new HashSet<>();
        Set<int[]> frontier = new HashSet<>();
        frontier.add(new int[]{r, c});
        for (int i = 0; i < d + 1; i++) {
            Set<int[]> newFrontier = new HashSet<>();
            for (int[] coord : frontier) {
                out.add("" + terrainAt(coord[0], coord[1]));
                char animal = animalAt(coord[0], coord[1]);
                if (animal != ' ') out.add("" + animal);
                for (int j = 0; j < buildingLocs.size(); j++) {
                    if (Arrays.equals(buildingLocs.get(j), coord)) {
                        out.add(new String[]{"b-s", "b-a", "w-s", "w-a", "g-s", "g-a", "bl-s", "bl-a"}[j]);
                    }
                }
                newFrontier.addAll(getNeighbors(coord[0], coord[1]));
            }
            frontier = newFrontier;
        }
        return out;
    }

    public int possibleSpacesCount() {
        int out = 0;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 12; c++) {
                if (!isBad(r, c)) out++;
            }
        }
        return out;
    }

    public boolean isReady() {
        return parent.mode == 2;
    }

    public int[] getRowCol(int x, int y) {
        int c = (int) (x / (parent.width / 12.f));
        float h = parent.height / 9.5f;
        if (c % 2 == 1) y -= 0.5 * h;
        int r = (int) (y / h);
        if (y < 0) r = -1;
        return new int[]{r, c};
    }

    public char terrainAt(int r, int c) {
        int boardRow = r / 3;
        int boardCol = c / 6;
        if (segments[boardRow][boardCol] != null)
            return segments[boardRow][boardCol].terrainAt(r % 3, c % 6);
        else return ' ';
    }

    public char animalAt(int r, int c) {
        int boardRow = r / 3;
        int boardCol = c / 6;
        if (segments[boardRow][boardCol] != null)
            return segments[boardRow][boardCol].animalAt(r % 3, c % 6);
        else return ' ';
    }

    public PlayerToken tokenAt(int r, int c, int player) {
        return tokens.get(player)[r][c];
    }

    public PlayerToken inferenceAt(int r, int c, int player) {
        return inferences.get(player)[r][c];
    }

    public void changeToken(int r, int c, int player) {
        if (r >= 0 && r < 9 && c >= 0 && c < 12) {
            PlayerToken[][] grid = tokens.get(player);
            grid[r][c] = PlayerToken.values()[(grid[r][c].ordinal() + 1) % 3];
        }
    }

    public int nBuildings() {
        return buildingLocs.size();
    }

    public void addBuilding(int[] loc) {
        buildingLocs.add(loc);
    }

    public void display() {
        float cw = parent.width / 12.f;
        float ch = parent.height / 9.5f;
        // Draw the terrain and tokens
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 12; c++) {
                // Draw the terrain
                parent.stroke(0xff000000);
                switch (terrainAt(r, c)) {
                    case 'w':
                        parent.fill(0xff3366ff);
                        break;
                    case 'm':
                        parent.fill(0xff939393);
                        break;
                    case 'f':
                        parent.fill(0xff006600);
                        break;
                    case 's':
                        parent.fill(0xff480091);
                        break;
                    case 'd':
                        parent.fill(0xffffcc00);
                        break;
                    default:
                        parent.fill(0);
                }
                float yOffset = c % 2 == 0 ? 0 : 0.5f * ch;
                float x = c * cw;
                float y = r * ch + yOffset;
                parent.rect(x, y, cw, ch);
                // Draw the animals
                char animal = animalAt(r, c);
                if (animal == ' ') continue;
                else if (animal == 'c') parent.stroke(0xffff0000);
                parent.noFill();
                parent.rect(x + 0.1f * cw, y + 0.1f * cw, 0.8f * cw, 0.8f * ch);
                parent.stroke(0);
            }
        }
        // Draw X on the impossible spaces
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 12; c++) {
                float yOffset = c % 2 == 0 ? 0 : 0.5f * ch;
                float x = c * cw;
                float y = r * ch + yOffset;
                if (isBad(r, c)) parent.drawX(x, y, cw, ch);
            }
        }
        // Draw the buildings
        float radius = 0.2f * Math.min(cw, ch);
        for (int i = 0; i < buildingLocs.size(); i++) {
            int[] coord = buildingLocs.get(i);
            float x = coord[1] * cw + 0.7f * cw;
            float y = coord[0] * ch + 0.7f * ch;
            if (coord[1] % 2 == 1) y += 0.5 * ch;
            switch (i) {
                case 0:
                    parent.fill(0xff1e50e6);
                    parent.polygon(x, y, radius, 8);
                    break;
                case 1:
                    parent.fill(0xff1e50e6);
                    parent.polygon(x, y, radius, 3);
                    break;
                case 2:
                    parent.fill(0xffffffff);
                    parent.polygon(x, y, radius, 8);
                    break;
                case 3:
                    parent.fill(0xffffffff);
                    parent.polygon(x, y, radius, 3);
                    break;
                case 4:
                    parent.fill(0xff009933);
                    parent.polygon(x, y, radius, 8);
                    break;
                case 5:
                    parent.fill(0xff009933);
                    parent.polygon(x, y, radius, 3);
                    break;
                case 6:
                    parent.fill(0xff000000);
                    parent.polygon(x, y, radius, 8);
                    break;
                case 7:
                    parent.fill(0xff000000);
                    parent.polygon(x, y, radius, 3);
                    break;
            }
        }
        // Draw the tokens
        parent.pushStyle();
        parent.rectMode(parent.CENTER);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 12; c++) {
                float yOffset = c % 2 == 0 ? 0 : 0.5f * ch;
                float x = c * cw;
                float y = r * ch + yOffset;
                for (int p = 0; p < 5; p++) {
                    PlayerToken t = tokenAt(r, c, p);
                    int opacity = 255;
                    if (t == PlayerToken.NONE) {
                        t = inferenceAt(r, c, p);
                        opacity = 50;
                    }
                    parent.fill(CryptidSolver.playerColors[p], opacity);
                    parent.stroke(0, opacity);
                    switch (t) {
                        case CUBE:
                            parent.rect(x + (p + 1) * cw / 6, y + ch / 3, cw / 6, ch / 6);
                            break;
                        case DISC:
                            parent.ellipse(x + (p + 1) * cw / 6, y + ch / 3, cw / 6, ch / 6);
                            break;
                    }
                }
            }
        }
        parent.popStyle();
    }

    public boolean isBad(int r, int c) {
        for (int i = 0; i < 5; i++) {
            if (tokenAt(r, c, i) == PlayerToken.CUBE || inferenceAt(r, c, i) == PlayerToken.CUBE) {
                return true;
            }
        }
        return false;
    }

    public List<Clue> getAllClues() {
        List<Clue> clues = new ArrayList<>();
        clues.add(new Clue(0, new String[]{"f", "d"}));
        clues.add(new Clue(0, new String[]{"f", "w"}));
        clues.add(new Clue(0, new String[]{"f", "s"}));
        clues.add(new Clue(0, new String[]{"f", "m"}));
        clues.add(new Clue(0, new String[]{"d", "w"}));
        clues.add(new Clue(0, new String[]{"d", "s"}));
        clues.add(new Clue(0, new String[]{"d", "m"}));
        clues.add(new Clue(0, new String[]{"w", "s"}));
        clues.add(new Clue(0, new String[]{"w", "m"}));
        clues.add(new Clue(0, new String[]{"s", "m"}));
        clues.add(new Clue(1, "f"));
        clues.add(new Clue(1, "d"));
        clues.add(new Clue(1, "s"));
        clues.add(new Clue(1, "m"));
        clues.add(new Clue(1, "w"));
        clues.add(new Clue(1, new String[]{"c", "b"}));
        clues.add(new Clue(2, new String[]{"b-s", "w-s", "g-s", "bl-s"}));
        clues.add(new Clue(2, new String[]{"b-a", "w-a", "g-a", "bl-a"}));
        clues.add(new Clue(2, "c"));
        clues.add(new Clue(2, "b"));
        clues.add(new Clue(3, new String[]{"b-s", "b-a"}));
        clues.add(new Clue(3, new String[]{"w-s", "w-a"}));
        clues.add(new Clue(3, new String[]{"g-s", "g-a"}));
        if (nBuildings() == 8) {
            clues.add(new Clue(3, new String[]{"bl-s", "bl-a"}));
            clues.add(new Clue(0, new String[]{"f", "d"}, true));
            clues.add(new Clue(0, new String[]{"f", "w"}, true));
            clues.add(new Clue(0, new String[]{"f", "s"}, true));
            clues.add(new Clue(0, new String[]{"f", "m"}, true));
            clues.add(new Clue(0, new String[]{"d", "w"}, true));
            clues.add(new Clue(0, new String[]{"d", "s"}, true));
            clues.add(new Clue(0, new String[]{"d", "m"}, true));
            clues.add(new Clue(0, new String[]{"w", "s"}, true));
            clues.add(new Clue(0, new String[]{"w", "m"}, true));
            clues.add(new Clue(0, new String[]{"s", "m"}, true));
            clues.add(new Clue(1, "f", true));
            clues.add(new Clue(1, "d", true));
            clues.add(new Clue(1, "s", true));
            clues.add(new Clue(1, "m", true));
            clues.add(new Clue(1, "w", true));
            clues.add(new Clue(1, new String[]{"c", "b"}, true));
            clues.add(new Clue(2, new String[]{"b-s", "w-s", "g-s", "bl-s"}, true));
            clues.add(new Clue(2, new String[]{"b-a", "w-a", "g-a", "bl-a"}, true));
            clues.add(new Clue(2, "c", true));
            clues.add(new Clue(2, "b", true));
            clues.add(new Clue(3, new String[]{"b-s", "b-a"}, true));
            clues.add(new Clue(3, new String[]{"w-s", "w-a"}, true));
            clues.add(new Clue(3, new String[]{"g-s", "g-a"}, true));
            clues.add(new Clue(3, new String[]{"bl-s", "bl-a"}, true));
        }
        return clues;
    }

    public boolean clueInference(List<Clue> clues) {
        List<Clue> deadClues = new ArrayList<>();
        for (Clue clue : clues) {
            boolean dead = true;
            boardLoop: for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 12; c++) {
                    if (clue.clueValid(this, r, c)){
                        dead = false;
                        break boardLoop;
                    }
                }
            }
            if (dead) deadClues.add(clue);
        }
        if (deadClues.size() > 0) {
            clues.removeAll(deadClues);
            return true;
        }
        return false;
    }

    public List<Clue> runInference(int player) {
        // Filter out the impossible clues
        List<Clue> clues = getAllClues();
        List<Clue> deadClues = new ArrayList<>();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 12; c++) {
                for (Clue clue : clues) {
                    boolean eval = clue.evaluate(this, r, c);
                    if (tokenAt(r, c, player) == PlayerToken.CUBE && eval || tokenAt(r, c, player) == PlayerToken.DISC && !eval) {
                        deadClues.add(clue);
                    }
                }
            }
        }
        clues.removeAll(deadClues);
        // Mark the map with any new information we've gained
        boolean dirty = true;
        while (dirty) {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 12; c++) {
                    boolean totalYes = true;
                    boolean totalNo = true;
                    for (Clue clue : clues) {
                        if (clue.evaluate(this, r, c)) totalNo = false;
                        else totalYes = false;
                    }
                    if (totalYes) inferences.get(player)[r][c] = PlayerToken.DISC;
                    else if (totalNo) inferences.get(player)[r][c] = PlayerToken.CUBE;
                    else inferences.get(player)[r][c] = PlayerToken.NONE;
                }
            }
            dirty = clueInference(clues);
            System.out.println("DIRTY: " + dirty);
        }
        return clues;
    }

    public List<List<Clue>> inferAll() {
        List<List<Clue>> clues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            clues.add(runInference(i));
        }
        return clues;
    }
}


