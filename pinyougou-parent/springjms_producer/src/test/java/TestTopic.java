import cn.itcast.demo.TopicPRoducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-jms-producer.xml")
public class TestTopic {

    @Autowired
    private TopicPRoducer topicPRoducer;
    @Test
    public void sendTextQueue(){
        topicPRoducer.sendTextMessage("爱我中华");
    }
}
