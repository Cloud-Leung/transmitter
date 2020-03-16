package cn.transmitter.annotation;

import cn.transmitter.autoconfig.TransmitterAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Transmitter 自动启动类
 *
 * @author cloud
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({TransmitterAutoConfiguration.class})
public @interface EnableTransmitter {

}
