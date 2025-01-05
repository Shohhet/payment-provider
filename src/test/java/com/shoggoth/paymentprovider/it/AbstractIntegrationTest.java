package com.shoggoth.paymentprovider.it;

import com.shoggoth.paymentprovider.config.TestcontainersConfig;
import com.shoggoth.paymentprovider.entity.BankAccount;
import com.shoggoth.paymentprovider.entity.Merchant;
import com.shoggoth.paymentprovider.repository.*;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.shoggoth.paymentprovider.util.TestDataUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestcontainersConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AbstractIntegrationTest {

    protected Merchant persistMerchant;

    protected BankAccount persistBankAccount;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected ConnectionFactory connectionFactory;

    @Autowired
    protected BankAccountRepository bankAccountRepository;

    @Autowired
    protected MerchantRepository merchantRepository;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected PaymentCardRepository paymentCardRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    @BeforeEach
    public void clearDatabase(@Value("classpath:/sql/truncate_db.sql") Resource truncateDbScript) {

        runSqlScriptBlocking(truncateDbScript);

        var transientBankAccount = BankAccount.builder()
                .balance(INITIAL_ACCOUNT_AMOUNT)
                .currency(CURRENCY)
                .build();

        persistBankAccount = bankAccountRepository.save(transientBankAccount).block();

        var transientMerchant = Merchant.builder()
                .secretKey("{noop}" + SECRET_KEY)
                .bankAccountId(persistBankAccount.getId())
                .build();

        persistMerchant = merchantRepository.save(transientMerchant).block();
    }

    private void runSqlScriptBlocking(final Resource script) {

        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, script))
                .block();
    }
}
