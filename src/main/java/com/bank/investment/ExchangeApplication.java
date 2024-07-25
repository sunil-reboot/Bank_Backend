package com.bank.investment;

import com.bank.investment.repository.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;

@SpringBootApplication
@EnableTransactionManagement
@OpenAPIDefinition(info = @Info(title = "investment", version = "1.0.0"))
@EnableJpaRepositories
public class ExchangeApplication {

	@Autowired
	private UserRepository repository;

	public static void main(String[] args) throws KeyStoreException {
		Security.addProvider(new de.dentrassi.crypto.pem.PemKeyStoreProvider());
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		SpringApplication.run(ExchangeApplication.class, args);
	}

	@PostConstruct
	public void initUsers() {
	}

}
