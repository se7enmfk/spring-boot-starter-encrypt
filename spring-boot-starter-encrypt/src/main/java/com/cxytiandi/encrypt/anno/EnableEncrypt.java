package com.cxytiandi.encrypt.anno;

import com.cxytiandi.encrypt.auto.EncryptAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用加密Starter
 * 
 * <p>在Spring Boot启动类上加上此注解<p>
 * 
 * <pre class="code">
 * &#064;SpringBootApplication
 * &#064;EnableEncrypt
 * public class App {
 *     public static void main(String[] args) {
 *         SpringApplication.run(App.class, args);
 *     }
 * }
 * <pre>
 * 
 * @author yinjihuan
 *
 * @about http://cxytiandi.com/about
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EncryptAutoConfiguration.class})
public @interface EnableEncrypt {

}
