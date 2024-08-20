package com.crane.usercenterback.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Swagger配置文件
 *
 * @Author CraneResigned
 * @Date 2024/8/20 19:52:01
 */
@Configuration
@EnableSwagger2WebMvc
@Profile("dev")
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.crane.usercenterback.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    //基本信息，页面展示
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("用户中心")
                .description("用户中心接口文档")
                //联系人实体类
                .contact(
                        new Contact("Crane Resigned", "https://github.com/Crane-R/user-center-back", "crane.resigned@qq.com")
                )
                //版本号
                .version("1.0.0")
                .build();
    }
}