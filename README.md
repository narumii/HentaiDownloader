# HentaiDownloader

Simple solution that allows you to download your favourite images from various sites.

---
## How to

- Download [Java](https://adoptium.net/temurin/releases/?version=11)
- Download [HentaiDownloader](https://github.com/narumii/HentaiDownloader/releases)
- Run HentaiDownloader: `java -jar HentaiDownloader.jar`
- Input the required arguments into the application

---
## Booru search function
Sites based on booru have search functions, from now HentaiDownloader will be using that instead of rating and tags inputs.\
**Some sites may have different search functions, this is only simplified version of it**

- Tags: `loli pink_hair happy_sugar_life rape`
- Exclude Tags: `-happy_sugar_life -rape`
- Rating: `rating:general` `rating:safe` `rating:questionable` `rating:explicit`
- Score: `score:>100` `score:<100`

If you want to know more check those link may help you\
[GelBooru Source 1](https://gelbooru.com/index.php?page=wiki&s=view&id=25921)
[GelBooru Source 2](https://gelbooru.com/index.php?page=wiki&s=&s=view&id=26263)
[GelBooru Source 3](https://gelbooru.com/index.php?page=forum&s=view&id=4555) \
[DanBooru Documentation](https://danbooru.donmai.us/wiki_pages/help:cheatsheet)

---

### Supported sites

- [DanBooru](https://danbooru.donmai.us/)
- [GelBooru](https://gelbooru.com/)
- [Rule34](https://rule34.xxx/)
- [SafeBooru](https://safebooru.org/)
- [Yandere](https://yande.re/post)
- [Konachan](https://konachan.net/)
- [AllTheFallen (AFT)](https://booru.allthefallen.moe/)
- [BleachBooru](https://bleachbooru.org/)
- [LoliBooru](https://lolibooru.moe/)
- [Tbib](https://tbib.org/)
- [HypnoHub](https://hypnohub.net/)
---

- [EHentai](https://e-hentai.org/)
- [ExHentai](https://exhentai.org/) [`How to access`](https://f95zone.to/threads/how-to-access-exhentai-2021.76821/)

---

- [NHentai](https://nhentai.net/)
- [9Hentai](https://9hentai.to/)
- [IMHentai](https://imhentai.xxx/)

---

- [Nekos.Life](https://nekos.life/)

---

- [RealBooru](https://realbooru.com/)

---

### Explanation

- DanBooru: `You need to specify user api_key and user_id in order to allow HentaiDownloader work better` [User Options](https://gelbooru.com/index.php?page=account&s=options)
- GelBooru: `You need to specify user api_key and login in order to allow HentaiDownloader work better` [User Api Keys](https://danbooru.donmai.us/users/923254/api_keys)
- EXHentai `Since this site is blocked for some users you need to specify "ipb_member_id", "ipb_pass_hash", "igneous" cokkies` [Guide](https://f95zone.to/threads/how-to-access-exhentai-2021.76821/) [Guide 2](https://howtoaccessexhentai.wordpress.com/)
- NHentai: `They added some fucking shit so you need to specify: your user agent and "cf_chl_2", "cf_chl_prog", "cf_clearance", "csrftoken" cookies`

---

> Built on: [Java 11 (Adoptium)](https://adoptium.net/?variant=openjdk11&jvmVariant=hotspot)
>
`MultiThreading is bad`
