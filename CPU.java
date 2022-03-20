import java.io.*;
import java.util.Scanner;
import java.lang.Runtime;

public class CPU
{
    // PC initially set to 0
    private static int PC = 0;
    // Start of stack
    private static int SP = 999;
    // Holds instructions
    private static int IR;
    // Holds values to manipulate
    private static int AC;
    private static int X;
    private static int Y;
    // Timer to interrupt after every X instructions
    private static int timer;
    // Counts the number of instructions
    private static int instructionCounter;
    // True while instructions to read
    private static boolean read = true;
    // Flag for kernel mode
    private static boolean kernelMode = false;
    // Input and output streams
    public static InputStream is;
    public static OutputStream os;
    public static PrintWriter pw;
    public static Scanner sc;
    public static void main(String args[]) {

        try
        {
            // Local variable to temporarily hold values
            int x;

            // Getting runtime
            Runtime rt = Runtime.getRuntime();

            // Memory Program and program to run
            Process proc = rt.exec(" java Memory "+ args[0]);

            // Setting streams
            is = proc.getInputStream();
            os = proc.getOutputStream();
            sc = new Scanner(is);
            pw = new PrintWriter(os);
            // Getting timer from command line argument
            timer = Integer.parseInt(args[1]);


            // Fetching instructions
            while (read) {
                // Checks to see if enough instructions have been executed to call for an interrupt
                // !kernelMode keeps interrupts from happening during system calls
                if(timer < instructionCounter && !kernelMode){
                    interrupt(0);
                }
                // Getting instruction from memory
                IR = getIR(PC);
                switch(IR) {
                    case 1: AC = getIR(++PC); // Loading the value of next line into AC
                        PC++;
                        break;
                    case 2: x = getIR(++PC); // Getting the address from next line
                            AC = getIR(x); // Loading the value at address x into the AC
                        PC++;
                        break;
                    case 3: x = getIR(++PC); // Getting the address from next line
                            x = getIR(x); // Getting the value from the address --- 0
                            AC = getIR(x); // Loading the value at address x into the AC -- loading from 0
                        PC++;
                        break;
                    case 4: x = getIR(++PC); // Getting the address from next line
                            AC = getIR(x + X); // Loading the value at address (x + X) into the AC
                        PC++;
                        break;
                    case 5: x = getIR(++PC); // Getting the address from next line
                            AC = getIR(x + Y); // Loading the value at address (x + Y) into the AC
                        PC++;
                        break;
                    case 6: AC = getIR(SP + X + 1); // Loading value from address SP+X into the AC
                        PC++;
                        break;
                    case 7: x = getIR(++PC); // Getting address from next line
                            writeToMem(x, AC); // Writing the value of the AC into the address x
                        PC++;
                        break;

                    case 8: AC = (int)(Math.random() * 100) + 1; // Putting random num into AC
                        PC++;
                        break;
                    case 9: x = getIR(++PC);  // Getting print type instruction from next line
                        if(x == 1){
                            System.out.print(AC);
                        } else{
                            System.out.print((char)AC);
                        }
                        PC++;
                        break;
                    case 10: AC += X; // Add X to AC
                        PC++;
                        break;
                    case 11: AC += Y;  // Add Y to AC
                        PC++;
                        break;
                    case 12: AC -= X; // Subtract value in X from AC
                        PC++;
                        break;
                    case 13: AC -= Y; // Subtract value in Y from AC
                        PC++;
                        break;
                    case 14: X = AC; // Copy value of AC into X
                        PC++;
                        break;
                    case 15: AC = X; // Copy value of X into AC
                        PC++;
                        break;
                    case 16: Y = AC; // Copy value of AC into Y
                        PC++;
                        break;
                    case 17: AC = Y; // Copy value of Y into AC
                        PC++;
                        break;
                    case 18: SP = AC; // Copy value of AC into SP;
                        PC++;
                        break;
                    case 19: AC = SP; // Copy value of SP into AC
                        PC++;
                        break;
                    case 20: x = getIR(++PC); // Getting address from next line
                            PC = x; // Setting program counter to that address
                            break;
                    case 21: if(AC == 0){            // If the value of AC is zero
                                x = getIR(++PC);     // Set PC to address and break;
                                PC = x;              // Else increment PC by two and break
                                break;
                            }
                            PC = PC + 2;
                           break;
                    case 22: if(AC != 0){           // If the value of AC is not zero
                               x = getIR(++PC);     // Set PC to address and break;
                               PC = x;              // Else increment PC by two and break
                               break;
                             }
                           PC = PC + 2;
                        break;
                    case 23:
                             x = PC + 2;                // Get the return address
                             push(x);                   // Push the return address onto the stack
                             PC = getIR(PC + 1); // Set PC to jump address
                        break;
                    case 24:
                            PC = pop();
                        break;
                    case 25: X++; // Increment X
                      PC++;
                      break;
                    case 26: X--; // Decrement X
                      PC++;
                      break;
                    case 27:
                          push(AC);   // Push AC onto stack
                            PC++;
                        break;
                    case 28: AC = pop();  // Pop AC from stack
                            PC++;
                        break;
                    case 29:
                           PC++;        // System call
                           interrupt(1);
                        break;
                    case 30:
                        PC = pop();              // getting PC from the stack
                        SP = pop();              // Getting the SP from the stack
                        // Get previous instruction
                        x = pop();
                        // Only reset timer if not a system call
                        if(x != 29){
                            instructionCounter = 0;
                        }
                        kernelMode = false;     // Exit kernel mode
                        break;
                    case 50: end();
                        break;
                        default:
                            break;

                }

                        // increments counter for both user mode and kernel mode
                        instructionCounter++;

            }

            proc.waitFor();

            int exitVal = proc.exitValue();

            System.out.println("Process exited: " + exitVal);

        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }



    }

