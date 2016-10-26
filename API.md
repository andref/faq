API
===

- Na inclusão e alteração de objetos, o conteúdo do objeto vai no corpo da requisição, codificado como JSON
- Os objetos relacionados não são trazidos inteiros: somente um "rótulo" é incorporado:

    ```json
    {
        "id":"8d5aeb66-18b8-4f05-b319-08e3edc314fc",
        "titulo":"Java",
        "descricao":"Perguntas e respostas relacionadas à linguagem Java",
        "questoes": [
            {
                "id":"407e2cdb-da21-4193-8bcb-272f81b8a2ec",
                "descricao":"É necessário usar o modificador final em variáveis locais?"
            },
            {
                "id":"1a68633e-22ad-4d4a-be61-c064f40f7e4a",
                "descricao":"É possível escrever o pom.xml em groovy?"
            }
        ]
    }
    ```
- `POST` retorna o status `201 Created` informa onde ele pode ser encontrado usando o header `Location`
- `GET` e `PUT` retornam o status `200 OK` 
- `DELETE` retorna o status `204 No Content`
- Sempre que o item não existir, a API retorna `404 Not Found`

Questões
--------

- `GET    /questoes`  --- recupera todas as questões cadastradas
- `POST   /questoes/` --- inclui uma nova versão
- `GET    /questoes/{id}` --- recupera uma questão por ID
- `PUT    /questoes/{id}` --- altera uma questão
- `DELETE /questoes/{id}` --- remove uma questão

### Temas da Questão

- `GET    /questoes/{id}/temas` --- lista os temas associados à questão
- `POST   /questoes/{id}/temas` --- vincula um tema **existente** à lista.
- `DELETE /questoes/{id}/temas/{id}` --- desvincula um tema da lista

### Questões Relacionadas

- `GET    /questoes/{id}/rel` --- lista as questões relacionadas
- `POST   /questoes/{id}/rel` --- vincula uma questão relacionada
- `DELETE /questoes/{id}/rel/{id}` --- desvincula uma questão relacionada

Temas
-----

- `GET    /temas` --- lista todos os temas
- `POST   /temas/` ---  inclui um novo tema
- `GET    /temas/{id}` --- recupera um tema por ID
- `PUT    /temas/{id}` --- altera um tema
- `DELETE /temas/{id}` --- remove um tema

### Questões do Tema

- `GET    /temas/{id}/questoes` --- lista as questões relacionadas a este tema
