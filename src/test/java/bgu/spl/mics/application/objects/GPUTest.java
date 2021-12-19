package bgu.spl.mics.application.objects;

import org.junit.Before;
import static org.junit.Assert.assertEquals;
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
        model = new Model("test", Data.Type.Images, 10000);
        Model[] m = {model};
        student = new Student("name", "Phd", Student.Degree.MSc, 0, 0,m);
        model.setStudent(student);
        gpu = new GPU(GPU.Type.RTX3090);
        gpu.setModel(model);
        cluster = Cluster.getInstance();
    }

    @Test
    public void testTestModelEvent() {
        gpu.testModelEvent(model);
        Model.Result prob =model.getResult();
        assertTrue((prob == Model.Result.Bad)||(prob == Model.Result.Good));

    }
   

    @Test
    public void testNumOfBatchesToSend() {
        assertTrue(gpu.numOfBatchesToSend()>=0);
    }


    @Test
    public void testAdvanceTick() {
        int time = gpu.getTickTime();
        gpu.setModel(null);
        gpu.advanceTick();
        assertEquals(time + 1, gpu.getTickTime());

    }
}
