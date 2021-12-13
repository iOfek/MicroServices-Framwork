package bgu.spl.mics.application;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jws.WebParam.Mode;

import com.google.gson.Gson;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.application.services.TimeService;
import com.google.gson.stream.JsonReader;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        Student[] students = null;
        String[] gpus = null;
        String[] cpus = null;
        ConfrenceInformation[] conferences = null;
        int tickTime;
        int duration;

        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("example_input.json"));
            JsonReader jReader = new JsonReader(reader);

            jReader.beginObject();
            while (jReader.hasNext()){
                String name = jReader.nextName();
                System.out.println(name);
               if (name.equals("Students")) {
                    students = gson.fromJson(jReader,Student[].class);
                   System.out.println("YES - Students.... length is-" +students.length);
               }
                if (name.equals("GPUS")) {
                    System.out.println("entered gpus------");
                    gpus = gson.fromJson(jReader,String[].class);
                    System.out.println("YES - GPUs.... length is-" +gpus.length );
                }
                if (name.equals("CPUS")) {
                    cpus = gson.fromJson(jReader,String[].class);
                    System.out.println("YES - CPUs.... length is-" +cpus.length );
                }
                if (name.equals("Conferences")) {
                    conferences = gson.fromJson(jReader,ConfrenceInformation[].class);
                    System.out.println("YES - conferences.... length is-" +conferences.length );
                }
                if (name.equals("TickTime")) {
                    tickTime = jReader.nextInt();
                    System.out.println("YES - tickTime... is-" +tickTime );
                }
                if (name.equals("Duration")) {
                    duration = jReader.nextInt();
                    System.out.println("YES - duration... is-" +duration );
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


//        Student[]students;
//        GPU[]gpus;
//        CPU[]cpus;
//        ConfrenceInformation[]confrences;
//        int tickTime;
//        int duration;
//        Data data = new Data(Data.Type.Text, 0, 1000000);
//        DataBatch dataBatch = new DataBatch(data, 0);
//        Model model = new Model("testinggg", data, Model.Status.PreTrained, Model.Result.None);
//        Model[] m = {model};
//        Student student = new Student("name", "CS", Student.Degree.MSc, 0, 0,m);
//
//        GPU gpu = new GPU(GPU.Type.RTX3090, null);
//        Cluster cluster = Cluster.getInstance();
//        TimeService timeService = TimeService.getInstance();
//        StudentService studentService = new StudentService("studen", student);
//        GPUService gpuService = new GPUService("GPU Service", gpu);
//
//        ExecutorService e = Executors.newFixedThreadPool(3);
//        e.submit(gpuService);
//        try {
//            Thread.sleep(1000);
//        } catch (Exception ex) {
//            //TODO: handle exception
//        }
//        e.submit(timeService);
//        e.submit(studentService);
        
        

/* 
        Thread t1 = new Thread(()->{
            gpuService.run();
        });
        t1.start();
        

        Thread t2 = new Thread(()->{
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            timeService.run();
        });
        t2.start();
        


        Thread t3 = new Thread(()->{
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            studentService.run();
        });
        t3.start(); */
         
        

    }
}
