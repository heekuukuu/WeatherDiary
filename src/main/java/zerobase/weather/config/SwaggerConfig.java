package zerobase.weather.config;//package zerobase.weather.config;//package zerobase.weather.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//
//   @Bean
//    public Docket api() {
//       return new Docket(DocumentationType.SWAGGER_2)
//               .select()
//                .apis(RequestHandlerSelectors.any())
//               .paths(PathSelectors.any())
//               .build().apiInfo(apiInfo());
//    }
//    private ApiInfo apiInfo() {
//       String description = "Welcome Log Company";
//        return new ApiInfoBuilder()
//                .title("SWAGGER TEST")
//               .description(description)
//                .version("1.0")
//                .build();    }
//}

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("날씨 일기 프로젝트")
                .description("날씨일기 CRUD 가능한 백엔드 API")
                .version("2.0");
    }
}