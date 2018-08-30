package ru.minesweeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableJpaRepositories(basePackages = "ru.minesweeper.storage")
@SpringBootApplication(scanBasePackages = "ru.minesweeper")
public class MinesweeperApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinesweeperApplication.class, args);
    }

    @Bean
    public ViewResolver viewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver("/WEB-INF/view/", ".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }
}
