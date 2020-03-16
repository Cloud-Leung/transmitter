package cn.transmitter;

import cn.transmitter.autoconfig.ClassConstrutorReplaceListener;
import org.springframework.boot.SpringApplication;

/**
 * Transmitter 启动器
 *
 * @author cloud
 */
public class TransmitterApplication {

    public static SpringApplication run(Class<?> clazz) {
        SpringApplication springApplication = new SpringApplication(clazz);
        springApplication.addListeners(new ClassConstrutorReplaceListener());
        return springApplication;
    }

}
