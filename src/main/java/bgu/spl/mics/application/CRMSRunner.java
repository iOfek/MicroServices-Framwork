package bgu.spl.mics.application;
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
        Data data = new Data(Data.Type.Text, 0, 1000);
        DataBatch dataBatch = new DataBatch(data, 0);
        Model model = new Model("test", data, Model.Status.PreTrained, Model.Result.None);
        Model[] m = {model};
        Student student = new Student("name", "CS", Student.Degree.MSc, 0, 0,m);

        GPU gpu = new GPU(GPU.Type.RTX3090, null);
        Cluster cluster = Cluster.getInstance();
        StudentService studentService = new StudentService("studen", student);
        GPUService gpuService = new GPUService("GPU Service", gpu);
        MessageBusImpl msb = MessageBusImpl.getInstance();

        msb.register(gpuService);
        msb.register(studentService);
        Thread t1 = new Thread(()->{
            gpuService.run();
        });
        t1.start();
        
         try {
            Thread.sleep(1000);
        } catch (Exception e) { }
        
        TrainModelEvent t = new TrainModelEvent(model);
        msb.sendEvent(t);
       
        System.out.println("Sent Model: "+t.getModel().getName()); 

        /* msb.subscribeEvent(TrainModelEvent.class, gpuService);
        TrainModelEvent t = new TrainModelEvent(model);
        msb.sendEvent(t);
        Message message = null;
        try {
            message =msb.awaitMessage(gpuService);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace(); */
        
        
        //System.out.println(((TrainModelEvent)message).getModel().getName());
        
        //System.out.println(gpu.getModel().getName());
        //ExecutorService ea = Executors.newFixedThreadPool(3);
       /*  e.execute(mBusImpl);
        e.execute(gpuService);
        e.execute(studentService); */
        
        //gpuService.run();
  /*       Thread t1 = new Thread(()->{
            studentService.run();
            try {
                Thread.sleep(1000);
            } catch (Exception e) { }
        });
        t1.start(); */
    }
}
