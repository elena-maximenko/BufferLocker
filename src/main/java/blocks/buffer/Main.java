package blocks.buffer;


import java.io.*;

public class Main {
    private static final int SIZE = 100;
    private static final Object LOCK = new Object();
    public static void main(String[] args) {
        ProcessBuilder processBuilder = new ProcessBuilder(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

        try(FileReader fileReader = new FileReader(new File("read.txt"));
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            Process readingProcess = processBuilder.start();

            char[] chars = new char[SIZE]; // fixed sized buffer representation

            int data;
            synchronized (LOCK) { // for testing add one to SIZE
                data = bufferedReader.read(chars, 0, SIZE); // attempt to read more than buffer size
            }
            StringBuilder stringBuilder = new StringBuilder(data);
            stringBuilder.append(chars, 0, data);

            Process writingProcess = processBuilder.start();

            System.out.println(stringBuilder);

            try(FileOutputStream fileOutputStream = new FileOutputStream(new File("write.txt"));
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, SIZE)) {

                // for testing add one to SIZE
                bufferedOutputStream.write("Hello".getBytes(), 0, SIZE+1); // attempt tp write more that we have, autoblocking
                bufferedOutputStream.flush();
            }

            // add block from locker for clear appropriate process block not auto one

            readingProcess.destroy();
            writingProcess.destroy();
        }
        catch (IndexOutOfBoundsException e){
            try { // do this with locker
                synchronized (LOCK){
                    LOCK.wait();//1000); // blocking we were trying to read more than buffer had
                }
            }
            catch (InterruptedException ie){
                ie.fillInStackTrace();
            }
        }
        catch (IOException e){
            e.fillInStackTrace();
        }


       // Process writingProcess = processBuilder.start();


       // writingProcess.destroy();
    }
}
