currency-api — Microsserviços e Padrões de Resiliência



Aluno: Anderson Tavares

RA:1134877

Disciplina: Paradigmas de Linguagens de Programação — ATITUS

Professor: Luciano Ferretto



1\. Descrição do projeto



Este projeto implementa dois microsserviços em Java com Spring Boot: product-api e currency-api.



A product-api disponibiliza informações sobre produtos e consulta a currency-api para realizar conversões de moeda. Os serviços utilizam configurações de banco de dados e integração com Consul para descoberta de serviços.



2\. Padrões de resiliência



Fallback



Permite que a product-api retorne uma resposta alternativa quando ocorre uma falha na consulta de conversão. A implementação utiliza -1 como valor de convertedPrice no cenário de fallback.



Timeout



Define um tempo máximo de espera pela comunicação com o serviço de conversão, evitando que uma requisição fique aguardando indefinidamente.



Retry



Permite repetir chamadas que falham, de acordo com o número de tentativas e o intervalo configurados.



Circuit Breaker



Controla chamadas a um serviço que apresenta falhas recorrentes. Quando o circuito está aberto, novas chamadas podem ser bloqueadas temporariamente para evitar sobrecarregar o serviço.



Caching



Armazena resultados de conversões para permitir o reaproveitamento de respostas e reduzir chamadas repetidas à currency-api.



3\. Tecnologias utilizadas



Java



Spring Boot



Maven



PostgreSQL



Consul



Resilience4j



Caffeine Cache



4\. Execução do projeto



É necessário ter Java, Docker e os serviços de infraestrutura configurados no ambiente.



Iniciar a infraestrutura



Execute o script start\_consul.bat, localizado na pasta principal do projeto, conforme a configuração do ambiente.



Iniciar a currency-api



No terminal, acesse a pasta currency-api e execute:



mvnw.cmd spring-boot:run



Iniciar a product-api



Em outro terminal, acesse a pasta product-api e execute:



mvnw.cmd spring-boot:run



5\. Consulta de produtos



Com a product-api em execução, consulte:



http://localhost:8000/products?targetCurrency=BRL



O parâmetro targetCurrency informa a moeda de destino desejada para a conversão.



A currency-api utiliza a porta 8100, enquanto a product-api utiliza a porta 8000, conforme a configuração atual do projeto.



6\. Considerações finais



O projeto demonstra a aplicação de padrões de resiliência em uma arquitetura de microsserviços. Esses mecanismos ajudam a lidar com falhas de comunicação, limitar o tempo de espera, repetir chamadas, controlar falhas recorrentes e reutilizar resultados por meio de cache.

