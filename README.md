Para que serve:
Aplicaçao serve para ler arquivo Txt contendo lista de Usuarios que possuem Ordem de Pedidos e dentro das Ordens de pedidos temos os Produtos e seus valores, tratar os dados e salvar na base de dados.


Como utilizar:
Baixar o projet no Git

Utilizar com Intelij ou Eclipse

Importar o projeto

após importar o projeto 
execute vá até a pasta dele e abra um CMD e execute o comando  Mvn clean install  para baixar as dependencias bem como atualizar o projeto

após isso execute mvn spring-boot:run


a aplicaçao subirá e será acessivel pelo endereco
http://localhost:8080/upload.html



Na aplicacao selecione o arquivo texto data_1.txt ou data_2.txt

Como usar : Clicar na opção escolher arquivo ( selecione os arquivos mencionados)

pós carregar o arquivo clique em “Salvar na base”.

Feito as etapas anteriores será possível consultar usuario por ID
Consutlar todos os Usuarios e Consultar por intervalo de Datas.










Consultar o banco de dados da aplicacao, nota, só funciona com a aplicacao no ar posi 
é o banco em memória.


Padrões: Foi utilizado MVC com SpringBoot e front com Html e javascript
Banco de Dados H2, embarcado no Spring, somente em memória.
Relacionamento das entidades: Users , Products , Orders foram feitas respeitando os critérios do exemplo no desafio proposto.
