<h1 align="center">MyMusicPlayer</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-28%2B-brightgreen.svg?style=flat"/></a>
  <br>
  <a href="https://www.linkedin.com/in/lucas-mello-a43887188/"><img alt="Linkedin" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"/></a>
  <a href="mailto:lucasmellorodrigues2012@gmail.com"><img alt="Gmail" src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white"/></a>
</p>

<p align="center">  

⭐ Esse é um projeto de estudos para desenvolvimento Android nativo com kotlin.

🎵 Aplicativo reprodutor de áudios disponiveis no dispositivo do usuário.

</p>

## Download
 Faça o download da <a href="https://github.com/LucasMelll0/MyMusicPlayer/blob/master/apk/mymusicplayer.apk?raw=true">APK diretamente</a>. Você pode ver <a href="https://www.google.com/search?q=como+instalar+um+apk+no+android">aqui</a> como instalar uma APK no seu aparelho android.

## Tecnologias usadas e bibliotecas de código aberto

- Minimum SDK level 28
- [Linguagem Kotlin](https://kotlinlang.org/)

- Jetpack
  - Lifecycle: Observe os ciclos de vida do Android e manipule os estados da interface do usuário após as alterações do ciclo de vida.
  - ViewModel: Gerencia o detentor de dados relacionados à interface do usuário e o ciclo de vida. Permite que os dados sobrevivam a alterações de configuração, como rotações de tela.
  - ViewBinding: Liga os componentes do XML no Kotlin através de uma classe que garante segurança de tipo e outras vantagens.
 - Room: Biblioteca para persistência de dados local.
  - Custom Views: View customizadas feitas do zero usando XML.
  - Navigation Component: Para navegação do app.

- Arquitetura
  - MVVM (View - ViewModel - Model)
  - Comunicação da ViewModel com a View através de LiveData
  - Repositories para abstração da comunidação com a camada de dados.
  
- Bibliotecas
  - [Coil](https://github.com/coil-kt/coil): Para carregamento de imagens e cacheamento das mesmas.
  - [Glide](https://github.com/bumptech/glide) Para carregamento de imagens em notificações.
  - [Koin](https://insert-koin.io/): Para injeção de depêndencias.
  - [ExoPlayer](https://exoplayer.dev/): Para reprodução de midia.

## Arquitetura
**MyMusicPlayer** utiliza a arquitetura MVVM e o padrão de Repositories, que segue as [recomendações oficiais do Google](https://developer.android.com/topic/architecture).
</br></br>
<img width="100%" src="midia/architecture.png"/>
<br>

<a href="https://www.linkedin.com/posts/lucas-mello-a43887188_android-dev-services-activity-7051608775643832321--GHk/?utm_source=share&utm_medium=member_desktop">  Clique aqui para ver postagem demonstrando o app</a>


