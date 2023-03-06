    /*
    By default, Spring uses for @Transactional a bean of type TransactionManager / PlatformTransactionManager
    qualified with de name "transactionManager"

    ChainedTransactionManager deprecated, but alternative poorly documented.

    Transactions + mongo -> replica set is needed
     */

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
public class TransactionsConfig {



    @Bean("mongoTransaction")
    public MongoTransactionManager transactionManagerMongo(MongoDatabaseFactory dbFactory){
        return new MongoTransactionManager(dbFactory);
    }


    @Bean("transactionManager")
    public JpaTransactionManager transactionManagerJpa(){
        return new JpaTransactionManager();
    }

    @Bean("chainedTransactionManager")
    public PlatformTransactionManager chainedTransactionManager
            (@Qualifier("mongoTransaction") MongoTransactionManager mongoTransactionManager,
             @Qualifier("transactionManager") JpaTransactionManager jpaTransactionManager  ){

        ChainedTransactionManager chainedManager =
                new ChainedTransactionManager(jpaTransactionManager, mongoTransactionManager);

        //ChainedTransactionManager deprecated. TODO: use TransactionSynchronization

        return chainedManager;
    }
}
