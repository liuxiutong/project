import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-jms-consumer-topic.xml")
public class TextTopic {

    @Test
    public void testTopic(){
        try {
            System.in.read();
        } catch (IOException e) {


        }

    }
}
