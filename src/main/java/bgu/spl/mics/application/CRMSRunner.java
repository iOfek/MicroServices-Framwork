package bgu.spl.mics.application;
import java.lang.Thread.State;
import java.util.LinkedList;
import java.util.Vector;
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
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.application.services.TimeService;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Student[]students;
        GPU[]gpus;
        CPU[]cpus;
        ConfrenceInformation[]confrences;
        int tickTime;
        int duration;
        Data data = new Data(Data.Type.Text, 0, 1000000);
        DataBatch dataBatch = new DataBatch(data, 0);
        Model model1 = new Model("model1", data, Model.Status.PreTrained, Model.Result.None);
        Model model2 = new Model("model2", data, Model.Status.PreTrained, Model.Result.None);
        Model model3 = new Model("model3", data, Model.Status.PreTrained, Model.Result.None);
        Model model4 = new Model("model4", data, Model.Status.PreTrained, Model.Result.None);

        Model[] models1 = {model1,model2};
        Student student1 = new Student("name", "CS", Student.Degree.MSc, 0, 0,models1);
        Model []models2 ={model3,model4};
        Student student2 = new Student("name", "CS", Student.Degree.MSc, 0, 0,models2);
        GPU gpu = new GPU(GPU.Type.RTX3090);
        CPU cpu = new CPU(32);
        CPU cpu2 = new CPU(32);
        Cluster cluster = Cluster.getInstance();
        cluster.addCpu(cpu);
        cluster.addGpu(gpu); 
        TimeService timeService = TimeService.getInstance();
        StudentService studentService1 = new StudentService("student 1", student1);
        //StudentService studentService2 = new StudentService("student 2", student2);
        GPUService gpuService = new GPUService("GPU Service", gpu);
        CPUService cpuService = new CPUService("CPU Service",cpu);
        CPUService cpuService2 = new CPUService("CPU Service2",cpu2);

        Thread t1 = new Thread(()->{cpuService.run();});
        Thread t2 = new Thread(()->{gpuService.run();});
        Thread t3 = new Thread(()->{cpuService2.run();});
        Thread t4 = new Thread(()->{cluster.run();});
        Thread t5  = new Thread(()->{timeService.run();});
        Thread t6  = new Thread(()->{studentService1.run();});
        //MessageBusImpl msb = MessageBusImpl.getInstance();
        t1.start();
        t2.start();
        t3.start();
        while(t1.getState()!= State.WAITING || t2 .getState()!= State.WAITING||t3 .getState()!= State.WAITING){
            System.out.print("");
        } 

        /* try {
            Thread.sleep(1000);
            
        } catch (Exception ex) {
            //TODO: handle exception
        }  */
        t4.start();
        t5.start();
        t6.start();
        /* ExecutorService e = Executors.newFixedThreadPool(5);
        e.submit(gpuService);
        e.submit(cpuService);
        e.submit(cluster);
        try {
            Thread.sleep(6000);
            e.submit(timeService);
            
            e.submit(studentService1);
            
        } catch (Exception ex) {
            //TODO: handle exception
        } */
        
       
        //e.submit(studentService2);

        

    }
}
