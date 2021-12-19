package bgu.spl.mics.application.objects;

import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class CPUTest {

    private static Data data;
    private static DataBatch dataBatch;
    private static CPU cpu;
    private static Cluster cluster;
    

    @Before
    public void setUp() throws Exception{
        data = new Data(Data.Type.Images, 0, 1000);
        dataBatch = new DataBatch(data, 0);
        cpu = new CPU(32);
        cluster = Cluster.getInstance();
    }




    @Test
    public void testUpdateTickTime() {
        int time = cpu.getTickTime();
        try {
            cpu.advanceTick();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(time + 1, cpu.getTickTime());
    }
    @Test
    public void testCPUProcessingTimeInTicks(){
        assertEquals(4, cpu.CPUProcessingTimeInTicks(dataBatch));
        data = new Data(Data.Type.Text, 0, 1000);
        dataBatch = new DataBatch(data, 0);
        assertEquals(2, cpu.CPUProcessingTimeInTicks(dataBatch));
        data = new Data(Data.Type.Tabular, 0, 1000);
        dataBatch = new DataBatch(data, 0);
        assertEquals(1, cpu.CPUProcessingTimeInTicks(dataBatch));
    }
}
