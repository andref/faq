--
-- Versão 1.1.0
--
-- O tamanho da coluna resposta foi reduzido para 5000.
--

--
-- Cria uma categoria para as questões que tiveram a resposta cortada
--

insert into
    categoria (id, titulo, descricao)
values (
    X'31f60381d057481c8ac27c0314b5a232',
    'Resposta Cortada',
    'Estas questões tiveram sua resposta cortada após a migração p/ versão 1.1.0'
);

--
-- Vincula as questões com resposta > 5000 caracteres à categoria criada
--

insert into
    questao_categoria (questoes_id, categorias_id)
select
    q.id, X'31f60381d057481c8ac27c0314b5a232'
from
    questao q
where
    length(q.resposta) > 5000;

--
-- Reduz o tamanho das respostas
--

update
    questao
set
    resposta = substring(resposta, 0, 5000)
where
    length(resposta) > 5000;

--
-- Altera a tabela
--

alter table questao
alter column resposta varchar(5000) not null;
