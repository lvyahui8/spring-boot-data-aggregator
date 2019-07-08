package io.github.lvyahui8.spring.example.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/8 22:04
 */
@Component
@ConfigurationProperties(prefix = "example")
@Data
public class ExampleProperties
{
    private boolean logging = false;
}
