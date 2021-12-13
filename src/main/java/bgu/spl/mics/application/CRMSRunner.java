package bgu.spl.mics.application;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jws.WebParam.Mode;

import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;
import com.google.gson.stream.JsonReader;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        Student[] students = null;
        GPU[] gpus = null;
        LinkedList<CPU> cpus = new LinkedList<>();
        ConfrenceInformation[] conferences = null;
        int tickTime=0;
        int duration=0;

        String[] tempGPUs=null;
        int[] tempCPUs=null;

        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("example_input.json"));
            JsonReader jReader = new JsonReader(reader);

            jReader.beginObject();
            while (jReader.hasNext()){
                String name = jReader.nextName();
               if (name.equals("Students")) {
                   students = gson.fromJson(jReader,Student[].class);
               }
                if (name.equals("GPUS")) {
                    tempGPUs = gson.fromJson(jReader,String[].class);
                    gpus= new GPU[tempGPUs.length];
                    for(int i=0;i<tempGPUs.length;i++){
                        gpus[i]=new GPU(GPU.Type.valueOf(tempGPUs[i]));
                    }
                }
                if (name.equals("CPUS")) {
                    tempCPUs = gson.fromJson(jReader,int[].class);
                    for(int i=0;i<tempCPUs.length;i++){
                        cpus.add(new CPU(tempCPUs[i]));
                    }
                }
                if (name.equals("Conferences")) {
                    conferences = gson.fromJson(jReader,ConfrenceInformation[].class);
                }
                if (name.equals("TickTime")) {
                    tickTime = jReader.nextInt();
                }
                if (name.equals("Duration")) {
                    duration = jReader.nextInt();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        ExecutorService e = Executors.newFixedThreadPool(3);
        for(int i=0;i<gpus.length;i++) {
            e.submit(new GPUService("GPU Service "+ i,gpus[i]));
        }
        for(int i=0;i<cpus.size();i++) {
            e.submit(new CPUService("CPU Service "+ i));
        }
        for(int i=0;i<conferences.length;i++) {
            e.submit(new ConferenceService("Conference Service "+i,conferences[i]));
        }
        for(int i=0;i<students.length;i++) {
            e.submit(new StudentService("Student Service "+i,students[i]));
        }
        e.submit(new TimeService(tickTime,duration));


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
