package com.easemob.serversslconnection.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;

@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.address}")
    public String address;

    @Value("${spring.rabbitmq.username}")
    public String username;

    @Value("${spring.rabbitmq.password}")
    public String password;

    @Value("${spring.rabbitmq.queue}")
    public String queueName;

    @Value("${spring.rabbitmq.exchange}")
    public String topicExchangeName;

    @Value("${spring.rabbitmq.routing-key}")
    public String routingKey;

    @Autowired
    private Environment env;

    @Bean
    RabbitConnectionFactoryBean connectionFactoryBean() throws IOException {
        RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
        if (Boolean.parseBoolean(env.getProperty("spring.rabbitmq.ssl.enabled"))){
            connectionFactoryBean.setUseSSL(true);
            connectionFactoryBean.setKeyStore(env.getProperty("spring.rabbitmq.ssl.key-store"));
            connectionFactoryBean.setKeyStorePassphrase(env.getProperty("spring.rabbitmq.ssl.key-store-password"));
            connectionFactoryBean.setTrustStore(env.getProperty("spring.rabbitmq.ssl.trust-store"));
            connectionFactoryBean.setTrustStorePassphrase(env.getProperty("spring.rabbitmq.ssl.trust-store-password"));
            connectionFactoryBean.setEnableHostnameVerification(Boolean.parseBoolean(env.getProperty("spring.rabbitmq.ssl.verify-hostname")));
        }
        return connectionFactoryBean;
    }

    @Bean
    ConnectionFactory connectionFactory(RabbitConnectionFactoryBean connectionFactoryBean){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(connectionFactoryBean.getRabbitConnectionFactory());
        connectionFactory.setAddresses(address);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    Queue queue(){
        return new Queue(queueName, true, false, false);
    }


    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    TopicExchange exchange() {
        return  new TopicExchange(topicExchangeName);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageListener(listenerAdapter);
        container.setQueueNames(queueName);
//        container.setAutoDeclare(true);
//        container.setMismatchedQueuesFatal(true);
//        container.start();
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
