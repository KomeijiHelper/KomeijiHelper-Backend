package komeiji.back.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
//        System.out.println("-------------");
//        System.out.println("springShopOpenAPI");
//        System.out.println("-------------");
        return new OpenAPI()
                .info(new Info().title("KomeijiHelper心理咨询")
                        .description("KomeijiHelper_API接口文档")
                        .version("v0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目地址")
                        .url("https://github.com/KirisameVanilla/KomeijiHelper"));
    }
}
