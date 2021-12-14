package bgu.spl.mics.application.objects;

import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class GPUTest {
    private static Data data;
    private static DataBatch dataBatch;
    private static Model model;
    private static GPU gpu;
    private static Cluster cluster;
    private static Student student;

    

    @Before
    public void setUp() throws Exception{
        data = new Data(Data.Type.Text, 0, 1000);
        dataBatch = new DataBatch(data, 0);
        model = new Model("test", data, Model.Status.PreTrained, Model.Result.None);
        Model[] m = {model};
        student = new Student("name", "CS", Student.Degree.MSc, 0, 0,m);

        gpu = new GPU(GPU.Type.RTX3090);
        gpu.setModel(model);
        cluster = Cluster.getInstance();
    }

    @Test
    public void testTestModelEvent() {
        
        Model.Result prob = gpu.testModelEvent();
        assertTrue((prob == Model.Result.Bad)||(prob == Model.Result.Good));
    }
   

    @Test
    public void testNumOfBatchesToSend() {
        assertTrue(gpu.numOfBatchesToSend()>=0);
    }

    @Test
    public void testDivideDataToDataBatches() {
        DataBatch[] test = gpu.divideDataToDataBatches();
        assertTrue(test.length == data.getSize()/1000);
    }

    @Test
    public void testSendUnproccessedDataBatchToCluster() {
        gpu.sendUnproccessedDataBatchToCluster(dataBatch);
        assertTrue(cluster.getInQueue().contains(dataBatch));
        gpu.setVRAMCapacity(1);
        assertThrows(IllegalStateException.class, () ->gpu.sendUnproccessedDataBatchToCluster(dataBatch));
    }


    @Test
    public void testTrainDataBatch() {
        int time = gpu.getTickTime()+ gpu.getDataBatchTrainingTime();
        int pre_training_proccessed = data.getProcessed();
        gpu.trainDataBatch(dataBatch);
        assertFalse(gpu.getVRAM().contains(dataBatch));
        assertEquals(time,gpu.getTickTime() );
        assertEquals(data.getProcessed(),(pre_training_proccessed+1));
    }

    @Test
    public void testUpdateTick() {
        int time = gpu.getTickTime();
        gpu.advanceTick();
        assertEquals(time + 1, gpu.getTickTime());

    }
}
