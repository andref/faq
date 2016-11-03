--
-- Versão 1.0.0
--

create table categoria (
    id binary not null,
    descricao varchar(1000),
    titulo varchar(100) not null,
    primary key (id)
);

create table questao (
    id binary not null,
    autor varchar(500),
    datadepublicacao date not null,
    pergunta varchar(1000) not null,
    resposta varchar(10000) not null,
    versao integer not null,
    primary key (id)
);

create table questao_categoria (
    questoes_id binary not null,
    categorias_id binary not null
);

create table questao_relacionada (
    questao_id binary not null,
    questoesrelacionadas_id binary not null
);

alter table categoria
    add constraint uk_categoria_titulo unique (titulo);

alter table questao
    add constraint uk_questao_pergunta unique (pergunta);

alter table questao_categoria
    add constraint fk_questao_categoria_categoria
    foreign key (categorias_id)
    references categoria;

alter table questao_categoria
    add constraint fk_questao_categoria_questao
    foreign key (questoes_id)
    references questao;

alter table questao_relacionada
    add constraint fk_questao_relacionada_relacionada
    foreign key (questoesrelacionadas_id)
    references questao;

alter table questao_relacionada
    add constraint fk_questao_relacionada_questao
    foreign key (questao_id)
    references questao;
