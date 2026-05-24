# Dados

Aplicativo Android nativo para rolar dados, feito em Kotlin com Jetpack Compose.

## Objetivo

Criar um rolador de dados simples, sem anúncios e sem rastreamento, para uso pessoal e de pessoas próximas. A interface do aplicativo é somente em português do Brasil.

## Recursos implementados

- Abre com 1 dado padrão de 1 a 6.
- Permite usar de 1 a 40 dados.
- Botões flutuantes na ordem: rolar, diminuir dados, aumentar dados e configurações.
- Dados em tela cheia quando há apenas 1 dado.
- Grade automática quando há vários dados.
- Intervalo padrão de valores entre 1 e 100.
- Intervalo individual por dado dentro das configurações.
- Pontos clássicos para valores de 1 a 6.
- Números para valores acima de 6 no modo automático.
- Modo somente números.
- Cores multicoloridas ou cor única.
- Animação de rolagem com velocidade lenta, normal e rápida.
- Tocar na tela para rolar.
- Agitar o celular para rolar.
- Som e vibração configuráveis.
- Total dos resultados configurável.

## Requisitos

- Android Studio instalado.
- JDK 17.
- Android SDK com plataforma Android 36 ou 36.1.
- Emulador Android ou aparelho físico com depuração USB.

Nesta máquina, o SDK Android foi encontrado em:

```text
C:\Users\thoma\AppData\Local\Android\Sdk
```

Se `adb` e `emulator` não estiverem no `PATH`, use os executáveis diretamente dentro do SDK ou rode o app pelo Android Studio.

## Como compilar

```powershell
.\gradlew.bat :app:assembleDebug
```

O APK de debug fica em:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## Como rodar testes automatizados

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Os testes automatizados cobrem a lógica principal: quantidade de dados, intervalos válidos, intervalo por dado, rolagem por faixa, total e padrão clássico dos pontos.

## Como abrir no Android Studio

1. Abra o Android Studio.
2. Escolha `Open`.
3. Selecione esta pasta do projeto.
4. Aguarde a sincronização do Gradle.
5. Escolha um emulador ou aparelho físico.
6. Clique em `Run`.

## Testes manuais finais

- Abrir o app e confirmar que aparece 1 dado grande de 1 a 6.
- Rolar pelo botão de dado.
- Tocar na área do dado e confirmar que rola quando a opção está ligada.
- Usar `+` até vários dados aparecerem em grade.
- Confirmar que `+` não passa de 40 dados.
- Usar `-` e confirmar que não fica abaixo de 1 dado.
- Abrir configurações e expandir cada seção.
- Alterar intervalo padrão para 1 a 20 e confirmar que valores acima de 6 aparecem como números.
- Alterar intervalo individual de um dado e confirmar que ele respeita a faixa própria.
- Alternar entre modo automático e somente números.
- Alternar entre multicolorido e cor única.
- Testar velocidades lenta, normal e rápida.
- Ligar/desligar total.
- Ligar/desligar som e vibração.
- Em aparelho físico, testar agitar para rolar.

## Specs do projeto

As decisões e etapas de implementação ficam em `docs/superpowers/specs/`.