    // Gets and returns instruction
    public static int getIR(int address) throws IOException {
        // Prevent user program from entering system memory
        if(address > 999 && !kernelMode){
            System.out.println("Memory violation: accessing system address 1000 in user mode ");
            System.exit(0);
        }
        pw.println("read " + address);
        pw.flush();
        String line = sc.nextLine();
        return Integer.parseInt(line);

    }

    // Writes to memory
    public static void writeToMem(int address, int data) throws IOException {
        // Prevent user program from entering system memory
        if(address > 999 && !kernelMode){
            System.out.println("ERROR: Attempt to read from system memory");
            System.exit(0);
        }
        pw.println("write " + address + " " + data);
        pw.flush();
    }

    // Method to end memory read
    public static void end() throws IOException {
        // Send end read instruction to memory
        pw.println("done");
        pw.flush();
        // Stop reading from memory
        read = false;
    }

    public static void push(int returnData) throws IOException {
           // Write the return address to the current available spot of the stack pointer
            writeToMem(SP, returnData);
            // Decrement the stack point value
            SP -= 1;
    }

    public static int pop() throws IOException {
        // increment Stack to get back data at pointer location
        SP += 1;
        // Get the return address from the stack
        int address = getIR(SP);

        return address;
    }


    public static void interrupt(int inter) throws IOException {

        // CPU enters kernel mode
        kernelMode = true;
        // Push type interrupt call to stack
        push(IR);
        // Holds current SP location
        int currentSP = SP;
        // Stack pointer is switched to system stack
        SP = 1999;
        // Pushing SP onto the system stack
        push(currentSP);
        // Pushing PC onto the system stack
        push(PC);

        // Go to address 1000 is timer
        // Fo to address 1500 if system call
        // Throw error for wrong interrupt type
        if(inter == 0){
            PC = 1000;
        } else if(inter == 1){
            PC = 1500;
        } else{
            System.out.println("ERROR: invalid interrupt type");
            System.exit(0);
        }



    }


}
