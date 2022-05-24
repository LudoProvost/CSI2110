import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

class Main {

    public static int[][] mapVisited;
    public static int[][] mapDistance;
    public static char[][] mapChar;
    private static int row, col;
    public static ArrayList<int[]> biggestContinentRegion;
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
        boolean isPartB = false;
        try {
            isPartB = args[0].equals("PartB");
        } catch (Exception ignored) {}

        while (true) {
            try {
                String[] firstLine = readLine().split(" ");
                row = Integer.parseInt(firstLine[0]);
                col = Integer.parseInt(firstLine[1]);

                mapVisited = new int[row][col];
                mapDistance = new int[row][col];
                mapChar = new char[row][col];
            } catch (Exception e) {
                break;
            }

            biggestContinentRegion = new ArrayList<>();

            if (!biggestContinentRegion.isEmpty()) {
                biggestContinentRegion.clear();
            }

            // initialize visited array, all pos to 0 (not visited)
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    mapVisited[i][j] = 0;
                    mapDistance[i][j] = -1;
                }
            }

            // create map array, 1 for land and 0 for water
            for (int i = 0; i < row; i++) {
                String ithRow = readLine();
                for (int j = 0; j < col; j++) {

                    assert ithRow != null;
                    mapChar[i][j] = ithRow.charAt(j);
                }
            }

            String[] location = Objects.requireNonNull(readLine()).split(" ");
            int mijidLocationX = Integer.parseInt(location[1]);
            int mijidLocationY = Integer.parseInt(location[0]);

           landSymbol = mapChar[mijidLocationY][mijidLocationX];

            // set visited mijid location to 2
            mapVisited[mijidLocationY][mijidLocationX] = 2;
            int biggest = 0, longestDistance = 0, tempDistance = 0;


            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if (mapChar[i][j] == landSymbol && mapVisited[i][j] != 1) {
                        int total = findConnectedLand(i, j);
                        if (total >= biggest) {
                            if (isPartB) {
                                tempDistance = findNorthwest();
                                if (total == biggest && tempDistance > longestDistance) {
                                    longestDistance = tempDistance;
                                } else if (total > biggest) {
                                    longestDistance = tempDistance;
                                }
                            }
                            biggest = total;
                        }
                        biggestContinentRegion.clear();
                    }
                }
            }

            if (isPartB) {
                if (biggest == 0) {
                    longestDistance = -1;
                }
                System.out.println(biggest + " " + longestDistance);
            } else {
                System.out.println(biggest);
            }
            readLine();
        }
    }

    public static int findConnectedLand(int y, int x) {

        // edge case: on Mijid's current continent
        if (mapVisited[y][x] == 2) {
            biggestContinentRegion.clear();
            return -400;
        }

        int total = 1;
        mapVisited[y][x] = 1;

        // recurse top
        if (y != 0 && mapVisited[y-1][x] != 1 && mapChar[y-1][x] == landSymbol) {
            total += findConnectedLand(y-1, x);
        }

        // recurse bottom
        if (y != row-1 && mapVisited[y+1][x] != 1 && mapChar[y+1][x] == landSymbol) {
            total += findConnectedLand(y+1, x);
        }

        // recurse right
        if (x != col-1) {
            if (mapVisited[y][x+1] != 1 && mapChar[y][x+1] == landSymbol) {
                total += findConnectedLand(y, x+1);
            }
        } else {
            if (mapVisited[y][0] != 1 && mapChar[y][0] == landSymbol) {
                total += findConnectedLand(y, 0);
            }
        }

        // recurse left
        if (x != 0) {
            if (mapVisited[y][x-1] != 1 && mapChar[y][x-1] == landSymbol) {
                total += findConnectedLand(y, x-1);
            }
        } else {
            if (mapVisited[y][col-1] != 1 && mapChar[y][col-1] == landSymbol) {
                total += findConnectedLand(y, col-1);
            }
        }

        biggestContinentRegion.add(new int[]{x, y});
        return total;
    }

    public static int findNorthwest() {

        int northwestX = col,northwestY = row, x, y, longestDistance = 0;

        // find most northwest point
        for (int[] value : biggestContinentRegion) {
            x = value[0];
            y = value[1];

            if (y < northwestY) {
                northwestX = x;
                northwestY = y;
            } else if (y == northwestY) {
                if (x < northwestX) {
                    northwestX = x;
                    northwestY = y;
                }
            }
        }

        findDistance(northwestY, northwestX, 0);

        for (int[] ints : biggestContinentRegion) {
            x = ints[0];
            y = ints[1];
            if (mapDistance[y][x] > longestDistance) {
                longestDistance = mapDistance[y][x];
            }
        }
        return longestDistance;
    }

    public static void findDistance(int y, int x, int dist) {

        if (mapDistance[y][x] == -1 || dist < mapDistance[y][x]) {
            mapDistance[y][x] = dist;
        }

//        mapDistance[y][x] = dist;

        // recurse top
        if (y != 0  && (mapDistance[y-1][x] == -1 || mapDistance[y-1][x] > dist) && mapChar[y-1][x] == landSymbol) {
            findDistance(y-1, x, dist+1);
        }

        // recurse bottom
        if (y != row-1 && (mapDistance[y+1][x] == -1 || mapDistance[y+1][x] > dist) && mapChar[y+1][x] == landSymbol) {
            findDistance(y+1, x, dist+1);
        }

        // recurse right
        if (x != col-1) {
            if ((mapDistance[y][x+1] == -1 || mapDistance[y][x+1] > dist) && mapChar[y][x+1] == landSymbol) {
                findDistance(y, x+1, dist+1);
            }
        } else {
            if ((mapDistance[y][0] == -1 || mapDistance[y][0] > dist) && mapChar[y][0] == landSymbol) {
                findDistance(y, 0, dist+1);
            }
        }

        // recurse left
        if (x != 0) {
            if ((mapDistance[y][x-1] == -1 || mapDistance[y][x-1] > dist) && mapChar[y][x-1] == landSymbol) {
                findDistance(y, x-1, dist+1);
            }
        } else {
            if ((mapDistance[y][col-1] == -1 || mapDistance[y][col-1] > dist) && mapChar[y][col-1] == landSymbol) {
                findDistance(y, col-1, dist+1);
            }
        }

        return;
    }

}

// print the map in console for debugging
//        for (int i = 0; i < row; i++) {
//            for (int j = 0; j < col;j++) {
//                System.out.print(map[i][j]);
//            }
//            System.out.print("\n");
//        }
