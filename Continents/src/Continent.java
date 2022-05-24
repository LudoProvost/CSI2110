import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

class Continent {

    public static int[][] visited;
    public static char[][] mapChar;
    private static int row, col;
    private static char landSymbol;

    private static String readLine() {
        byte[] chars = new byte[2048];
        int car = -1;
        int i = 0;
        try {
            while (i < 2048) {
                car = System.in.read();
                if (car < 0 || car == '\n' || car == '\r') {
                    break;
                }
                chars[i++] += car;
            }
        } catch (IOException e) {
            return null;
        }
        return new String(chars, 0, i);
    }

    public static void main(String[] args) {
        while (true) {
            try {
                String[] firstLine = readLine().split(" ");
                row = Integer.parseInt(firstLine[0]);
                col = Integer.parseInt(firstLine[1]);

                visited = new int[row][col];
                mapChar = new char[row][col];
            } catch (Exception e) {
                break;
            }

            // initialize visited array, all pos to 0 (not visited)
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    visited[i][j] = 0;
                }
            }

            // create map array
            for (int i = 0; i < row; i++) {
                String ithRow = readLine();
                for (int j = 0; j < col; j++) {

                    assert ithRow != null;
                    mapChar[i][j] = ithRow.charAt(j);
                }
            }

            String[] location = readLine().split(" ");
            int mijidLocationX = Integer.parseInt(location[1]);
            int mijidLocationY = Integer.parseInt(location[0]);

            landSymbol = mapChar[mijidLocationY][mijidLocationX];

            // set visited mijid location to 2
            visited[mijidLocationY][mijidLocationX] = 2;
            int biggest = 0;

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if (mapChar[i][j] == landSymbol && visited[i][j] != 1) {
                        int total = findConnectedLand(i, j);
                        if (total >= biggest) {
                            biggest = total;
                        }
                    }
                }
            }

            System.out.println(biggest);
            readLine();
        }
    }

    public static int findConnectedLand(int y, int x) {

        // edge case: on Mijid's current continent
        if (visited[y][x] == 2) {
            return -400;
        }

        int total = 1;
        visited[y][x] = 1;

        // recurse top
        if (y != 0 && visited[y-1][x] != 1 && mapChar[y-1][x] == landSymbol) {
            total += findConnectedLand(y-1, x);
        }

        // recurse bottom
        if (y != row-1 && visited[y+1][x] != 1 && mapChar[y+1][x] == landSymbol) {
            total += findConnectedLand(y+1, x);
        }

        // recurse right
        if (x != col-1) {
            if (visited[y][x+1] != 1 && mapChar[y][x+1] == landSymbol) {
                total += findConnectedLand(y, x+1);
            }
        } else {
            if (visited[y][0] != 1 && mapChar[y][0] == landSymbol) {
                total += findConnectedLand(y, 0);
            }
        }

        // recurse left
        if (x != 0) {
            if (visited[y][x-1] != 1 && mapChar[y][x-1] == landSymbol) {
                total += findConnectedLand(y, x-1);
            }
        } else {
            if (visited[y][col-1] != 1 && mapChar[y][col-1] == landSymbol) {
                total += findConnectedLand(y, col-1);
            }
        }

        return total;
    }
}

// print the map in console for debugging
//        for (int i = 0; i < row; i++) {
//            for (int j = 0; j < col;j++) {
//                System.out.print(map[i][j]);
//            }
//            System.out.print("\n");
//        }
