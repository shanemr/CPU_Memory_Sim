import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Memory
{
    private static int[] memory = new int[2000];
    private static ArrayList<String> result = new ArrayList<>();
    public static void main(String args[]) {


        // Formatting input program
            try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
                while (br.ready()) {
                    String line = br.readLine().replaceAll(" .+$", "");
                    // Gets rid of any initial blank lines
                    if(!line.equals("")){
                        result.add(line);
                    }

                }

            }
            catch (Exception e){

        }


        // Initializing values 0 - 1999
        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0;
        }



        // Holds current memory location to load program data into.
        int memoryLocation = 0;
        // Loading program instructions into main memory
        for (int i = 0; i < result.size(); i++) {
            // Check for system memory location
            String check = result.get(i).substring(0,1);
            if(check.equals(".")){
                memoryLocation = Integer.parseInt(result.get(i).substring(1));
                memory[memoryLocation] = memoryLocation;
                continue;
            }
            memory[memoryLocation] = Integer.parseInt(result.get(i));
            memoryLocation++;

        }



        // Scanner to got information from CPU
        Scanner sc = new Scanner(System.in);

        String instructionType = null;
        // Gets instruction type from CPU
        while (sc.hasNext()) {
            instructionType = sc.nextLine();

            String[] command = instructionType.split(" ");

            if (command[0].equals("read")) {
                read(Integer.parseInt(command[1]));
            } else if (command[0].equals("write")) {
                write(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
            } else if(command[0].equals("done")){
                // End execution
                break;
            }
        }
    }
   // Read from memory
    public static void read(int cell){
        System.out.println(memory[cell]);
    }
    // Write to memory
    public static void write(int address, int data){
        memory[address] = data;
    }









}