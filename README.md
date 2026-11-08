# Territory Predictive Insights (GS 2º/2026 - POO)

Tema: **CitizenTech** | Recorte escolhido: **Clima local - chuva intensa e risco de alagamento**

## 1. Público beneficiado e decisão apoiada

**Quem sofre o problema:** moradores de pontos vulneráveis a alagamento (trechos de córrego, ruas baixas) e a associação de bairro / defesa civil local que os atende.

**Decisão que o sistema apoia:** "devemos avisar a comunidade, preparar abrigo e reforçar a limpeza de bueiros *hoje*?". O sistema lê sensores (chuva, nível do córrego, umidade do solo), prevê o valor das próximas leituras e emite alertas **explicados** (qual sensor, valor previsto, limite estourado). A decisão final continua sendo de uma pessoa.

O território é descrito por região e equipamento. Nenhum dado pessoal de pessoas reais é usado.

## 2. Como rodar

Requisitos: Java 17+ e Maven (ou abrir no IntelliJ).

```
mvn spring-boot:run      # roda o menu no console
mvn test                 # roda os testes
```

No IntelliJ: abra a pasta que contém o `pom.xml`, espere o Maven carregar e rode `TerritorioApplication`.

O banco é **H2 em arquivo** (pasta `dados/`, criada sozinha). Não precisa instalar nada, e o histórico sobrevive ao reiniciar. Para começar do zero, apague a pasta `dados/`.

**Roteiro rápido de teste no menu:** `2` (gerar 10 rodadas de leitura) → `4` (previsões e alertas) → `5` (histórico) → `6` (alertas) → feche e abra o programa → `5` de novo (os dados continuam lá).

## 3. Estrutura

| Pacote | Conteúdo |
|---|---|
| `dominio` | `ComponenteTerritorial` (abstrata) e `PontoMonitorado`; interface `Sensor` + 3 implementações; `DadosTerritorio`; `EstrategiaPrevisao` + 2 filhas; entidades `Medicao` e `Alerta` |
| `excecao` | `DadosInvalidosException`, `FonteIndisponivelException` |
| `persistencia` | `MedicaoRepository`, `AlertaRepository` (Spring Data JPA) |
| `servico` | `ServicoMonitoramento`, `FabricaPrevisao`, `ConfiguracaoSistema` |
| raiz | `SistemaMonitoramento` (menu), `TerritorioApplication` |

## 4. Decisões da equipe

- **Spring Boot + JPA** (em vez de JDBC): menos código repetido e o Spring gerencia as conexões.
- **H2 em arquivo**: roda na correção sem instalar banco.
- **Sensores simulados** com `Random` em faixas realistas (`SensorSimulado` concentra o código comum).
- **Duas estratégias** de previsão simples: média móvel das últimas N leituras e comparação do período recente com o anterior (projeta a tendência).
- **Limiares por tipo de sensor**, guardados em `DadosTerritorio` e acessados pelo `ConfiguracaoSistema`, porque cada sensor tem uma unidade diferente.

## 5. Padrões criacionais (justificativa)

- **Factory (`FabricaPrevisao` + enum `TipoPrevisao`)**: o serviço precisa de *todas* as estratégias, mas não deve saber qual classe concreta é criada. Cada valor do enum sabe criar a sua estratégia, então não há `if (tipo == ...)` espalhado, e adicionar uma terceira estratégia exige mexer só em um lugar.
- **Singleton (`ConfiguracaoSistema`)**: os limiares e a janela de previsão precisam ser **os mesmos** para o menu e para o serviço. Uma única instância evita configurações diferentes em partes diferentes do programa. Construtor privado, acesso por `getInstance()`.

## 6. Reflexão crítica

**Quando o sistema deve se calar?** Com menos de 5 leituras no banco, qualquer média é chute. Por isso `EstrategiaPrevisao` lança `DadosInvalidosException` e o menu mostra "Sem previsão" em vez de inventar um número. Um alerta falso também gasta a confiança da comunidade: se o sistema grita sem base, as pessoas deixam de ouvir.

**O que os dados não representam?** Um sensor por ponto não diz nada sobre a rua ao lado, e valores simulados com `Random` não têm relação com o clima real. A média também suaviza picos: uma chuva muito forte e curta pode alagar e passar despercebida. A previsão não considera relevo, drenagem nem previsão meteorológica oficial.

**Por que o alerta apoia a decisão da pessoa, em vez de decidir por ela?** Quem conhece o território (histórico de enchentes, obras, eventos locais) sabe coisas que o sistema não sabe. Por isso o alerta sempre explica o *porquê* (sensor, valor previsto, limite estourado), para que a pessoa possa concordar, discordar ou checar com outras fontes. O sistema sugere, a pessoa decide.

## 7. Testes

Testes JUnit 5 no padrão AAA em `src/test/java`: validação de `Medicao`, as duas estratégias, a fábrica, `DadosTerritorio`, o Singleton e a lógica de alertas do `ServicoMonitoramento` usando **Mockito** (repositories falsos, sem banco). Há testes com `assertThrows` para as exceções customizadas.

## 8. Uso de IA

> **PREENCHER pela equipe, com suas palavras:** onde a IA foi usada (ex.: tirar dúvidas sobre JPA, Mockito, estrutura do projeto, geração de código inicial), e **como o código foi revisado** (ex.: cada integrante leu e rodou o que ficou com sua parte, alterou e testou, consegue explicar cada classe).

## 9. Divisão de tarefas

> **PREENCHER:** quem ficou com domínio e exceções, persistência, previsões/alertas e testes.
