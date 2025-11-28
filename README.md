# 🛒 Minhas Compras

Aplicativo Android para gerenciamento de compras, controle de validade de produtos e estimativa de duração do estoque.

## 📱 Funcionalidades

### Gerenciamento de Compras
- ✅ Adicionar novas compras com data, loja e lista de produtos
- ✅ Visualizar histórico completo de compras
- ✅ Filtrar compras por data específica ou período
- ✅ Ver detalhes de cada compra

### Controle de Produtos
- ✅ Cadastrar produtos com nome, quantidade, unidade, preço
- ✅ Definir data de validade para produtos perecíveis
- ✅ Organizar por categorias (Alimentos, Limpeza, Higiene, etc.)

### Controle de Validade
- 🔴 Vermelho: produto vencido ou vence em até 3 dias
- 🟡 Amarelo: vence em até 7 dias
- 🟢 Verde: validade ok (mais de 7 dias)

### Estimativa de Duração do Estoque
- 📊 Cálculo automático baseado no consumo médio
- ⏱️ Visualização de dias restantes de estoque
- 🚨 Alertas quando o estoque está baixo

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Banco de Dados:** Room (SQLite)
- **Arquitetura:** MVVM
- **Navegação:** Navigation Compose

## 📋 Requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 34
- Dispositivo/Emulador com Android 8.0 (API 26) ou superior

## 🚀 Como Executar

1. Clone o repositório:
```bash
git clone https://github.com/BKB06/minhas-compras.git
```

2. Abra o projeto no Android Studio

3. Aguarde o Gradle sincronizar as dependências

4. Execute o app em um emulador ou dispositivo físico

## 📁 Estrutura do Projeto

```
app/src/main/java/com/minhascompras/
├── MainActivity.kt
├── MinhasComprasApplication.kt
├── data/
│   ├── database/
│   ├── model/
│   └── repository/
├── ui/
│   ├── components/
│   ├── navigation/
│   ├── screens/
│   └── theme/
└── util/
```

## 📄 Licença

Este projeto é de uso pessoal e educacional.

---

Desenvolvido com ❤️ para facilitar o controle de compras do dia a dia.