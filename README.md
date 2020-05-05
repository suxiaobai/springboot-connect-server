# Springboot uses ssl to connect components

| Components | Version | Remarks | 
| ---------- | ------- | ---- |
| springboot | 2.2.6.RELEASE | |
| rabbitmq   | 3.8.3 | |

## Rabbitmq

#### 1. Install rabbitmq
https://www.rabbitmq.com/download.html

#### 2. Manually Generating a CA, Certificates and Private Keys
https://www.rabbitmq.com/ssl.html
```
cd scripts/rabbitmq/
sh make_ssl.sh
# Enter the password twice
# Enter yes

# server use
server/private_key.pem
server/server_certificate.pem
testca/ca_certificate.pem

# java use
client/client_certificate.p12
keystore/rabbitstore
```

#### 3. Config rabbitmq 
```
# rabbitmq.conf
listeners.ssl.default = 5671

ssl_options.cacertfile = /etc/rabbitmq/ssl/ca_certificate.pem
ssl_options.certfile   = /etc/rabbitmq/ssl/server_certificate.pem
ssl_options.keyfile    = /etc/rabbitmq/ssl/private_key.pem
ssl_options.verify     = verify_peer
ssl_options.fail_if_no_peer_cert = false

# restart rabbitmq-server
```

#### 4. Config springboot
```
# address host must be your rabbitmq-server hostname
spring.rabbitmq.address=rabbitmq1:5671

spring.rabbitmq.ssl.enabled=true
spring.rabbitmq.ssl.key-store=client_certificate.p12
spring.rabbitmq.ssl.key-store-password=MySecretPassword
spring.rabbitmq.ssl.trust-store=rabbitstore
# the keystore password you set in step 2
spring.rabbitmq.ssl.trust-store-password=MySecretPassword
spring.rabbitmq.ssl.verify-hostname=true
```

#### 5. About hostname verify
If you cannot connect to rabbitmq-server with the host name
```
spring.rabbitmq.ssl.verify-hostname=false
```
