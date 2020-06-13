package dell.antonio

import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.data.mongodb.core.mapping.event.*
import org.springframework.validation.beanvalidation.*

@Configuration
class RepositoryConfiguration {
    @Bean
    fun localValidatorFactoryBean(): LocalValidatorFactoryBean {
        return LocalValidatorFactoryBean()
    }

    @Bean
    fun validatingMongoEventListener(
            @Qualifier("localValidatorFactoryBean")
            lfb: LocalValidatorFactoryBean): ValidatingMongoEventListener {
        return ValidatingMongoEventListener(lfb)
    }
}
