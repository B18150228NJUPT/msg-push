package ycc;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;

@SpringBootApplication(exclude = {
        TransactionAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class
})
public class ApolloMain implements CommandLineRunner {

    @Value("${timeout:100}")
    private int timeout;

    public static void main(String[] args) {
        SpringApplication.run(ApolloMain.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("timeout: " + timeout);
    }

    @ApolloConfigChangeListener("TEST1.test.yml")
    public void onChange(ConfigChangeEvent changeEvent) {
        System.out.println("apollo change : " + changeEvent.toString());
    }
}