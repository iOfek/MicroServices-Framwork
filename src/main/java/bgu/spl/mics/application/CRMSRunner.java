package bgu.spl.mics.application;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
/* import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; */

/* import javax.jws.WebParam.Mode;
 */
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.ConfrenceInformation;
/* import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TrainModelEvent; */
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Student;
import com.google.gson.stream.JsonReader;
/* import com.sun.org.apache.xpath.internal.operations.Mod;
 */
/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class CRMSRunner {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(args[0]);

        Student[] students = null;
        GPU[] gpus = null;
        LinkedList<CPU> cpus = new LinkedList<>();
        ConfrenceInformation[] conferences = null;
        Cluster cluster= Cluster.getInstance();
        int tickTime=0;
        int duration=0;

        String[] tempGPUs=null;
        int[] tempCPUs=null;

        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(args[0]));
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

        LinkedList<Thread> gpuThreads = new LinkedList<>();
        LinkedList<Thread> cpuThreads = new LinkedList<>();
        LinkedList<Thread> conferenceThreads = new LinkedList<>();
        LinkedList<Thread> studentThreads = new LinkedList<>();

        TimeService timeService= TimeService.getInstance();
        timeService.setDuration(duration);
        timeService.setTicktime(tickTime);
        for(int i=0;i<gpus.length;i++) {
            GPUService gpuService=new GPUService("GPU Service "+ i,gpus[i]);
            gpuThreads.add(new Thread(()->{gpuService.run();}));
            cluster.addGpu(gpus[i]);
        }

        for(int i=0;i<cpus.size();i++) {
            CPUService cpuService=new CPUService("CPU Service "+ i,cpus.get(i));
            cpuThreads.add(new Thread(()->{cpuService.run();}));
            cluster.addCpu(cpus.get(i));
        }
        

        for(int i=0;i<conferences.length;i++) {
            conferences[i].setModels();
            ConferenceService conferenceService=new ConferenceService("Conference Service "+i,conferences[i]);
            conferenceThreads.add(new Thread(()->{conferenceService.run();}));
        }
        //creating models for students
        for(int i=0;i<students.length;i++) {
            for(int j=0;j<students[i].getModels().length;j++){
                Model m =students[i].getModels()[j];
                students[i].getModels()[j] = new Model(m.getName(),m.getDataType(),m.getDataSize());
                students[i].getModels()[j].setStudent(students[i]);
            }
            StudentService studentService=new StudentService("Student Service "+i,students[i]);
            studentThreads.add(new Thread(()->{studentService.run();}));
        }
        cluster.setCluster();

         for(Thread gpuT:gpuThreads) {
            gpuT.start();
        }
        for(Thread cpuT:cpuThreads) {
            cpuT.start();
        }
         for(Thread conT:conferenceThreads) {
            conT.start();
        }  
        for(Thread cpuT:cpuThreads) {
            while (!(cpuT.getState() == Thread.State.WAITING) ) {
                //System.out.println("waiting for gpu thread to be in wait state");
            }
        }
        for(Thread gpuT:gpuThreads) {
            while (!(gpuT.getState() == Thread.State.WAITING)) {
                //System.out.println("waiting for gpu thread to be in wait state");
            }
        } 
        
        
        for(Thread conT:conferenceThreads) {
            while (!(conT.getState() == Thread.State.WAITING)) {
                //System.out.println("waiting for gpu thread to be in wait state");
            }
        } 
        
        
        


        for(Thread studentT:studentThreads){
            studentT.start();

        } 
        for(Thread studentT:gpuThreads) {
            while (!(studentT.getState() == Thread.State.WAITING)) {
                //System.out.println("waiting for gpu thread to be in wait state");
            }
        } 


        Thread ts = new Thread(()->{timeService.run();});
        //Thread.sleep(8000);
         ts.start();
        /* while (!(ts.getState() == Thread.State.WAITING)) {
            //System.out.println("waiting for gpu thread to be in wait state");
        } */
        //ts.join(); 

         

        for(Thread gpuT:gpuThreads) {
            gpuT.join();
        }
        for(Thread cpuT:cpuThreads) {
            cpuT.join();
        }
        for(Thread studentT:studentThreads){
            studentT.join();
        } 
        
        for(Thread conT:conferenceThreads) {
            conT.join();
        } 

        System.out.println("FINIS");
        try {
            FileWriter myWriter = new FileWriter("output.txt");
            myWriter.write("Students\n");
            for (Student student : students) {
                myWriter.write(student.getName()+"\n");
    
                for (Model model : student.getModels()) {
                    myWriter.write("Model "+model.getName()+" Status: "+model.getStatus() +" Result: "+model.getResult() +" Published " + model.isPublished()+"\n");
                }
                myWriter.write("Papers Read: "+student.getPapersRead()+"\n\n");
            }

            myWriter.write("Confrences\n");
            for (ConfrenceInformation confrenceInformation : conferences) {
                myWriter.write(confrenceInformation.getName()+"\n");
                myWriter.write("Published Models:");
               // myWriter.write(confrenceInformation.getModels().size());
                 for (Model m : confrenceInformation.getModels()) {
                    myWriter.write(" "+m.getName());
                } 
                myWriter.write("\n");
            }
            myWriter.write("\n\n");
            myWriter.write("Cluster Statistics\n"); 
            myWriter.write("GPU time use "+ cluster.getGpuTimeUsed()+"\n");
            myWriter.write("CPU time use " + cluster.getCpuTimeUsed()+"\n");
            myWriter.write("Number Of Databatchs Processed By Cpus " + cluster.getNumberOfDatabatchsProcessedByCpus()+"\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } 
        cluster.printStatistics();
        System.out.println("Trained Models");
		for (String modelName : cluster.getTrainedModelNames()) {
			System.out.print(", "+modelName);
		}
/*         for (Student student : students) {
            System.out.println(student.getName());

            for (Model model : student.getModels()) {
                System.out.println("Model "+model.getName()+" Status: "+model.getStatus() +" Result: "+model.getResult() +" Published " + model.isPublished());
            }
            System.out.println("Papers Read: "+student.getPapersRead());
        } */
                
        

    }
}
