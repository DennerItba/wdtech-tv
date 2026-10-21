# My TV+ (Aplicativo Android TV Nativo)

Projeto nativo desenvolvido em **Kotlin** e **AndroidX Leanback**, estruturado com as tecnologias mais recentes do ecossistema Android para Smart TVs.

---

## 🛠️ Tecnologias Utilizadas
* **Interface de TV:** `androidx.leanback:leanback` (Navegação otimizada para controle remoto DPAD)
* **Player de Vídeo:** `androidx.media3:media3-exoplayer` e `media3-exoplayer-hls` (Transmissão de streams M3U8/HLS e MP4 de alta performance)
* **Carregamento de Imagens:** `io.coil-kt:coil` (Rápido, leve e com cache para logotipos e banners)
* **Rede:** `com.squareup.retrofit2:retrofit` (Consumo da API de canais e categorias)

---

## 🚀 Como Abrir e Compilar

### Opção 1: Via Android Studio (Recomendado)
1. Abra o **Android Studio**.
2. Clique em **File > Open...** e selecione a pasta:
   `C:\Users\Denner\OneDrive\Área de Trabalho\Nova pasta\MyTvApp`
3. Aguarde o Gradle sincronizar os pacotes.
4. Conecte um emulador de Android TV ou sua TV via ADB e clique no botão **Run (▶)**.

### Opção 2: Gerar o APK pelo Terminal (Gradle)
No diretório do projeto, execute:
```bash
# Compilar versão de teste (Debug):
./gradlew assembleDebug

# O APK será gerado em:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📺 Estrutura dos Arquivos Criados
* `app/src/main/AndroidManifest.xml`: Configurações de TV, banner e tela cheia.
* `MainActivity.kt`: Vitrine principal de canais com linhas e categorias.
* `PlayerActivity.kt`: Player Media3 ExoPlayer com controles nativos de TV.
* `CardPresenter.kt`: Renderizador dos cards de canais com foco visual do controle remoto.
* `ApiService.kt`: Interface Retrofit pronta para conectar ao seu servidor.
