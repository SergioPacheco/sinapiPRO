# Documentação — SinapiPRO

## Estrutura

```
docs/
├── levantamento-de-requisitos/
│   └── sinapiPRO.md          ← Regras de negócio do domínio (obras, orçamentos, SINAPI)
├── modelagem/
│   ├── [SinapiPRO]EER.mwb    ← Diagrama EER (MySQL Workbench)
│   └── [SinapiPRO]MER.mwb    ← Diagrama MER (MySQL Workbench)
└── requisitos/
    ├── [SinapiPRO] Requisitos.md              ← Requisitos funcionais completos
    ├── [SinapiPRO]Telas.md                    ← Especificação de telas
    ├── [SinapiPRO]Relatorios.md               ← Relatórios gerais
    ├── [SinapiPRO]Relatorios - Composicao.md  ← Relatórios de composições
    ├── [SinapiPRO]Relatorios - Insumos.md     ← Relatórios de insumos
    ├── [SinapiPRO]Relatorios - Insumos do Orçamento.md
    └── [SinapiPRO]Relatorios - Orcamento.md   ← Relatórios de orçamento
```

## Schema do Banco

O schema atual é gerenciado pelo **Flyway** — consulte as migrations em:

```
src/main/resources/db/migration/
├── V1–V13   Schema core
├── V14–V32  Módulos operacionais
├── V33–V34  Estoque e permissões
├── V35      Dados iniciais (admin)
└── V36      Dados de demonstração
```

## Modelagem

Para abrir os arquivos `.mwb` use o [MySQL Workbench](https://www.mysql.com/products/workbench/).
