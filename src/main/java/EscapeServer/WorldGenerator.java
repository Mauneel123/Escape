package EscapeServer;

import java.io.IOException;
import java.util.Scanner;

public class WorldGenerator {
    static World world;
    static Integer activatedARef;

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        //create the world
        System.out.println("===================================================");
        System.out.println("How many levels do you want to create? Please input an integer:");
        Integer level = sc.nextInt();
        System.out.println("Generation process started...");
        System.out.println("===================================================");
        world = new World(0L, level);
        System.out.println("Generation process finished.");
    }
}