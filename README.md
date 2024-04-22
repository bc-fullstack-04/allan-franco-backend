# allan-franco-backend
Projeto Backend - Allan Franco Mauricio

## How to run it()
- Abra o terminal e acesse o diretorio app-user-api
- Execute o comando: mvn install
- Acesse o diretorio app-integration-api e execute o comando novamente
- Execute o comando: docker.compose -f docker.composer.yml build
- Execute o comando: docker.compose -f docker.composer.yml up

**OBSERVAÇÃO**: As portas do docker são: "Aplicação = 8081" e "Integração = 8082"