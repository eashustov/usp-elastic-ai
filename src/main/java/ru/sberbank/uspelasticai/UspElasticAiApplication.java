package ru.sberbank.uspelasticai;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ru.sberbank.uspelasticai.ai.customcorpusword2vec.TestLogs;

@SpringBootApplication
public class UspElasticAiApplication {

	public static void main(String[] args) throws Exception {
//		SpringApplication.run(UspElasticAiApplication.class, args);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(UspElasticAiApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);

		String[] arguments = new String[] {"123"};
		ru.sberbank.uspelasticai.ai.customcorpusword2vec.TestLogs.main(arguments);
	}
}
