# DevManager Mobile 🚀

**DevManager Mobile** é a aplicação móvel nativa em Android para a plataforma SaaS **DevManager Web**, desenvolvida para gestão de projetos, clientes, orçamentos em PDF, funil de vendas (CRM Kanban) e controle financeiro de desenvolvedores freelancers.

> **Projeto Acadêmico - Faculdade Projeção**  
> **Curso:** Análise e Desenvolvimento de Sistemas (ADS)  
> **Disciplina:** Desenvolvimento Mobile  
> **Autores:** Yuri Assenção Gama & Edney dos Angelos Fontenele  

---

## 🛠️ Tecnogias e Stack Utilizada

* **Linguagem:** Kotlin 2.0.21
* **Interface Gráfica:** Jetpack Compose + Material Design 3
* **Arquitetura:** Clean Architecture + MVVM (Model-View-ViewModel)
* **Injeção de Dependências:** Hilt + KSP (Kotlin Symbol Processing)
* **Persistência Local (Offline-First):** Room Database 2.6.1
* **Rede & API REST:** Retrofit 2 + Kotlinx Serialization + OkHttp3
* **Navegação:** Navigation Compose
* **Assincronismo:** Kotlin Coroutines + StateFlow / Flow

---

## ✨ Funcionalidades Principais

- 🔐 **Autenticação & Perfis:** Suporte a perfis de **Desenvolvedor Admin** e **Cliente**.
- 📊 **Painel Financeiro:** Dashboard com gráficos em Canvas, faturas pendentes/atrasadas e projeção de receita.
- 🎯 **Funil Kanban:** Organização visual de leads e projetos (Contato Inicial, Negociação, Aguardando Aprovação, Fechado).
- 👥 **Gestão de Clientes:** CRUD completo de clientes com histórico de comunicação e inativação segura (RN003).
- 📄 **Orçamento em PDF:** Geração e visualização nativa de PDF (A4) do orçamento técnico para aprovação online.
- 🚀 **Entrega de Projetos:** Validação de entrega com inserção de URL de produção/repositório (RN004).

---

## 🔑 Credenciais para Demonstração (Demo)

* **Desenvolvedor Admin:**
  * **E-mail:** `admin@dev.com`
  * **Senha:** `123456`

* **Cliente:**
  * **E-mail:** `cliente@acme.com`
  * **Senha:** `123456`

---

## 📐 Arquitetura do Sistema

```
UI (Jetpack Compose / Screens / ViewModels)
    │
    ▼
Domain Layer (UseCases - Regras de Negócio RN001 a RN005)
    │
    ▼
Data Layer (DevManagerRepository)
    ├── Local: Room Database (Fonte Única da Verdade)
    └── Remote: Retrofit API (Sincronização em Background)
```

---

## 🚀 Como Executar o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/yurigama040407-droid/devmanager-sextafeira-mobile.git
   ```
2. Abra o projeto no **Android Studio (Ladybug / Iguana ou superior)**.
3. Aguarde a sincronização do Gradle.
4. Clique no botão **Run (▶)** no topo para compilar e instalar no emulador ou dispositivo físico.

---

© 2026 DevManager Mobile — Yuri Assenção Gama & Edney dos Angelos Fontenele
