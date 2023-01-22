package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableMongoRepositories // not needed
public class TransactionsConfig {

    /*
    By defaul, Spring uses for @Transactional a bean of type TransactionManager / PlataformTransactionManager
    qualified with de name "transactionManager"
     */

    @Bean("mongoTransaction")
    public MongoTransactionManager transactionManagerMongo(MongoDatabaseFactory dbFactory){
        return new MongoTransactionManager(dbFactory);
    }


    @Bean("transactionManager")
    public JpaTransactionManager transactionManagerjpa(){
        return new JpaTransactionManager();
    }

    @Bean("chainedTransactionManager")
    public PlatformTransactionManager chainedTransactionManager
            (@Qualifier("mongoTransaction") MongoTransactionManager mongoTransactionManager,
             @Qualifier("transactionManager") JpaTransactionManager jpaTransactionManager  ){

        ChainedTransactionManager transactionManager =
                new ChainedTransactionManager(jpaTransactionManager, mongoTransactionManager);

        //ChainedTransactionManager deprecated. TODO: use TransactionSynchronization

        return transactionManager;
    }
}
