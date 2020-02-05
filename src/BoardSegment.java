public class BoardSegment {

    private char[][] terrain, animal;
    private boolean isRotated;

    public BoardSegment(String terrain, String animal, boolean isRotated) {
        this.terrain = new char[3][6];
        this.animal = new char[3][6];
        this.isRotated = isRotated;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 6; c++) {
                this.terrain[r][c] = terrain.charAt(6 * r + c);
                this.animal[r][c] = animal.charAt(6 * r + c);
            }
        }
    }

    public char terrainAt(int r, int c) {
        return isRotated ? terrain[2 - r][5 - c] : terrain[r][c];
    }

    public char animalAt(int r, int c) {
        return isRotated ? animal[2 - r][5 - c] : animal[r][c];
    }

}
