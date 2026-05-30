# GATE ClickBus — Simulador MVP

> Módulo inteligente de validação de tickets e gestão de fluxo de passageiros em terminais rodoviários.

---

## 📌 Sobre o Projeto

O **GATE ClickBus** nasceu de um problema real identificado nos terminais rodoviários:
aglomeração de passageiros nas plataformas, filas nos guichês e desorientação no embarque.

Este repositório contém o **MVP do simulador mobile**, desenvolvido em Android com Jetpack Compose,
que demonstra o funcionamento do motor de regras inteligente sem a necessidade de um GATE físico.

---

## 🎯 Objetivo do MVP

Provar que é possível controlar o fluxo de pessoas dentro de terminais rodoviários
usando apenas um aplicativo mobile — validando tickets e direcionando passageiros
com base em uma matriz de regras inteligente.

---

## 🧠 Motor de Regras

O coração do sistema é a **Matriz de Decisão**, que avalia o contexto do passageiro
e retorna uma decisão automaticamente:

| Tempo Restante (T) | Ocupação da Plataforma | Decisão |
|---|---|---|
| T < 0 min | Qualquer | 🔴 Realocação automática |
| T > 60 min | Qualquer | 🔵 Área de descanso |
| 0 ≤ T ≤ 60 min | Cheia | 🟠 Área de descanso (alivia fluxo) |
| 0 ≤ T ≤ 60 min | Vazia ou Normal | 🟢 Embarque imediato |

### Contexto do Motor (`ContextoGate`)

O motor não recebe variáveis soltas — ele recebe um **contexto rico** que pode ser expandido
sem quebrar nenhuma regra existente:

```kotlin
data class ContextoGate(
    val ticket: Ticket,
    val tempoRestante: Int,
    val horarioAtual: Int,
    val ocupacaoPlataforma: OcupacaoPlataforma
    // Futuras variáveis entram aqui:
    // val terminalLotado: Boolean
    // val clima: String
)
```

---

## 📱 Funcionalidades do MVP

- [x] Geração de tickets aleatórios (passageiro, rota, plataforma, horário)
- [x] Plataformas A, B, C e D com ocupação aleatória (Vazia / Normal / Cheia)
- [x] Motor de regras com matriz de decisão escalável
- [x] Resultado visual da triagem com cor indicadora
- [ ] Matriz de percepção (Tempo × Disponibilidade × Fluxo) — Sprint 2
- [ ] Painel do operador da rodoviária — Sprint 3
- [ ] Integração com API ClickBus — Sprint 4

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos

- Android Studio Hedgehog ou superior
- SDK mínimo: API 26 (Android 8.0)
- Kotlin 1.9+

### Passos

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/GateClickBus.git

# Abra no Android Studio
File → Open → selecione a pasta do projeto

# Rode no emulador
Shift + F10
```


