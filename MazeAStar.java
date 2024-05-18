import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;


public class MazeAStar {
    static class Node {
        int x, y;
        Node parent;
        int cost;
        int heuristic;
        int totalCost;

        Node(int x, int y, Node parent, int cost, int heuristic) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.cost = cost;
            this.heuristic = heuristic;
            this.totalCost = cost + heuristic;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        public static Comparator<Node> byTotalCost = Comparator.comparingInt(n -> n.totalCost);
    }

    private static int heuristic(int x, int y, int goalX, int goalY) {
        return Math.abs(x - goalX) + Math.abs(y - goalY);
    }

    static void printMaze(int[][] maze, Node current, List<Node> path) {
        final String RED = "\u001B[31m";
        final String WHITE = "\u001B[37m";
        final String BLACK = "\u001B[30m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        final String CYAN = "\u001B[36m";
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";


        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                boolean isPathNode = false;
                if (path != null) {
                    for (Node node : path) {
                        if (node.x == x && node.y == y) {
                            isPathNode = true;
                            break;
                        }
                    }
                }
                if (current != null && current.x == x && current.y == y) {
                    System.out.print(RED + "A " + RESET);
                } else if (isPathNode) {
                    if (x == path.get(0).x && y == path.get(0).y) {
                        System.out.print(GREEN + "S " + RESET);
                    } else if (maze[y][x] == 9) {
                        System.out.print(GREEN + "G " + RESET);
                    } else {
                        System.out.print(YELLOW + "P " + RESET);
                    }
                } else if (maze[y][x] == 1) {
                    System.out.print(CYAN + "# " + RESET);
                } else {
                    System.out.print(WHITE + ". " + RESET);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    static boolean searchPath(int[][] maze, int startX, int startY, int goalX, int goalY, List<Node> path) {
        int rows = maze.length;
        int cols = maze[0].length;
        PriorityQueue<Node> openSet = new PriorityQueue<>(Node.byTotalCost);
        boolean[][] visited = new boolean[rows][cols];
        Node startNode = new Node(startX, startY, null, 0, heuristic(startX, startY, goalX, goalY));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (visited[current.y][current.x]) {
                continue;
            }

            visited[current.y][current.x] = true;
            printMaze(maze, current, null);

            if (current.x == goalX && current.y == goalY) {
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);
                printMaze(maze, null, path);
                return true;
            }

            int[][] moves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
            for (int[] move : moves) {
                int nx = current.x + move[0], ny = current.y + move[1];
                if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && maze[ny][nx] != 1 && !visited[ny][nx]) {
                    int newCost = current.cost + 1;
                    Node neighbor = new Node(nx, ny, current, newCost, heuristic(nx, ny, goalX, goalY));
                    openSet.add(neighbor);
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {

        String filePath = "maze3_72x39.txt"; // Path to the .txt file containing the maze layout
        int[][] maze = loadMaze(filePath);

        int startX = 0; // Starting x-coordinate
        int startY = 0; // Starting y-coordinate
        int goalX = 72; // Goal x-coordinate
        int goalY = 39;// Goal y-coordinate

        List<Node> path = new ArrayList<>();
        boolean found = searchPath(maze, startX, startY, goalX, goalY, path);

        if (!found) {
            System.out.println("No path found.");
        }
    }

    private static int[][] loadMaze(String filePath) {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] values = line.split("\\s+");
                int[] row = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    try {
                        if (!values[i].isEmpty()) {
                            row[i] = Integer.parseInt(values[i]);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing int at line " + lineNum + ", index " + i + ": '" + values[i] + "'");
                        throw e;
                    }
                }
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows.toArray(new int[0][]); 
    }

}

